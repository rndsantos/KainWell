package com.example.kainwell.domain

import com.example.kainwell.NutritionalIntakesEntity
import com.example.kainwell.data.food.Food
import com.example.kainwell.data.nutrient.NutritionalIntakesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.round

class OptimizeSelectedFoodItemsUseCase @Inject constructor(
    private val nutritionalIntakesRepository: NutritionalIntakesRepository,
) {
    private data class Solution(
        val finalTableau: Array<FloatArray>,
        val basicSolution: FloatArray,
        val optimizedValue: Float,
    )

    private class Simplex(
        private var tableau: Array<FloatArray>,
        private val isMax: Boolean = true,
    ) {
        private fun isSolutionOptimal(): Boolean {
            val lastRow = tableau.last()
            // returns true if there are no negative values in the last row (objective row) excluding the last column (Z)
            return lastRow.dropLast(1).all { it >= 0 }
        }

        // get the pivot column (most negative value in the objective row)
        private fun getPivotColumn(): Int {
            val lastRow = tableau.last().dropLast(1)
            return lastRow.indexOf(lastRow.minOrNull() ?: -1)
        }

        // get the pivot row using the minimum ratio test
        private fun getPivotRow(pivotCol: Int): Int {
            val ratios = Array(tableau.size - 1) { row ->
                val pivotElement = tableau[row][pivotCol]

                if (pivotElement <= 0) Float.POSITIVE_INFINITY
                else tableau[row].last() / pivotElement
            }

            if (ratios.all { it == Float.POSITIVE_INFINITY })
                return -1

            return ratios.withIndex().minByOrNull { it.value }?.index ?: -1
        }

        private fun pivot(pivotRow: Int, pivotCol: Int) {
            val pivotElement = tableau[pivotRow][pivotCol]
            val pivotRowValues = tableau[pivotRow]

            for (col in pivotRowValues.indices) {
                pivotRowValues[col] /= pivotElement
            }

            for (row in tableau.indices) {
                if (row == pivotRow) continue

                val multiplier = tableau[row][pivotCol]
                for (col in tableau[row].indices) {
                    tableau[row][col] -= multiplier * pivotRowValues[col]
                }
            }
        }

        private fun calculateBasicSolution(): FloatArray {
            var basicSolution = FloatArray(tableau[0].size - 1) { 0f }

            if (isMax) {
                val nonZeroVars = tableau.first().dropLast(1).indices.filter { col ->
                    tableau.indices.count { row -> tableau[row][col] == 1f } == 1 &&
                            tableau.indices.count { row -> tableau[row][col] != 0f } == 1
                }

                for (col in nonZeroVars) {
                    val row = tableau.indices.first { tableau[it][col] == 1f }
                    basicSolution[col] = tableau[row].last()
                }
            } else {
                val mutableBasicSolution = tableau.last().toMutableList()
                mutableBasicSolution.removeAt(mutableBasicSolution.lastIndex - 1)
                basicSolution = mutableBasicSolution.toFloatArray()
            }

            return basicSolution
        }

        fun solve(): Solution {
            while (!isSolutionOptimal()) {
                val pivotCol = getPivotColumn()
                if (pivotCol == -1) break // no valid pivot column, solution might be unbounded

                val pivotRow = getPivotRow(pivotCol)
                if (pivotRow == -1) break // no valid pivot row, solution might be infeasible

                pivot(pivotRow, pivotCol)
            }

            val basicSolution = calculateBasicSolution()
            val optimizedValue = tableau.last().last()
            return Solution(tableau, basicSolution, optimizedValue)
        }
    }

    /**
     * Extracts the food servings from the solution's basic solution.
     */
    private fun Solution.parseSolution(selectedFoodItemsSize: Int): FloatArray {
        val servings = FloatArray(selectedFoodItemsSize)

        System.arraycopy(
            basicSolution,
            basicSolution.size - selectedFoodItemsSize - 1,
            servings,
            0,
            selectedFoodItemsSize
        )

        return servings
    }

    private fun NutritionalIntakesEntity.toMinimumNutrientList(): List<Float> = listOf(
        minimum.calories,
        minimum.cholesterol,
        minimum.fat,
        minimum.sodium,
        minimum.carbohydrates,
        minimum.fiber,
        minimum.protein,
        minimum.vitA,
        minimum.vitC,
        minimum.calcium,
        minimum.iron
    )

    private fun NutritionalIntakesEntity.toMaximumNutrientList(): List<Float> = listOf(
        maximum.calories,
        maximum.cholesterol, maximum.fat,
        maximum.sodium,
        maximum.carbohydrates,
        maximum.fiber,
        maximum.protein,
        maximum.vitA,
        maximum.vitC,
        maximum.calcium,
        maximum.iron
    )

    private suspend fun createTableau(selectedFoodItems: List<Food>): Array<FloatArray> {
        val nutritionalIntakes = nutritionalIntakesRepository.getNutritionalIntakes()

        val minimumNutrientValues = nutritionalIntakes.toMinimumNutrientList()
        val maximumNutrientValues = nutritionalIntakes.toMaximumNutrientList()

        val numConstraints =
            minimumNutrientValues.size + maximumNutrientValues.size + selectedFoodItems.size
        val rows = selectedFoodItems.size + 1
        val columns = numConstraints + selectedFoodItems.size + 2
        val tableau = Array(rows) { FloatArray(columns) { 0f } }

        val costs = selectedFoodItems.map { it.price }.toFloatArray()

        for (i in selectedFoodItems.indices) {
            val selectedFoodNutrients = selectedFoodItems[i].nutrientsToList()

            for (j in selectedFoodNutrients.indices) {
                // decision variables
                tableau[i][j * 2] = selectedFoodNutrients[j]
                tableau[i][j * 2 + 1] = -selectedFoodNutrients[j]

                // bounds
                tableau[rows - 1][j * 2] = -minimumNutrientValues[j]
                tableau[rows - 1][j * 2 + 1] = maximumNutrientValues[j]
            }

            tableau[i][selectedFoodNutrients.size * 2 + i] = -1f
            tableau[rows - 1][selectedFoodNutrients.size * 2 + i] = 10f

            // slack variables
            tableau[i][numConstraints + i] = 1f
        }

        // objective function
        for (i in costs.indices) {
            tableau[i][columns - 1] = costs[i]
        }
        tableau[rows - 1][columns - 2] = 1f

        return tableau
    }

    suspend operator fun invoke(selectedFoodItems: List<Food>): List<Food> {
        return withContext(Dispatchers.IO) {
            val tableau = createTableau(selectedFoodItems)

            val simplex = Simplex(tableau, isMax = false)
            val optimizedFoodServings = simplex.solve().parseSolution(selectedFoodItems.size)

            optimizedFoodServings.zip(selectedFoodItems) { serving, food ->
                if (serving <= 0f) {
                    Food()
                } else
                    food.updateData(serving.round())
            }.filter {
                it.name.isNotEmpty()
            }
        }
    }
}

private fun multiplyServingSize(servingSize: String, serving: Float): String {
    val (servingValue, servingUnit) = servingSize.split(" ").let { parts ->
        parts[0].toFloat() to parts[1]
    }

    val calculatedServingSize = (servingValue * serving).round()
    return "$calculatedServingSize $servingUnit"
}

fun Float.round(decimalPlace: Int = 2): Float {
    val multiplier = 10f.pow(decimalPlace)
    return round(this * multiplier) / multiplier
}

fun Food.updateData(serving: Float): Food {
    return this.copy(
        serving = serving,
        servingSize = multiplyServingSize(servingSize, serving),
        calories = (calories * serving).round(),
        cholesterol = (cholesterol * serving).round(),
        fat = (fat * serving).round(),
        sodium = (sodium * serving).round(),
        carbohydrates = (carbohydrates * serving).round(),
        fiber = (fiber * serving).round(),
        protein = (protein * serving).round(),
        vitA = (vitA * serving).round(),
        vitC = (vitC * serving).round(),
        calcium = (calcium * serving).round(),
        iron = (iron * serving).round(),
        price = (price * serving).round(),
    )
}