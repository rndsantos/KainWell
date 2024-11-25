package com.example.kainwell.ui.home.diet

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kainwell.R
import com.example.kainwell.ui.Dimensions.MediumPadding
import com.example.kainwell.ui.Dimensions.SmallPadding
import com.example.kainwell.ui.common.composable.ErrorScreen
import com.example.kainwell.ui.common.composable.FoodImage
import com.example.kainwell.ui.common.composable.LoadingScreen
import com.example.kainwell.ui.common.composable.MacronutrientValue
import com.example.kainwell.ui.utils.isDarkMode

@Composable
fun DietScreen(
    modifier: Modifier = Modifier,
    viewModel: DietViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        is DietUiState.Loading -> LoadingScreen()
        is DietUiState.Success -> DietScreenReady(
            savedDiets = state.savedDiets,
            modifier = modifier,
        )

        is DietUiState.Error -> ErrorScreen(
            errorMessage = state.errorMessage,
        )
    }
}

@Composable
fun DietScreenReady(
    savedDiets: List<Diet>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.padding(MediumPadding)
    ) {
        Text(
            text = "Saved Diets", style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Black
            )
        )
        Spacer(Modifier.height(SmallPadding))
        if (savedDiets.isEmpty()) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "No saved diets yet",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(MediumPadding),
                contentPadding = PaddingValues(SmallPadding)
            ) {
                items(savedDiets) { diet ->
                    DietListItem(diet = diet, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
fun DietListItem(
    diet: Diet,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(
        pageCount = {
            diet.foodItems.size
        }
    )

    Surface(
        color = if (isDarkMode(LocalContext.current)) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
        shadowElevation = if (isDarkMode(LocalContext.current)) 0.dp else 4.dp,
        modifier = modifier
    ) {
        Box {
            Badge(
                containerColor = MaterialTheme.colorScheme.inverseSurface,
                modifier = Modifier
                    .padding(SmallPadding)
                    .align(Alignment.TopEnd)
            ) {
                Text(
                    text = "$${diet.totalCost}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(4.dp)
                )
            }

            Column {
                Column(
                    verticalArrangement = Arrangement.spacedBy(SmallPadding),
                    modifier = Modifier
                        .padding(MediumPadding)
                ) {
                    Text(text = "Macronutrients", style = MaterialTheme.typography.titleMedium)
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        MacronutrientValue(value = "${diet.totalCalories} kcal") {
                            Icon(
                                painter = painterResource(R.drawable.ic_calories),
                                contentDescription = "protein",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        MacronutrientValue(value = "${diet.totalProtein}g") {
                            Icon(
                                painter = painterResource(R.drawable.ic_protein),
                                contentDescription = "protein",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        MacronutrientValue(value = "${diet.totalCarbohydrates}g") {
                            Icon(
                                painter = painterResource(R.drawable.ic_carbs),
                                contentDescription = "carbs",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        MacronutrientValue(value = "${diet.totalFat}g") {
                            Icon(
                                painter = painterResource(R.drawable.ic_fats),
                                contentDescription = "fats",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    val threePagesPerViewport = object : PageSize {
                        override fun Density.calculateMainAxisPageSize(
                            availableSpace: Int,
                            pageSpacing: Int,
                        ): Int {
                            return (availableSpace - 2 * pageSpacing) / 3
                        }
                    }

                    HorizontalPager(
                        state = pagerState,
                        pageSize = threePagesPerViewport,
                        snapPosition = SnapPosition.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) { page ->
                        val foodItem = diet.foodItems[page]

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            FoodImage(
                                imageUrl = foodItem.img,
                                contentDescription = foodItem.name,
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .background(Color.Black)
                                    .alpha(0.5f)
                            )

                            Column(
                                Modifier.padding(MediumPadding)
                            ) {
                                Text(
                                    text = foodItem.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White
                                )
                                Text(
                                    text = foodItem.servingSize,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}