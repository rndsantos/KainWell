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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import com.example.kainwell.ui.Dimensions.LargePadding
import com.example.kainwell.ui.Dimensions.MediumPadding
import com.example.kainwell.ui.Dimensions.SmallPadding
import com.example.kainwell.ui.add_diet.AddDietUiState
import com.example.kainwell.ui.add_diet.AddDietViewModel
import com.example.kainwell.ui.common.composable.FoodImage
import com.example.kainwell.ui.common.composable.KainWellButton
import com.example.kainwell.ui.common.composable.LoadingScreen
import com.example.kainwell.ui.common.composable.MacronutrientValue

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
            onBack = onBack
        )

        is AddDietUiState.Loading -> LoadingScreen()

        else -> throw IllegalStateException("UI state found is not preloaded yet.")
    }
}

@Composable
fun ViewOptimizedDietScreenReady(
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
            ViewOptimizedDietBottomAppBar(
                onAddDiet = {
                    onAddDiet()
                    onNavigateToHome()
                }
            )
        },
    ) { innerPadding ->
        ViewOptimizedDietContent(
            optimizedDiet = optimizedDiet.toList(),
            innerPadding = innerPadding
        )
    }
}

@Composable
fun ViewOptimizedDietBottomAppBar(
    onAddDiet: () -> Unit,
) {
    Surface(
        shadowElevation = 5.dp,
    ) {
        KainWellButton(
            onClick = onAddDiet,
            containerColor = MaterialTheme.colorScheme.inverseSurface,
            shape = MaterialTheme.shapes.large.copy(
                topStart = MaterialTheme.shapes.large.bottomStart,
                topEnd = MaterialTheme.shapes.large.bottomEnd
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Save Diet",
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
fun ViewOptimizedDietContent(
    optimizedDiet: List<Food>,
    innerPadding: PaddingValues,
) {
    LazyColumn(
        contentPadding = innerPadding,
        modifier = Modifier.fillMaxSize()
    ) {
        items(optimizedDiet) { food ->
            FoodListItem(food = food)
        }
    }
}

@Composable
fun FoodListItem(
    food: Food,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.padding(vertical = SmallPadding, horizontal = MediumPadding)
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