package com.example.kainwell.ui.add_diet

//import org.jetbrains.kotlinx.multik.api.math.argMin
//import org.jetbrains.kotlinx.multik.api.mk
//import org.jetbrains.kotlinx.multik.api.ndarray
//import org.jetbrains.kotlinx.multik.ndarray.data.D2
//import org.jetbrains.kotlinx.multik.ndarray.data.NDArray
//import org.jetbrains.kotlinx.multik.ndarray.data.get
//import org.jetbrains.kotlinx.multik.ndarray.data.set
//import org.jetbrains.kotlinx.multik.ndarray.operations.all
//import org.jetbrains.kotlinx.multik.ndarray.operations.count
//import org.jetbrains.kotlinx.multik.ndarray.operations.div
//import org.jetbrains.kotlinx.multik.ndarray.operations.indexOfFirst
//import org.jetbrains.kotlinx.multik.ndarray.operations.mapIndexed
//import org.jetbrains.kotlinx.multik.ndarray.operations.minus
//import org.jetbrains.kotlinx.multik.ndarray.operations.times
//
//class Simplex(
//    private var tableau: NDArray<Float, D2>, // Use Multik NDArray for the tableau
//    private val isMax: Boolean = true,
//) {
//
//    // Check if the tableau is optimal
//    private fun isOptimal(): Boolean {
//        // The last row excluding the last column (Z column)
//        val lastRow = tableau[tableau.shape[0] - 1, 0 until tableau.shape[1] - 1]
//        // Check if all elements are >= 0
//        return lastRow.all { it >= 0f }
//    }
//
//    // Get the pivot column (most negative value in the objective row)
//    private fun getPivotColumn(): Int {
//        val lastRow = tableau[tableau.shape[0] - 1, 0 until tableau.shape[1] - 1]
//        return lastRow.argMin()
//    }
//
//    // Get the pivot row using the minimum ratio test
//    private fun getPivotRow(pivotCol: Int): Int {
//        val lastColumn = tableau[0 until tableau.shape[0] - 1, tableau.shape[1] - 1]
//        val pivotColumn = tableau[0 until tableau.shape[0] - 1, pivotCol]
//        val ratios = lastColumn / pivotColumn
//
//        // Ignore non-positive ratios
//        val validRatios = ratios.mapIndexed { i, value ->
//            if (pivotColumn[i] <= 0f || value.isNaN() || value.isInfinite()) Float.POSITIVE_INFINITY else value
//        }
//
//        return validRatios.argMin()
//    }
//
//    // Perform the pivot operation
//    private fun pivot(pivotRow: Int, pivotCol: Int) {
//        // Normalize the pivot row
//        val pivotElement = tableau[pivotRow, pivotCol]
//        tableau[pivotRow] = tableau[pivotRow] / pivotElement
//
//        // Eliminate all other rows
//        for (row in 0 until tableau.shape[0]) {
//            if (row == pivotRow) continue
//            val multiplier = tableau[row, pivotCol]
//            tableau[row] -= tableau[pivotRow] * multiplier
//        }
//    }
//
//    // Run the simplex algorithm
//    fun solve(): Solution {
//        while (!isOptimal()) {
//            val pivotCol = getPivotColumn()
//            val pivotRow = getPivotRow(pivotCol)
//            pivot(pivotRow, pivotCol)
//        }
//
//        // Extract the basic solution
//        val basicSolution = FloatArray(tableau.shape[1] - 1) { 0f }
//        for (col in 0 until tableau.shape[1] - 1) {
//            val column = tableau[0 until tableau.shape[0], col]
//            if (column.count { it == 1f } == 1 && column.count { it != 0f } == 1) {
//                val row = column.indexOfFirst { it == 1f }
//                basicSolution[col] = tableau[row, tableau.shape[1] - 1]
//            }
//        }
//
//        val optimalValue = tableau[tableau.shape[0] - 1, tableau.shape[1] - 1]
//        return Solution(tableau, basicSolution, optimalValue)
//    }
//
//    // Solution data class
//    data class Solution(
//        val finalTableau: NDArray<Float, D2>,
//        val basicSolution: FloatArray,
//        val optimalValue: Float,
//    )
//}
//
//fun main() {
//    val tableau = mk.ndarray(
//        arrayOf(
//            floatArrayOf(6.0F, 14.0F, 1.0F, 0.0F, 0.0F, 0.0F, 800.0F),
//            floatArrayOf(1000.0F, 2000.0F, 0.0F, 1.0F, 0.0F, 0.0F, 120000.0F),
//            floatArrayOf(1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 95.0F),
//            floatArrayOf(-4000.0F, -5000.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F)
//        )
//    )
//
//    val simplex = Simplex(tableau)
//    val solution = simplex.solve()
//
//    println("Basic Solution: ${solution.basicSolution.contentToString()}")
//    println("Optimized Value: ${solution.optimalValue}")
//}
