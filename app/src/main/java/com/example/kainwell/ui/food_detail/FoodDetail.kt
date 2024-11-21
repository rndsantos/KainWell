@file:OptIn(ExperimentalLayoutApi::class)

package com.example.kainwell.ui.food_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kainwell.data.food.Food
import com.example.kainwell.ui.Dimensions.ExtraLargePadding
import com.example.kainwell.ui.Dimensions.LargePadding
import com.example.kainwell.ui.Dimensions.MediumPadding
import com.example.kainwell.ui.Dimensions.SmallPadding
import com.example.kainwell.ui.add_diet.AddDietUiState
import com.example.kainwell.ui.add_diet.AddDietViewModel
import com.example.kainwell.ui.common.composable.FoodImage
import com.example.kainwell.ui.common.composable.KainWellButton
import com.example.kainwell.ui.meal.FoodItemCounter

@Composable
fun FoodDetail(
    onBack: () -> Unit,
    addDietViewModel: AddDietViewModel = hiltViewModel(),
    viewModel: FoodDetailViewModel = hiltViewModel(),
) {
    val food by viewModel.food
    val addDietUiState by addDietViewModel.uiState.collectAsStateWithLifecycle()

    when (val state = addDietUiState) {
        is AddDietUiState.Ready -> FoodDetailContent(
            food = food,
            selectedFoods = state.selectedFoodItems,
            onSelectedFoodItemsChange = addDietViewModel::onSelectedFoodItemsChange,
            onBack = onBack
        )

        else -> throw IllegalStateException("UI state found is not preloaded yet.")
    }
}

@Composable
fun FoodDetailContent(
    food: Food,
    selectedFoods: Map<Food, Int>,
    onSelectedFoodItemsChange: (Map<Food, Int>) -> Unit,
    onBack: () -> Unit,
) {
    var selectedFoodItems by remember {
        mutableStateOf(selectedFoods)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxHeight(0.8f)
            .clip(MaterialTheme.shapes.small),
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                shape = MaterialTheme.shapes.small,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(SmallPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MediumPadding)
                ) {
                    FoodItemCounter(
                        quantity = selectedFoodItems.getOrDefault(
                            food,
                            0
                        ),
                        onAdd = {
                            selectedFoodItems = selectedFoodItems.toMutableMap().apply {
                                this[food] = this.getOrDefault(food, 0) + 1
                            }
                        },
                        onRemove = {
                            selectedFoodItems = selectedFoodItems.toMutableMap().apply {
                                if (this[food] == 1) {
                                    this.remove(food)

                                    onSelectedFoodItemsChange(this)
                                    onBack()

                                    return@apply
                                }

                                this[food] = this.getOrDefault(food, 0) - 1
                            }
                        })
                    KainWellButton(
                        onClick = {
                            onSelectedFoodItemsChange(
                                selectedFoodItems
                            )
                            onBack()
                        },
                        containerColor = MaterialTheme.colorScheme.inverseSurface,
                        contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                        contentPadding = PaddingValues(
                            horizontal = ExtraLargePadding,
                            vertical = SmallPadding
                        ),
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Add to meal", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding)
        ) {
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                FoodImage(
                    imageUrl = food.img,
                    contentDescription = food.name,
                    modifier = Modifier
                        .aspectRatio(1.67f)
                )
                Column(
                    modifier = Modifier.padding(MediumPadding)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = food.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "$${food.price}",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                        Text(text = food.servingSize, style = MaterialTheme.typography.bodyLarge)
                    }
                    Spacer(Modifier.height(SmallPadding))
                    FlowRow(
                        maxItemsInEachRow = 4,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .padding(MediumPadding)
                            .fillMaxWidth()
                    ) {
                        NutrientCard(
                            name = "Calories",
                            value = "${food.calories}",
                            modifier = Modifier.weight(1f)
                        )
                        NutrientCard(
                            name = "Fat",
                            value = "${food.fat} g",
                        )
                        NutrientCard(
                            name = "Carbs",
                            value = "${food.carbohydrates} g",
                        )
                        NutrientCard(
                            name = "Protein",
                            value = "${food.protein} g",
                            modifier = Modifier.weight(1f)
                        )
                        NutrientCard(
                            name = "Fiber",
                            value = "${food.fiber} g",
                        )
                        NutrientCard(
                            name = "Sodium",
                            value = "${food.sodium} mg",
                            modifier = Modifier.weight(1f)
                        )
                        NutrientCard(
                            name = "Vit A",
                            value = "${food.vitA} IU",
                            modifier = Modifier.weight(1f)
                        )
                        NutrientCard(name = "Vit C", value = "${food.vitC} IU")
                        NutrientCard(
                            name = "Calcium",
                            value = "${food.calcium} mg",
                            modifier = Modifier.weight(1f)
                        )
                        NutrientCard(
                            name = "Iron",
                            value = "${food.iron} mg",
                            modifier = Modifier.weight(1f)
                        )
                        NutrientCard(
                            name = "Cholesterol",
                            value = "${food.cholesterol} mg",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NutrientCard(
    name: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 1.dp,
        shape = MaterialTheme.shapes.extraLarge,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(MediumPadding)

        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(LargePadding))
            Text(text = value, style = MaterialTheme.typography.bodySmall)
        }
    }
}