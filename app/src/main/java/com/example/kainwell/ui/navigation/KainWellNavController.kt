package com.example.kainwell.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun rememberKainWellNavController(
    navController: NavHostController = rememberNavController(),
): KainWellNavController = remember(navController) {
    KainWellNavController(navController)
}

@Stable
class KainWellNavController(
    val navController: NavHostController,
) {
    fun <T : Any> getBackStackEntry(route: T): NavBackStackEntry {
        return navController.getBackStackEntry(route)
    }

    fun <T : Any> navigateToBottomBarRoute(route: T) {
        if (route != navController.currentDestination) {
            navController.navigate(route) {
                launchSingleTop = true
                restoreState = true
                popUpTo(findStartDestination(navController.graph).id) {
                    saveState = true
                }
            }
        }
    }

    fun <T : Any> navigate(route: T, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(route)
        }
    }
}

private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED

private val NavGraph.startDestination: NavDestination?
    get() = findNode(startDestinationId)

private tailrec fun findStartDestination(graph: NavDestination): NavDestination {
    return if (graph is NavGraph) findStartDestination(graph.startDestination!!) else graph
}