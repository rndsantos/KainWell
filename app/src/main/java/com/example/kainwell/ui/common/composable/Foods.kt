package com.example.kainwell.ui.common.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.kainwell.ui.Dimensions.SmallPadding
import com.example.kainwell.ui.meal.FoodItemCounter
import com.example.kainwell.ui.utils.isDarkMode

@Composable
fun FoodItemCard(
    name: String,
    quantity: Int,
    price: Float,
    imageUrl: String,
    onClick: () -> Unit,
    onAddFoodItem: () -> Unit,
    onRemoveFoodItem: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = if (isDarkMode(LocalContext.current)) MaterialTheme.colorScheme.surfaceContainerLow
        else MaterialTheme.colorScheme.background,
        shape = MaterialTheme.shapes.small,
        shadowElevation = if (isDarkMode(LocalContext.current)) 0.dp else 4.dp,
        modifier = modifier.clickable {
            onClick()
        }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
        ) {
            Box {
                FoodImage(
                    imageUrl = imageUrl,
                    contentDescription = name,
                    modifier = Modifier
                        .aspectRatio(1f)
                )
                Box(
                    Modifier.align(Alignment.BottomEnd)
                ) {
                    if (quantity == 0)
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 4.dp,
                            shape = CircleShape,
                            modifier = Modifier
                                .size(45.dp)
                                .padding(SmallPadding)
                        ) {
                            IconButton(
                                onClick = onAddFoodItem,
                                modifier = Modifier.padding(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "add food to meal",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    else
                        FoodItemCounter(
                            quantity = quantity,
                            onAdd = onAddFoodItem,
                            onRemove = onRemoveFoodItem,
                            modifier = Modifier
                                .padding(SmallPadding)
                                .shadow(
                                    elevation = 4.dp,
                                    shape = MaterialTheme.shapes.extraLarge
                                )
                                .clip(MaterialTheme.shapes.extraLarge)
                                .background(MaterialTheme.colorScheme.surface)
                        )
                }

            }
            Column(
                modifier = Modifier.padding(SmallPadding)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(text = "$$price", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun FoodImage(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}

