package com.example.kainwell.ui.home.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.kainwell.data.food.Food
import com.example.kainwell.ui.Dimensions.MediumPadding
import com.example.kainwell.ui.Dimensions.SmallPadding
import com.example.kainwell.ui.common.composable.ErrorScreen
import com.example.kainwell.ui.common.composable.LoadingScreen

@Composable
fun GalleryScreen(
    modifier: Modifier = Modifier,
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
    foodItems: Map<String, List<Food>>,
) {
    Surface {
        LazyColumn(
            modifier = Modifier
                .padding(MediumPadding)
                .fillMaxSize()
        ) {
            items(foodItems.entries.toList()) { foodCategory ->
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainer
                ) {
                    Text(
                        text = foodCategory.key,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(MediumPadding)
                    )
                }

                ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                    val (grid) = createRefs()
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Adaptive(150.dp),
                        verticalItemSpacing = SmallPadding,
                        horizontalArrangement = Arrangement.spacedBy(SmallPadding),
                        modifier = Modifier
                            .constrainAs(grid) {
                                top.linkTo(parent.top)
                                height =
                                    Dimension.value(500.dp) // Adjust height as needed
                            }
                    ) {
                        items(foodCategory.value.toList()) { foodItem ->
                            AsyncImage(
                                model = foodItem.img,
                                contentScale = ContentScale.Crop,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}