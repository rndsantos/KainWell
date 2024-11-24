package com.example.kainwell.ui.common.composable

import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.kainwell.R
import com.example.kainwell.data.food.Food
import com.example.kainwell.ui.Dimensions.SmallPadding
import com.example.kainwell.ui.utils.isDarkMode

@Composable
fun FoodItemCard(
    food: Food,
    selected: Boolean,
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
                    imageUrl = food.img,
                    contentDescription = food.img,
                    modifier = Modifier
                        .aspectRatio(1f)
                )
                Box(
                    Modifier.align(Alignment.BottomEnd)
                ) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = !selected, enter = expandIn(
                            expandFrom = Alignment.Center
                        ) + fadeIn(
                            initialAlpha = 0.3f
                        ),
                        exit = shrinkOut(
                            shrinkTowards = Alignment.Center
                        ) + fadeOut()
                    ) {
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
                    }

                    androidx.compose.animation.AnimatedVisibility(
                        visible = selected,
                        enter = expandIn(
                            expandFrom = Alignment.Center
                        ) + fadeIn(
                            initialAlpha = 0.3f
                        ),
                        exit = shrinkOut(
                            shrinkTowards = Alignment.Center
                        ) + fadeOut()
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.inverseSurface,
                            shadowElevation = 4.dp,
                            shape = CircleShape,
                            modifier = Modifier
                                .size(45.dp)
                                .padding(SmallPadding)
                        ) {
                            IconButton(
                                onClick = onRemoveFoodItem,
                                modifier = Modifier.padding(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "add food to meal",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }

            }
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = food.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth(0.7f)
                    )
                    MacronutrientValue(value = "${food.calories} kcal") {
                        Icon(
                            painter = painterResource(R.drawable.ic_calories),
                            contentDescription = "protein",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(SmallPadding),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MacronutrientValue(value = "${food.protein}g") {
                            Icon(
                                painter = painterResource(R.drawable.ic_protein),
                                contentDescription = "protein",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        MacronutrientValue(value = "${food.carbohydrates}g") {
                            Icon(
                                painter = painterResource(R.drawable.ic_carbs),
                                contentDescription = "carbs",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        MacronutrientValue(value = "${food.fat}g") {
                            Icon(
                                painter = painterResource(R.drawable.ic_fats),
                                contentDescription = "fats",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
                Badge(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                ) {
                    Text(text = "$${food.price}", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}


@Composable
fun MacronutrientValue(
    value: String,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        icon()
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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

