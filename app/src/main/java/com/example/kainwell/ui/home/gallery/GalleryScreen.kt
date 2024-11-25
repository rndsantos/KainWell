package com.example.kainwell.ui.home.gallery

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.kainwell.data.food.Food
import com.example.kainwell.ui.common.composable.ErrorScreen
import com.example.kainwell.ui.common.composable.LoadingScreen

@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        is HomeScreenUiState.Loading -> LoadingScreen()
        is HomeScreenUiState.Error -> ErrorScreen(state.errorMessage)
        is HomeScreenUiState.Ready -> GalleryScreenReady(state.foodItems)
    }
}

@Composable
fun GalleryScreenReady(
    foodItems: List<Food>,
) {
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
                    .border(1.dp, MaterialTheme.colorScheme.inverseSurface)
                    .fillMaxWidth()
            )
        }
    }
}