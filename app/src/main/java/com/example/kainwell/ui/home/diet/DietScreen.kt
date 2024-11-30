package com.example.kainwell.ui.home.diet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composables.core.SheetDetent.Companion.FullyExpanded
import com.composables.core.SheetDetent.Companion.Hidden
import com.composables.core.rememberModalBottomSheetState
import com.example.kainwell.R
import com.example.kainwell.data.food.Food
import com.example.kainwell.ui.Dimensions.MediumPadding
import com.example.kainwell.ui.Dimensions.SmallPadding
import com.example.kainwell.ui.components.ErrorScreen
import com.example.kainwell.ui.components.FoodDetailBottomSheet
import com.example.kainwell.ui.components.FoodImage
import com.example.kainwell.ui.components.LoadingScreen
import com.example.kainwell.ui.components.MacronutrientValue
import com.example.kainwell.ui.utils.isDarkMode
import kotlin.math.min

@Composable
fun DietScreen(
    modifier: Modifier = Modifier,
    viewModel: DietViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        is DietUiState.Loading -> LoadingScreen()
        is DietUiState.Ready -> DietScreenReady(
            savedDiets = state.savedDiets,
            onEditTitle = viewModel::onEditDietTile,
            modifier = modifier,
        )

        is DietUiState.Error -> ErrorScreen(
            errorMessage = state.errorMessage,
            onBack = viewModel::onBack
        )
    }
}

@Composable
fun DietScreenReady(
    savedDiets: List<Diet>,
    onEditTitle: (Diet, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(initialDetent = Hidden)
    var selectedFood by remember {
        mutableStateOf(Food())
    }

    Column(
        modifier.padding(MediumPadding)
    ) {
        Text(
            text = "Saved Diets", style = MaterialTheme.typography.titleLarge.copy(
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
                    DietListItem(
                        diet = diet,
                        onFoodClick = { food ->
                            selectedFood = food
                            sheetState.currentDetent = FullyExpanded
                        },
                        onEditTitle = onEditTitle,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    FoodDetailBottomSheet(
        food = selectedFood,
        sheetState = sheetState,
        onClick = {
            sheetState.currentDetent = Hidden
        },
        onDismiss = {
            sheetState.currentDetent = Hidden
        }
    )
}

@Composable
private fun DietListItem(
    diet: Diet,
    onFoodClick: (Food) -> Unit,
    onEditTitle: (Diet, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dialogState = remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(
        pageCount = {
            diet.foodItems.size
        }
    )

    Surface(
        color = if (isDarkMode(LocalContext.current)) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
        shadowElevation = if (isDarkMode(LocalContext.current)) 0.dp else 4.dp,
        modifier = modifier.height(200.dp)
    ) {
        Box {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.inverseSurface,
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
                    Text(
                        text = diet.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            dialogState.value = true
                        }
                    )
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
                                tint = MaterialTheme.colorScheme.primary,
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
                            return (availableSpace - 2 * pageSpacing) / min(3, diet.foodItems.size)
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
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    onFoodClick(foodItem)
                                }
                        ) {
                            FoodImage(
                                imageUrl = foodItem.img,
                                contentDescription = foodItem.name,
                                modifier = Modifier
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
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 10.sp
                                    ),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (dialogState.value)
        EditDietTitleDialog(
            diet = diet,
            onDoneEditing = { newTitle ->
                onEditTitle(diet, newTitle)
                dialogState.value = false
            }, onDismiss = {
                dialogState.value = false
            }
        )
}

@Composable
private fun EditDietTitleDialog(
    diet: Diet,
    onDoneEditing: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var dietTitle by remember {
        mutableStateOf(diet.name)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.padding(MediumPadding)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(SmallPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(MediumPadding)
            ) {
                TextField(
                    value = dietTitle,
                    onValueChange = {
                        dietTitle = it
                    },
                    label = {
                        Text(
                            text = "Change diet name",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text
                    ),
                    singleLine = true,
                    maxLines = 1,
                    colors = TextFieldDefaults.colors().copy(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,

                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,

                        cursorColor = MaterialTheme.colorScheme.onPrimaryContainer,

                        errorContainerColor = MaterialTheme.colorScheme.errorContainer,
                        errorCursorColor = MaterialTheme.colorScheme.onErrorContainer,
                        errorIndicatorColor = MaterialTheme.colorScheme.onErrorContainer,

                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        disabledIndicatorColor = MaterialTheme.colorScheme.primary,
                    ),
                    modifier = Modifier.padding(SmallPadding)
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    TextButton(onClick = {
                        onDoneEditing(dietTitle)
                    }) {
                        Text(
                            text = "Done",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
