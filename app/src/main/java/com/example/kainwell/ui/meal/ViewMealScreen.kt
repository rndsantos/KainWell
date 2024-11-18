@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.kainwell.ui.meal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kainwell.R
import com.example.kainwell.data.food.Food
import com.example.kainwell.ui.Dimensions.SmallPadding
import com.example.kainwell.ui.add_diet.AddDietUiState
import com.example.kainwell.ui.add_diet.AddDietViewModel
import com.example.kainwell.ui.common.composable.FoodImage
import com.example.kainwell.ui.common.composable.KainWellButton

@Composable
fun ViewMealScreen(
    onBack: () -> Unit,
    viewModel: AddDietViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        is AddDietUiState.Ready -> ViewMealScreenReady(
            selectedFoodItems = state.selectedFoodItems,
            onAddSelectedFoodItem = viewModel::onAddSelectedFoodItem,
            onRemoveSelectedFoodItem = viewModel::onRemoveSelectedFoodItem,
            onBack = onBack
        )

        else -> throw IllegalStateException("UI state found is not preloaded yet.")
    }
}

@Composable
fun ViewMealScreenReady(
    selectedFoodItems: Map<Food, Int>,
    onAddSelectedFoodItem: (Food) -> Unit,
    onRemoveSelectedFoodItem: (Food) -> Unit,
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
                }
            )
        },
        bottomBar = {
            ViewMealBottomAppBar()
        },
    ) { innerPadding ->
        ViewMealContent(
            selectedFoodItems = selectedFoodItems,
            onAddSelectedFoodItem = onAddSelectedFoodItem,
            onRemoveSelectedFoodItem = onRemoveSelectedFoodItem,
            innerPadding = innerPadding
        )
    }
}

@Composable
fun ViewMealBottomAppBar() {
    Surface(
        shape = MaterialTheme.shapes.large,
        shadowElevation = 5.dp,
        modifier = Modifier
            .height(90.dp)
    ) {
        KainWellButton(
            onClick = { /*TODO*/ }, backgroundGradient = listOf(
                Color(0xFFD4145A),
                Color(0xFFFBB03B)
            ),
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Optimize Diet",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
            )
        }
    }
}

@Composable
fun ViewMealContent(
    selectedFoodItems: Map<Food, Int>,
    onAddSelectedFoodItem: (Food) -> Unit,
    onRemoveSelectedFoodItem: (Food) -> Unit,
    innerPadding: PaddingValues,
) {
    LazyColumn(
        contentPadding = innerPadding,
        modifier = Modifier.fillMaxSize()
    ) {
        items(selectedFoodItems.entries.toList()) { foodQuantityPair ->
            ListItem(
                headlineContent = {
                    Text(
                        foodQuantityPair.key.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                supportingContent = {
                    FoodItemCounter(
                        quantity = foodQuantityPair.value,
                        onAdd = { onAddSelectedFoodItem(foodQuantityPair.key) },
                        onRemove = { onRemoveSelectedFoodItem(foodQuantityPair.key) },
                        modifier = Modifier
                            .padding(vertical = SmallPadding)
                            .clip(MaterialTheme.shapes.extraLarge)
                            .background(MaterialTheme.colorScheme.surfaceContainer)

                    )
                },
                leadingContent = {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.size(50.dp)
                    ) {
                        FoodImage(
                            imageUrl = foodQuantityPair.key.img,
                            contentDescription = foodQuantityPair.key.name
                        )
                    }
                },
                trailingContent = {
                    Text(
                        text = "$${foodQuantityPair.key.price}",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            )
        }
    }
}

@Composable
fun FoodItemCounter(
    quantity: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = SmallPadding)
                    .clickable {
                        onRemove()
                    }
            ) {
                if (quantity == 1)
                    Icon(imageVector = Icons.Outlined.Delete, contentDescription = "close")
                else
                    Icon(
                        painter = painterResource(R.drawable.ic_remove),
                        contentDescription = "close"
                    )
            }
            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = SmallPadding)
                    .clickable(
                        enabled = quantity < 10
                    ) {
                        onAdd()
                    }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "add",
                    tint = if (quantity == 10) MaterialTheme.colorScheme.surfaceDim else Color.Unspecified
                )
            }
        }
    }
}