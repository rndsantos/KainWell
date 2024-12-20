@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.kainwell.ui.add_diet.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Badge
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
import com.example.kainwell.ui.Dimensions.MediumPadding
import com.example.kainwell.ui.Dimensions.SmallPadding
import com.example.kainwell.ui.add_diet.AddDietUiState
import com.example.kainwell.ui.add_diet.AddDietViewModel
import com.example.kainwell.ui.components.ErrorScreen
import com.example.kainwell.ui.components.FoodImage
import com.example.kainwell.ui.components.KainWellBottomAppBar
import com.example.kainwell.ui.components.LoadingScreen
import com.example.kainwell.ui.components.MacronutrientValue

@Composable
fun ViewOptimizedDietScreen(
    onNavigateToHome: () -> Unit,
    onBack: () -> Unit,
    viewModel: AddDietViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        is AddDietUiState.Ready -> ViewOptimizedDietScreenReady(
            optimizedDiet = state.optimizedDiet,
            onAddDiet = viewModel::onAddDiet,
            onNavigateToHome = onNavigateToHome,
            onBack = {
                viewModel.loadData()
                onBack()
            }
        )

        is AddDietUiState.Loading -> LoadingScreen()

        is AddDietUiState.Error -> ErrorScreen(
            errorMessage = state.errorMessage,
            onBack = viewModel::onErrorBack
        )
    }
}

@Composable
private fun ViewOptimizedDietScreenReady(
    optimizedDiet: List<Food>,
    onAddDiet: () -> Unit,
    onNavigateToHome: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Optimized Diet", style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                },
            )
        },
        bottomBar = {
            KainWellBottomAppBar(onClick = {
                onAddDiet()
                onNavigateToHome()
            }, contentPadding = PaddingValues(MediumPadding)) {
                Text(
                    text = "Save Diet",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Black
                    ),
                )
            }
        },
    ) { innerPadding ->
        ViewOptimizedDietContent(
            optimizedDiet = optimizedDiet.toList(),
            innerPadding = innerPadding
        )
    }
}

@Composable
private fun ViewOptimizedDietContent(
    optimizedDiet: List<Food>,
    innerPadding: PaddingValues,
) {
    val isNonOptimal = optimizedDiet.any { food ->
        food.serving > 10f
    }

    LazyColumn(
        contentPadding = innerPadding,
        verticalArrangement = Arrangement.spacedBy(MediumPadding),
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = SmallPadding, horizontal = MediumPadding)
    ) {
        if (isNonOptimal)
            item {
                NonOptimalNote()
            }

        items(optimizedDiet) { food ->
            FoodListItem(food = food)
        }
    }
}

@Composable
private fun NonOptimalNote() {
    Surface(
        color = MaterialTheme.colorScheme.tertiary,
        shape = MaterialTheme.shapes.large,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(SmallPadding),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(SmallPadding)
        ) {
            Icon(imageVector = Icons.Filled.Info, contentDescription = null)
            Text(
                text = "This diet meets your daily intake, but may contain impractical servings.",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FoodListItem(
    food: Food,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = MaterialTheme.shapes.medium,
    ) {
        Box {
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
                        text = food.name,
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
            }
            Badge(
                containerColor = MaterialTheme.colorScheme.inverseSurface,
                modifier = Modifier
                    .padding(SmallPadding)
                    .align(Alignment.TopEnd)
            ) {
                Text(
                    text = food.servingSize,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}