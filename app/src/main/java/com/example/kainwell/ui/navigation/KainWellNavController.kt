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
    fun getBackStackEntry(route: Route): NavBackStackEntry {
        return navController.getBackStackEntry(route)
    }

    fun navigateToBottomBarRoute(route: Route) {
        if (route.toString() != navController.currentDestination?.route) {
            navController.navigate(route) {
                launchSingleTop = true
                restoreState = true
                popUpTo(findStartDestination(navController.graph).id) {
                    saveState = true
                }
            }
        }
    }

    fun navigate(route: Route, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(route)
        }
    }

    fun navigateAndPopUp(route: Route, popUp: Route, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(route) {
                popUpTo(popUp) {
                    inclusive = true
                }
            }
        }
    }
}

typealias Route = Any

private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED

private val NavGraph.startDestination: NavDestination?
    get() = findNode(startDestinationId)

private tailrec fun findStartDestination(graph: NavDestination): NavDestination {
    return if (graph is NavGraph) findStartDestination(graph.startDestination!!) else graph
}