package com.example.kainwell.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.kainwell.ui.Dimensions.LargePadding
import com.example.kainwell.ui.Dimensions.SmallPadding
import com.example.kainwell.ui.home.diet.DietScreen
import com.example.kainwell.ui.home.gallery.GalleryScreen
import com.example.kainwell.ui.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
object Diet {
    override fun toString(): String {
        return "com.example.kainwell.ui.home.Diet"
    }
}

@Serializable
object Gallery {
    override fun toString(): String {
        return "com.example.kainwell.ui.home.Gallery"
    }
}

fun NavGraphBuilder.addHomeGraph(
    navigateToWelcome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable<Diet> {
        DietScreen(
            navigateToWelcome = navigateToWelcome,
            modifier = modifier,
        )
    }
    composable<Gallery> {
        GalleryScreen()
    }
}

@Composable
fun KainWellBottomBar(
    currentRoute: Route,
    navigateToAddDiet: () -> Unit,
    navigateToRoute: (route: Route) -> Unit,
) {
    BottomAppBar(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            BottomNavItem(
                selected = currentRoute == Diet.toString(),
                icon = Icons.Filled.FavoriteBorder,
                label = "Diets",
                onClick = {
                    navigateToRoute(Diet)
                },
            )
            BottomNavItem(
                selected = false,
                icon = Icons.Outlined.AddCircle,
                label = "Add",
                onClick = navigateToAddDiet,
            )
            BottomNavItem(
                selected = currentRoute == Gallery.toString(),
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
private fun BottomNavItem(
    selected: Boolean,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .clip(CircleShape)
            .then(
                if (selected) Modifier.background(
                    MaterialTheme.colorScheme.primaryContainer.copy(
                        alpha = 0.5f
                    )
                ) else Modifier
            )
            .padding(vertical = SmallPadding, horizontal = LargePadding)
            .size(50.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                icon,
                tint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                contentDescription = label
            )
            Text(label, style = MaterialTheme.typography.bodySmall)
        }

    }
}