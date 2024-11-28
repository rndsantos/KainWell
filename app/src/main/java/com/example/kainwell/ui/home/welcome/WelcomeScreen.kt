@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.kainwell.ui.home.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kainwell.NutrientEntity
import com.example.kainwell.ui.Dimensions.MediumPadding
import com.example.kainwell.ui.Dimensions.SmallPadding
import com.example.kainwell.ui.components.ErrorScreen
import com.example.kainwell.ui.components.KainWellBottomAppBar
import com.example.kainwell.ui.utils.isDarkMode

@Composable
fun WelcomeScreen(
    navigateToDietScreen: () -> Unit,
    viewModel: WelcomeViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState.value) {
        is WelcomeUiState.Ready -> WelcomeScreenReady(
            setNutritionalIntakes = viewModel::setNutritionalIntakes,
        )

        is WelcomeUiState.Finished -> navigateToDietScreen()
        is WelcomeUiState.Error -> ErrorScreen(
            errorMessage = state.errorMessage,
        )
    }
}

@Composable
private fun WelcomeScreenReady(
    setNutritionalIntakes: (NutrientEntity, NutrientEntity) -> Unit,
) {
    data class Nutrient(val name: String, var minimum: String, var maximum: String)

    val nutrients = listOf(
        Nutrient("Calories", "2000", "2250"),
        Nutrient("Carbohydrates", "0", "300"),
        Nutrient("Cholesterol", "0", "300"),
        Nutrient("Sodium", "0", "2400"),
        Nutrient("Fat", "0", "65"),
        Nutrient("Fiber", "25", "100"),
        Nutrient("Protein", "50", "100"),
        Nutrient("Vitamin A", "5000", "50000"),
        Nutrient("Vitamin C", "50", "20000"),
        Nutrient("Calcium", "800", "1600"),
        Nutrient("Iron", "10", "30"),
    )

    var nutrientsState by remember { mutableStateOf(nutrients.toTypedArray()) }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    text = "Set your daily goals", style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black
                    )
                )
            })
        },
        bottomBar = {
            KainWellBottomAppBar(onClick = {
                setNutritionalIntakes(
                    NutrientEntity.newBuilder().apply {
                        nutrientsState.forEach { nutrient ->
                            when (nutrient.name) {
                                "Calories" -> calories = nutrient.minimum.toFloat()
                                "Carbohydrates" -> carbohydrates =
                                    nutrient.minimum.toFloat()

                                "Cholesterol" -> cholesterol = nutrient.minimum.toFloat()
                                "Sodium" -> sodium = nutrient.minimum.toFloat()
                                "Fat" -> fat = nutrient.minimum.toFloat()
                                "Fiber" -> fiber = nutrient.minimum.toFloat()
                                "Protein" -> protein = nutrient.minimum.toFloat()
                                "Vitamin A" -> vitA = nutrient.minimum.toFloat()
                                "Vitamin C" -> vitC = nutrient.minimum.toFloat()
                                "Calcium" -> calcium = nutrient.minimum.toFloat()
                                "Iron" -> iron = nutrient.minimum.toFloat()
                            }
                        }
                    }.build(),
                    NutrientEntity.newBuilder().apply {
                        nutrientsState.forEach { nutrient ->
                            when (nutrient.name) {
                                "Calories" -> calories = nutrient.maximum.toFloat()
                                "Carbohydrates" -> carbohydrates =
                                    nutrient.maximum.toFloat()

                                "Cholesterol" -> cholesterol = nutrient.maximum.toFloat()
                                "Sodium" -> sodium = nutrient.maximum.toFloat()
                                "Fat" -> fat = nutrient.maximum.toFloat()
                                "Fiber" -> fiber = nutrient.maximum.toFloat()
                                "Protein" -> protein = nutrient.maximum.toFloat()
                                "Vitamin A" -> vitA = nutrient.maximum.toFloat()
                                "Vitamin C" -> vitC = nutrient.maximum.toFloat()
                                "Calcium" -> calcium = nutrient.maximum.toFloat()
                                "Iron" -> iron = nutrient.maximum.toFloat()
                            }
                        }
                    }.build()
                )
            }) {
                Text(
                    text = "Set Daily Intakes",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .padding(SmallPadding)
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(MediumPadding),
            contentPadding = innerPadding,
            modifier = Modifier.padding(horizontal = MediumPadding, vertical = SmallPadding)
        ) {
            items(nutrientsState) { nutrient ->
                NutrientInput(
                    name = nutrient.name,
                    minimumValue = nutrient.minimum,
                    onMinimumValueChange = { value ->
                        nutrient.minimum = value
                        nutrientsState = nutrientsState.clone()
                    },
                    maximumValue = nutrient.maximum,
                    onMaximumValueChange = { value ->
                        nutrient.maximum = value
                        nutrientsState = nutrientsState.clone()
                    },
                )
            }

        }
    }
}

@Composable
private fun NutrientValueTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            label = {
                Text(label)
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            maxLines = 1,
            colors = TextFieldDefaults.colors().copy(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier.padding(SmallPadding)

        )
    }
}

@Composable
private fun NutrientInput(
    name: String,
    minimumValue: String,
    onMinimumValueChange: (String) -> Unit,
    maximumValue: String,
    onMaximumValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = if (isDarkMode(LocalContext.current)) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
        shadowElevation = if (isDarkMode(LocalContext.current)) 0.dp else 4.dp,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(SmallPadding),
            modifier = Modifier.padding(MediumPadding)
        ) {
            Text(
                text = name, style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black
                )
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(SmallPadding),
                modifier = Modifier.fillMaxWidth()
            ) {
                NutrientValueTextField(
                    value = minimumValue,
                    label = "Minimum",
                    onValueChange = onMinimumValueChange,
                    modifier = Modifier.weight(1f)
                )
                NutrientValueTextField(
                    value = maximumValue,
                    label = "Maximum",
                    onValueChange = onMaximumValueChange,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

//@Composable
//private fun NutrientCard(
//    name: String,
//    modifier: Modifier = Modifier,
//) {
//    Surface(
//        modifier = modifier,
//        shape = MaterialTheme.shapes.medium,
//    ) {
//        Box(contentAlignment = Alignment.Center) {
//            Text(
//                text = name,
//                style = MaterialTheme.typography.titleSmall.copy(
//                    fontWeight = FontWeight.Black
//                )
//            )
//        }
//    }
//}