@file:OptIn(
    ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)

package com.example.kainwell.ui.add_diet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composables.core.DragIndication
import com.composables.core.ModalBottomSheet
import com.composables.core.ModalBottomSheetState
import com.composables.core.Scrim
import com.composables.core.Sheet
import com.composables.core.SheetDetent.Companion.FullyExpanded
import com.composables.core.SheetDetent.Companion.Hidden
import com.composables.core.rememberModalBottomSheetState
import com.example.kainwell.data.food.Food
import com.example.kainwell.ui.Dimensions.ExtraLargePadding
import com.example.kainwell.ui.Dimensions.LargePadding
import com.example.kainwell.ui.Dimensions.MediumPadding
import com.example.kainwell.ui.Dimensions.SmallPadding
import com.example.kainwell.ui.common.composable.ErrorScreen
import com.example.kainwell.ui.common.composable.FoodImage
import com.example.kainwell.ui.common.composable.FoodItemCard
import com.example.kainwell.ui.common.composable.KainWellButton
import com.example.kainwell.ui.common.composable.LoadingScreen
import com.example.kainwell.ui.common.ext.bottomBorder
import com.example.kainwell.ui.common.ext.titlecase
import kotlinx.coroutines.launch


@Composable
fun AddDietScreen(
    navigateToViewMeal: () -> Unit,
    viewModel: AddDietViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        is AddDietUiState.Loading -> LoadingScreen()
        is AddDietUiState.Error -> ErrorScreen(state.errorMessage)
        is AddDietUiState.Ready -> AddDietScreenReady(
            foodItems = state.foodItems,
            selectedFoodItems = state.selectedFoodItems,
            onAddSelectedFoodItem = viewModel::onAddSelectedFoodItem,
            onRemoveSelectedFoodItem = viewModel::onRemoveSelectedFoodItem,
            onResetSelectedFoodItems = viewModel::onResetSelectedFoodItems,
            navigateToViewMeal = navigateToViewMeal,
        )
    }

}

@Composable
fun AddDietScreenReady(
    foodItems: Map<String, List<Food>>,
    selectedFoodItems: List<Food>,
    onAddSelectedFoodItem: (Food) -> Unit,
    onRemoveSelectedFoodItem: (Food) -> Unit,
    onResetSelectedFoodItems: () -> Unit,
    navigateToViewMeal: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val sheetState = rememberModalBottomSheetState(initialDetent = Hidden)
    var query by remember {
        mutableStateOf(TextFieldValue(""))
    }
    var selectedCategory by remember {
        mutableIntStateOf(0)
    }
    var selectedFood by remember {
        mutableStateOf(Food())
    }

    Scaffold(
        topBar = {
            Column(
                Modifier.statusBarsPadding()
            ) {
                SearchBar(
                    query = query,
                    onQueryChange = { query = it },
                    modifier = Modifier.padding(
                        start = MediumPadding,
                        end = MediumPadding
                    )
                )
                CategoryList(
                    categories = foodItems.keys.toList(),
                    selectedCategoryIndex = selectedCategory,
                    onCategorySelected = {
                        selectedCategory = it
                        coroutineScope.launch {
                            lazyListState.animateScrollToItem(selectedCategory)
                        }
                    },
                    modifier = Modifier.padding(
                        start = SmallPadding,
                        end = SmallPadding,
                        top = LargePadding
                    )
                )
                HorizontalDivider()
            }
        },
        bottomBar = {
            if (selectedFoodItems.isEmpty())
                return@Scaffold

            AddDietBottomAppBar(
                selectedFoodItems = selectedFoodItems,
                onResetMeal = onResetSelectedFoodItems,
                onNavigateToViewMeal = {
                    navigateToViewMeal()
                }
            )
        },
    ) { innerPadding ->
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = MediumPadding)
        ) {
            items(
                items = foodItems.entries.toList(),
                key = { it.key }
            ) { foodCategory ->
                FoodItemCategory(
                    category = foodCategory.key,
                    foodItems = foodCategory.value,
                    selectedFoodItems = selectedFoodItems,
                    onFoodClick = { food ->
                        selectedFood = food
                        sheetState.currentDetent = FullyExpanded
                    },
                    onAddSelectedFoodItem = onAddSelectedFoodItem,
                    onRemoveSelectedFoodItem = onRemoveSelectedFoodItem,
                    modifier = Modifier.padding(top = MediumPadding)
                )
            }
        }

        FoodDetailBottomSheet(
            food = selectedFood,
            selected = selectedFoodItems.contains(selectedFood),
            sheetState = sheetState,
            onClick = {
                if (selectedFoodItems.contains(selectedFood))
                    onRemoveSelectedFoodItem(selectedFood)
                else
                    onAddSelectedFoodItem(selectedFood)

                sheetState.currentDetent = Hidden
            },
            onDismiss = {
                sheetState.currentDetent = Hidden
            }
        )
    }
}


@Composable
private fun AddDietBottomAppBar(
    selectedFoodItems: List<Food>,
    onResetMeal: () -> Unit,
    onNavigateToViewMeal: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
        shadowElevation = 5.dp,
        modifier = Modifier
            .height(95.dp)
    ) {
        ViewMealButton(
            selectedFoodItems = selectedFoodItems,
            onResetMeal = onResetMeal,
            onNavigateToViewMeal = onNavigateToViewMeal,
        )
    }
}

