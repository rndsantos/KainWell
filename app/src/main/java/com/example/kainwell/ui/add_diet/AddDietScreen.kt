@file:OptIn(
    ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)

package com.example.kainwell.ui.add_diet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.requiredWidthIn
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composables.core.SheetDetent.Companion.FullyExpanded
import com.composables.core.SheetDetent.Companion.Hidden
import com.composables.core.rememberModalBottomSheetState
import com.example.kainwell.R
import com.example.kainwell.data.food.Food
import com.example.kainwell.ui.Dimensions.LargePadding
import com.example.kainwell.ui.Dimensions.MediumPadding
import com.example.kainwell.ui.Dimensions.SmallPadding
import com.example.kainwell.ui.components.ErrorScreen
import com.example.kainwell.ui.components.FoodDetailBottomSheet
import com.example.kainwell.ui.components.FoodItemCard
import com.example.kainwell.ui.components.KainWellBottomAppBar
import com.example.kainwell.ui.components.LoadingScreen
import com.example.kainwell.ui.components.VerticalGrid
import kotlinx.coroutines.launch
import java.util.Locale


@Composable
fun AddDietScreen(
    navigateToViewMeal: () -> Unit,
    onBack: () -> Unit,
    viewModel: AddDietViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        is AddDietUiState.Loading -> LoadingScreen()
        is AddDietUiState.Error -> ErrorScreen(state.errorMessage, onBack = onBack)
        is AddDietUiState.Ready -> AddDietScreenReady(
            foodItems = state.foodItems,
            selectedFoodItems = state.selectedFoodItems,
            onQueryChange = viewModel::onQueryChange,
            onAddSelectedFoodItem = viewModel::onAddSelectedFoodItem,
            onRemoveSelectedFoodItem = viewModel::onRemoveSelectedFoodItem,
            onSelectAllFoodItems = viewModel::onSelectAllFoodItems,
            onResetSelectedFoodItems = viewModel::onResetSelectedFoodItems,
            onBack = onBack,
            navigateToViewMeal = navigateToViewMeal,
        )
    }

}

@Composable
private fun AddDietScreenReady(
    foodItems: Map<String, List<Food>>,
    selectedFoodItems: List<Food>,
    onQueryChange: (String) -> Unit,
    onAddSelectedFoodItem: (Food) -> Unit,
    onRemoveSelectedFoodItem: (Food) -> Unit,
    onSelectAllFoodItems: (List<Food>) -> Unit,
    onResetSelectedFoodItems: () -> Unit,
    onBack: () -> Unit,
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

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .collect {
                selectedCategory = it
            }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
            ) {
                Spacer(modifier = Modifier.height(SmallPadding))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                    SearchBar(
                        query = query,
                        onQueryChange = {
                            query = it
                            onQueryChange(it.text)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        onSelectAllFoodItems(foodItems.values.flatten())
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_select_all),
                            contentDescription = "back",
                        )
                    }
                }
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
                        top = MediumPadding
                    )
                )
                HorizontalDivider()
            }
        },
        bottomBar = {
            AnimatedVisibility(visible = selectedFoodItems.isNotEmpty()) {
                KainWellBottomAppBar(
                    onClick = navigateToViewMeal,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    AddDietBottomAppBarContent(
                        selectedFoodItems = selectedFoodItems,
                        onResetMeal = onResetSelectedFoodItems
                    )
                }
            }
        },
    ) { innerPadding ->
        if (foodItems.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center, modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        LargePadding
                    )
            ) {
                Text(
                    text = "Unknown food \"${query.text}\"",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else
            LazyColumn(
                state = lazyListState,
                contentPadding = innerPadding,
                modifier = Modifier
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
private fun AddDietBottomAppBarContent(
    selectedFoodItems: List<Food>,
    onResetMeal: () -> Unit,
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
                modifier = Modifier
                    .requiredHeightIn(max = 45.dp)
                    .requiredWidthIn(max = 200.dp)
                    .weight(1f)
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
            Icon(
                imageVector = Icons.Outlined.Clear,
                contentDescription = "clear meal"
            )
        }
    }
}

@Composable
private fun CategoryList(
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
                    modifier = Modifier
                        .then(
                            if (index == selectedCategoryIndex) Modifier.bottomBorder(
                                2.dp,
                                MaterialTheme.colorScheme.inverseSurface,
                                SmallPadding
                            ) else Modifier
                        )
                )
            }
        }
    }
}

private fun Modifier.bottomBorder(strokeWidth: Dp, color: Color, space: Dp) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        Modifier.drawBehind {
            val width = size.width
            val height = size.height + space.toPx() - strokeWidthPx / 2

            drawLine(
                color = color,
                start = Offset(x = 0f, y = height),
                end = Offset(x = width, y = height),
                strokeWidth = strokeWidthPx
            )
        }
    }
)

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

@Composable
private fun FoodItemCategory(
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
        VerticalGrid(
            horizontalSpacing = SmallPadding,
            verticalSpacing = SmallPadding,
            modifier = Modifier
                .padding(vertical = MediumPadding)
        ) {
            foodItems.forEach { foodItem ->
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
                )
            }
        }
    }
}

private fun String.titlecase(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
}


