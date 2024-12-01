package com.example.kainwell.ui.home.gallery

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.composables.core.SheetDetent.Companion.FullyExpanded
import com.composables.core.SheetDetent.Companion.Hidden
import com.composables.core.rememberModalBottomSheetState
import com.example.kainwell.data.food.Food
import com.example.kainwell.ui.components.ErrorScreen
import com.example.kainwell.ui.components.FoodDetailBottomSheet
import com.example.kainwell.ui.components.LoadingScreen

@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        is GalleryUiState.Loading -> LoadingScreen()
        is GalleryUiState.Error -> ErrorScreen(state.errorMessage)
        is GalleryUiState.Ready -> GalleryScreenReady(state.foodItems)
    }
}

@Composable
private fun GalleryScreenReady(
    foodItems: List<Food>,
) {
    val sheetState = rememberModalBottomSheetState(initialDetent = Hidden)
    var selectedFood by remember {
        mutableStateOf(Food())
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier.fillMaxSize()
    ) {
        items(foodItems) { food ->
            AsyncImage(
                model = food.img,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .border(1.dp, MaterialTheme.colorScheme.surface)
                    .fillMaxWidth()
                    .clickable {
                        selectedFood = food
                        sheetState.currentDetent = FullyExpanded
                    }
            )
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