@Composable
private fun ViewMealButton(
    selectedFoodItems: List<Food>,
    onResetMeal: () -> Unit,
    onNavigateToViewMeal: () -> Unit,
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    Surface(
        color = MaterialTheme.colorScheme.inverseSurface,
        contentColor = MaterialTheme.colorScheme.inverseOnSurface,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .padding(
                start = MediumPadding,
                end = MediumPadding,
                bottom = ExtraLargePadding,
                top = MediumPadding
            )
            .clickable(
                onClick = onNavigateToViewMeal,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = null
            )
            .indication(interactionSource, ripple())

    ) {
        Box {
            SelectedFoodItemsCount(selectedFoodItems.size)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = SmallPadding, horizontal = MediumPadding)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.width(200.dp)
                ) {
                    Text(
                        text = "View your meal",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = selectedFoodItems.joinToString { it.name },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.7f),
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            IconButton(
                onClick = onResetMeal, modifier = Modifier
                    .padding(12.dp)
                    .size(25.dp)
                    .aspectRatio(1f)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(imageVector = Icons.Outlined.Clear, contentDescription = "clear meal")
            }
        }
    }
}

@Composable
fun SelectedFoodItemsCount(
    size: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(10.dp)
            .size(25.dp)
            .background(Color.Transparent)
            .border(1.dp, MaterialTheme.colorScheme.onPrimary, CircleShape)
            .clip(CircleShape)
            .aspectRatio(1f)
    ) {
        Text(
            text = size.toString(),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun CategoryList(
    categories: List<String>,
    selectedCategoryIndex: Int,
    onCategorySelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(LargePadding),
        modifier = modifier
    ) {
        itemsIndexed(categories) { index, category ->
            Box(
                modifier = Modifier
                    .clickable {
                        onCategorySelected(index)
                    }
                    .padding(SmallPadding)
            ) {
                Text(
                    text = category.titlecase(),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.then(
                        if (index == selectedCategoryIndex) Modifier.bottomBorder(
                            2.dp,
                            MaterialTheme.colorScheme.inverseSurface
                        ) else Modifier
                    )
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
    ) {
        Box(
            Modifier.fillMaxSize()
        ) {
            if (query.text.isEmpty()) {
                SearchHint()
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                    singleLine = true,
                    maxLines = 1,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = MediumPadding)
                )
            }
        }
    }
}


@Composable
private fun SearchHint() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize()
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = "search"
        )
        Spacer(Modifier.width(SmallPadding))
        Text(
            text = "Search KainWell",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Stable
@Composable
fun FoodItemCategory(
    category: String,
    foodItems: List<Food>,
    selectedFoodItems: List<Food>,
    onFoodClick: (Food) -> Unit,
    onAddSelectedFoodItem: (Food) -> Unit,
    onRemoveSelectedFoodItem: (Food) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(vertical = SmallPadding)
            .fillMaxWidth()
    ) {
        Text(
            text = category.titlecase(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
        )
        FlowRow(
            maxItemsInEachRow = 2,
            horizontalArrangement = Arrangement.spacedBy(MediumPadding),
            verticalArrangement = Arrangement.spacedBy(MediumPadding),
            modifier = Modifier
                .padding(vertical = MediumPadding)
        ) {
            foodItems.map { foodItem ->
                FoodItemCard(
                    food = foodItem,
                    selected = selectedFoodItems.contains(foodItem),
                    onClick = {
                        onFoodClick(foodItem)
                    },
                    onAddFoodItem = {
                        onAddSelectedFoodItem(foodItem)
                    },
                    onRemoveFoodItem = {
                        onRemoveSelectedFoodItem(foodItem)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.4785f)
                )
            }
        }
    }
}

@Composable
fun FoodDetailBottomSheet(
    food: Food,
    selected: Boolean,
    sheetState: ModalBottomSheetState,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        state = sheetState,
        onDismiss = onDismiss
    ) {
        Scrim()
        Sheet(
            modifier = Modifier
                .shadow(4.dp, MaterialTheme.shapes.medium)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxHeight(0.8f)
            ) {
                DragIndication(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .background(
                            MaterialTheme.colorScheme.outlineVariant,
                            RoundedCornerShape(100)
                        )
                        .width(48.dp)
                        .height(4.dp)
                        .zIndex(1f)
                )
                Column {
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
                            Text(
                                text = food.servingSize,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Spacer(Modifier.height(SmallPadding))
                        NutritionalValuesGrid(food)
                    }
                }
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(SmallPadding),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(MediumPadding)
                            .navigationBarsPadding()
                    ) {
                        KainWellButton(
                            onClick = onClick,
                            containerColor = MaterialTheme.colorScheme.inverseSurface,
                            contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                            contentPadding = PaddingValues(
                                horizontal = ExtraLargePadding,
                                vertical = SmallPadding
                            ),
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (selected) "Remove from meal" else "Add to meal",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
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

@Composable
fun NutritionalValuesGrid(food: Food) {
    data class Nutrient(val name: String, val value: String)

    val nutrients = listOf(
        Nutrient("Calories", "${food.calories}"),
        Nutrient("Protein", "${food.protein} g"),
        Nutrient("Carbs", "${food.carbohydrates} g"),
        Nutrient("Fat", "${food.fat} g"),
        Nutrient("Fiber", "${food.fiber} g"),
        Nutrient("Sodium", "${food.sodium} mg"),
        Nutrient("Vit A", "${food.vitA} IU"),
        Nutrient("Vit C", "${food.vitC} IU"),
        Nutrient("Calcium", "${food.calcium} mg"),
        Nutrient("Iron", "${food.iron} mg"),
        Nutrient("Cholesterol", "${food.cholesterol} mg")
    )

    FlowRow(
        maxItemsInEachRow = 4,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .padding(MediumPadding)
            .fillMaxWidth()
    ) {
        nutrients.map { nutrient ->
            NutrientCard(
                name = nutrient.name,
                value = nutrient.value,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


