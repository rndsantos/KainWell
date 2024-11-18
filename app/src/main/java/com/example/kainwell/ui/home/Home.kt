package com.example.kainwell.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.kainwell.ui.home.diet.DietScreen
import com.example.kainwell.ui.home.gallery.GalleryScreen
import kotlinx.serialization.Serializable

@Serializable
object Diet

@Serializable
object Gallery

fun NavGraphBuilder.addHomeGraph(
    modifier: Modifier = Modifier,
) {
    composable<Diet> {
        DietScreen(
            modifier = modifier,
        )
    }
    composable<Gallery> {
        GalleryScreen(
            modifier = modifier,
        )
    }

    // add screen
}

@Composable
fun KainWellBottomBar(
    navigateToAddDiet: () -> Unit,
    navigateToRoute: (route: Any) -> Unit,
) {
    BottomAppBar(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            BottomNavItem(
                icon = Icons.Outlined.FavoriteBorder,
                label = "Diets",
                onClick = {
                    navigateToRoute(Diet)
                },
            )
            BottomNavItem(
                icon = Icons.Outlined.AddCircle,
                label = "Add",
                onClick = navigateToAddDiet,
            )
            BottomNavItem(
                icon = Icons.Outlined.Menu,
                label = "Gallery",
                onClick = {
                    navigateToRoute(Gallery)
                },
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(50.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(icon, contentDescription = "favorite")
            Text(label, style = MaterialTheme.typography.bodySmall)
        }

    }
}