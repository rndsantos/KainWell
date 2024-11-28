@file:OptIn(ExperimentalLayoutApi::class)

package com.example.kainwell.ui.components

import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.composables.core.DragIndication
import com.composables.core.ModalBottomSheet
import com.composables.core.ModalBottomSheetState
import com.composables.core.Scrim
import com.composables.core.Sheet
import com.example.kainwell.R
import com.example.kainwell.data.food.Food
import com.example.kainwell.ui.Dimensions.ExtraLargePadding
import com.example.kainwell.ui.Dimensions.MediumPadding
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
                            tint = MaterialTheme.colorScheme.primary,
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
fun FoodDetailBottomSheet(
    food: Food,
    sheetState: ModalBottomSheetState,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    selected: Boolean? = null,
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
            Column {
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .verticalScroll(
                            rememberScrollState()
                        )
                        .then(if (selected == null) Modifier.navigationBarsPadding() else Modifier)
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
                                .heightIn(250.dp)
                        )
                        Column(
                            modifier = Modifier
                                .padding(MediumPadding)
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

                }
                selected?.let {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 8.dp,
                        shape = MaterialTheme.shapes.small,
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
                                    vertical = MediumPadding
                                ),
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (selected) "Remove from meal" else "Add to meal",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Black
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NutritionalValuesGrid(food: Food) {
    data class Nutrient(val name: String, val value: String, val useWeight: Boolean = true)

    val nutrients = listOf(
        Nutrient("Fiber", "${food.fiber} g"),
        Nutrient("Sodium", "${food.sodium} mg"),
        Nutrient("Vit A", "${food.vitA} IU"),
        Nutrient("Vit C", "${food.vitC} IU"),
        Nutrient("Calcium", "${food.calcium} mg"),
        Nutrient("Iron", "${food.iron} mg"),
        Nutrient("Cholesterol", "${food.cholesterol} mg")
    )
    val macros = listOf(
        Nutrient("Calories", "${food.calories}"),
        Nutrient("Protein", "${food.protein} g"),
        Nutrient("Carbs", "${food.carbohydrates} g"),
        Nutrient("Fat", "${food.fat} g"),
    )
    val macroColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary
    )

    FlowRow(
        maxItemsInEachRow = 4,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MediumPadding)
    ) {
        macros.zip(macroColors) { nutrient, color ->
            NutrientCard(
                name = nutrient.name,
                value = nutrient.value,
                badge = {
                    Badge(
                        containerColor = color,
                        modifier = Modifier.zIndex(1f)
                    )
                },
                modifier = Modifier.then(
                    if (nutrient.useWeight) Modifier.weight(1f) else Modifier
                )
            )
        }

        nutrients.map { nutrient ->
            NutrientCard(
                name = nutrient.name,
                value = nutrient.value,
                modifier = Modifier
                    .then(
                        if (nutrient.useWeight) Modifier.weight(1f) else Modifier
                    )
            )
        }
    }
}

@Composable
private fun NutrientCard(
    name: String,
    value: String,
    modifier: Modifier = Modifier,
    badge: @Composable (() -> Unit)? = null,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shadowElevation = 1.dp,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier
    ) {
        if (badge != null) {
            badge()
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(MediumPadding)

        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Black
                ),
                maxLines = 1,
            )
            Spacer(Modifier.height(MediumPadding))
            Text(text = value, style = MaterialTheme.typography.bodySmall, maxLines = 1)
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
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
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

