@file:OptIn(ExperimentalLayoutApi::class)

package com.example.kainwell.ui.add_diet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kainwell.data.food.Food
import com.example.kainwell.ui.Dimensions.ExtraLargePadding
import com.example.kainwell.ui.Dimensions.LargePadding
import com.example.kainwell.ui.Dimensions.MediumPadding
import com.example.kainwell.ui.Dimensions.SmallPadding
import com.example.kainwell.ui.common.composable.ErrorScreen
import com.example.kainwell.ui.common.composable.FoodItemCard
import com.example.kainwell.ui.common.composable.LoadingScreen
import java.util.Locale

@Composable
fun AddDietScreen(
    onFoodClick: (String, String) -> Unit,
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
            onFoodClick = onFoodClick,
            onAddSelectedFoodItem = viewModel::onAddSelectedFoodItem,
            onRemoveSelectedFoodItem = viewModel::onRemoveSelectedFoodItem,
            navigateToViewMeal = navigateToViewMeal,
        )
    }

}

@Composable
fun AddDietScreenReady(
    foodItems: Map<String, List<Food>>,
    selectedFoodItems: Map<Food, Int>,
    onFoodClick: (String, String) -> Unit,
    onAddSelectedFoodItem: (Food) -> Unit,
    onRemoveSelectedFoodItem: (Food) -> Unit,
    navigateToViewMeal: () -> Unit,
) {
    var query by remember {
        mutableStateOf(TextFieldValue(""))
    }
    var selectedCategory by remember {
        mutableStateOf("")
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
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
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
                onNavigateToViewMeal = {
                    navigateToViewMeal()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = MediumPadding)
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {
                foodItems.entries.map { foodCategory ->
                    FoodItemCategory(
                        category = foodCategory.key,
                        foodItems = foodCategory.value,
                        selectedFoodItems = selectedFoodItems,
                        onFoodClick = onFoodClick,
                        onAddSelectedFoodItem = onAddSelectedFoodItem,
                        onRemoveSelectedFoodItem = onRemoveSelectedFoodItem,
                        modifier = Modifier.padding(top = MediumPadding)
                    )
                }
            }
        }
    }
}


@Composable
private fun AddDietBottomAppBar(
    selectedFoodItems: Map<Food, Int>,
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
            onNavigateToViewMeal = onNavigateToViewMeal
        )
    }
}

@Composable
private fun ViewMealButton(
    selectedFoodItems: Map<Food, Int>,
    onNavigateToViewMeal: () -> Unit,
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    Surface(
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
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
            Surface(
                color = Color.Transparent,
                shape = CircleShape,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary),
                modifier = Modifier
                    .padding(10.dp)
                    .size(25.dp)
                    .aspectRatio(1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = selectedFoodItems.values.sum().toString(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
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
                        text = selectedFoodItems.entries.joinToString {
                            "${it.value} ${it.key.name}"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryList(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(LargePadding),
        modifier = modifier
    ) {
        items(categories) { category ->
            Box(
                modifier = Modifier
                    .clickable {
                        onCategorySelected(category)
                    }
                    .padding(SmallPadding)
            ) {
                Text(
                    text = category.titlecase(),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.then(
                        if (category == selectedCategory) Modifier.bottomBorder(
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

fun String.titlecase(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
}

fun Modifier.bottomBorder(strokeWidth: Dp, color: Color) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        Modifier.drawBehind {
            val width = size.width
            val height = size.height + SmallPadding.toPx() - strokeWidthPx / 2

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
fun FoodItemCategory(
    category: String,
    foodItems: List<Food>,
    selectedFoodItems: Map<Food, Int>,
    onFoodClick: (String, String) -> Unit,
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
                    name = foodItem.name,
                    price = foodItem.price,
                    imageUrl = foodItem.img,
                    quantity = selectedFoodItems.getOrDefault(foodItem, 0),
                    onClick = {
                        onFoodClick(foodItem.id, foodItem.category)
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

