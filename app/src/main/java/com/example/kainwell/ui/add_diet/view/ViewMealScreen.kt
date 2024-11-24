@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.kainwell.ui.add_diet.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kainwell.R
import com.example.kainwell.data.food.Food
import com.example.kainwell.ui.Dimensions.LargePadding
import com.example.kainwell.ui.Dimensions.MediumPadding
import com.example.kainwell.ui.Dimensions.SmallPadding
import com.example.kainwell.ui.add_diet.AddDietUiState
import com.example.kainwell.ui.add_diet.AddDietViewModel
import com.example.kainwell.ui.add_diet.SelectedFoodItemsCount
import com.example.kainwell.ui.common.composable.FoodImage
import com.example.kainwell.ui.common.composable.KainWellButton
import com.example.kainwell.ui.common.composable.LoadingScreen
import com.example.kainwell.ui.common.composable.MacronutrientValue

@Composable
fun ViewMealScreen(
    onNavigateToViewOptimizedDiet: () -> Unit,
    onBack: () -> Unit,
    viewModel: AddDietViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        is AddDietUiState.Ready -> ViewMealScreenReady(
            selectedFoodItems = state.selectedFoodItems,
            optimizedDiet = state.optimizedDiet,
            onNavigateToViewOptimizedDiet = onNavigateToViewOptimizedDiet,
            onAddSelectedFoodItem = viewModel::onAddSelectedFoodItem,
            onRemoveSelectedFoodItem = viewModel::onRemoveSelectedFoodItem,
            onOptimizeMeal = viewModel::onOptimizeMeal,
            onBack = onBack
        )

        is AddDietUiState.Loading -> LoadingScreen()

        else -> throw IllegalStateException("UI state found is not preloaded yet.")
    }
}

@Composable
fun ViewMealScreenReady(
    selectedFoodItems: List<Food>,
    optimizedDiet: List<Food>,
    onNavigateToViewOptimizedDiet: () -> Unit,
    onAddSelectedFoodItem: (Food) -> Unit,
    onRemoveSelectedFoodItem: (Food) -> Unit,
    onOptimizeMeal: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Meal", style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Outlined.Close, contentDescription = "close")
                    }
                },
                actions = {
                    SelectedFoodItemsCount(
                        size = selectedFoodItems.size,
                    )
                }
            )
        },
        bottomBar = {
            ViewMealBottomAppBar(
                onOptimizeMeal = {
                    onOptimizeMeal()
                    onNavigateToViewOptimizedDiet()
                }
            )
        },
    ) { innerPadding ->
        ViewMealContent(
            selectedFoodItems = selectedFoodItems.toList(),
            onAddSelectedFoodItem = onAddSelectedFoodItem,
            onRemoveSelectedFoodItem = onRemoveSelectedFoodItem,
            innerPadding = innerPadding
        )
    }
}

@Composable
fun ViewMealBottomAppBar(
    onOptimizeMeal: () -> Unit,
) {
    Surface(
        shadowElevation = 5.dp,
    ) {
        KainWellButton(
            onClick = onOptimizeMeal,
            containerColor = MaterialTheme.colorScheme.inverseSurface,
            shape = MaterialTheme.shapes.large.copy(
                topStart = MaterialTheme.shapes.large.bottomStart,
                topEnd = MaterialTheme.shapes.large.bottomEnd
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Optimize Diet",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(LargePadding)
                    .navigationBarsPadding()
            )
        }
    }
}

@Composable
fun ViewMealContent(
    selectedFoodItems: List<Food>,
    onAddSelectedFoodItem: (Food) -> Unit,
    onRemoveSelectedFoodItem: (Food) -> Unit,
    innerPadding: PaddingValues,
) {
    LazyColumn(
        contentPadding = innerPadding,
        modifier = Modifier.fillMaxSize()
    ) {
        items(selectedFoodItems) { food ->
            FoodListItem(food = food, onRemoveSelectedFoodItem = onRemoveSelectedFoodItem)
        }
    }
}

@Composable
fun FoodListItem(
    food: Food,
    onRemoveSelectedFoodItem: (Food) -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.padding(vertical = SmallPadding, horizontal = MediumPadding)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(MediumPadding),
        ) {
            Surface(
                modifier = Modifier.size(120.dp)
            ) {
                FoodImage(
                    imageUrl = food.img,
                    contentDescription = food.name
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    food.name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    MacronutrientValue(value = "${food.calories} kcal") {
                        Icon(
                            painter = painterResource(R.drawable.ic_calories),
                            contentDescription = "protein",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(SmallPadding),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MacronutrientValue(value = "${food.protein}g") {
                            Icon(
                                painter = painterResource(R.drawable.ic_protein),
                                contentDescription = "protein",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        MacronutrientValue(value = "${food.carbohydrates}g") {
                            Icon(
                                painter = painterResource(R.drawable.ic_carbs),
                                contentDescription = "carbs",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        MacronutrientValue(value = "${food.fat}g") {
                            Icon(
                                painter = painterResource(R.drawable.ic_fats),
                                contentDescription = "fats",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            IconButton(onClick = {
                onRemoveSelectedFoodItem(food)
            }) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "close"
                )
            }
        }
    }
}

//@Composable
//fun FoodItemCounter(
//    quantity: Int,
//    onAdd: () -> Unit,
//    onRemove: () -> Unit,
//    modifier: Modifier = Modifier,
//) {
//    Box(modifier) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween,
//            modifier = Modifier.padding(vertical = 4.dp)
//        ) {
//            Box(
//                modifier = Modifier
//                    .padding(horizontal = SmallPadding)
//                    .clickable {
//                        onRemove()
//                    }
//            ) {
//                if (quantity == 1)
//                    Icon(imageVector = Icons.Outlined.Delete, contentDescription = "close")
//                else
//                    Icon(
//                        painter = painterResource(R.drawable.ic_remove),
//                        contentDescription = "close"
//                    )
//            }
//            Text(
//                text = quantity.toString(),
//                style = MaterialTheme.typography.titleSmall.copy(
//                    fontWeight = FontWeight.Bold
//                ),
//                modifier = Modifier.padding(horizontal = 10.dp)
//            )
//            Box(
//                modifier = Modifier
//                    .padding(horizontal = SmallPadding)
//                    .clickable(
//                        enabled = quantity < 10
//                    ) {
//                        onAdd()
//                    }
//            ) {
//                Icon(
//                    imageVector = Icons.Outlined.Add,
//                    contentDescription = "add",
//                    tint = if (quantity == 10) MaterialTheme.colorScheme.surfaceDim else Color.Unspecified
//                )
//            }
//        }
//    }
//}