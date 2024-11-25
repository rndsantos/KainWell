package com.example.kainwell.ui.home.diet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kainwell.R
import com.example.kainwell.ui.Dimensions.MediumPadding
import com.example.kainwell.ui.Dimensions.SmallPadding
import com.example.kainwell.ui.common.composable.ErrorScreen
import com.example.kainwell.ui.common.composable.LoadingScreen
import com.example.kainwell.ui.common.composable.MacronutrientValue
import com.example.kainwell.ui.utils.isDarkMode

@Composable
fun DietScreen(
    modifier: Modifier = Modifier,
    viewModel: DietViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        is DietUiState.Loading -> LoadingScreen()
        is DietUiState.Success -> DietScreenReady(
            savedDiets = state.savedDiets,
            modifier = modifier,
        )

        is DietUiState.Error -> ErrorScreen(
            errorMessage = state.errorMessage,
        )
    }
}

@Composable
fun DietScreenReady(
    savedDiets: List<Diet>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.padding(MediumPadding)
    ) {
        Text(
            text = "Saved Diets", style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Black
            )
        )

        if (savedDiets.isEmpty()) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "No saved diets yet",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(MediumPadding)
            ) {
                items(savedDiets) { diet ->
                    DietListItem(diet = diet, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
fun DietListItem(
    diet: Diet,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = MaterialTheme.shapes.large,
        shadowElevation = if (isDarkMode(LocalContext.current)) 0.dp else 4.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(SmallPadding)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                MacronutrientValue(value = "${diet.totalCalories} kcal") {
                    Icon(
                        painter = painterResource(R.drawable.ic_calories),
                        contentDescription = "protein",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                }
                MacronutrientValue(value = "${diet.totalProtein}g") {
                    Icon(
                        painter = painterResource(R.drawable.ic_protein),
                        contentDescription = "protein",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(14.dp)
                    )
                }
                MacronutrientValue(value = "${diet.totalCarbohydrates}g") {
                    Icon(
                        painter = painterResource(R.drawable.ic_carbs),
                        contentDescription = "carbs",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(14.dp)
                    )
                }
                MacronutrientValue(value = "${diet.totalFat}g") {
                    Icon(
                        painter = painterResource(R.drawable.ic_fats),
                        contentDescription = "fats",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}