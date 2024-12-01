package com.example.kainwell

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import com.example.kainwell.ui.add_diet.AddDietScreen
import com.example.kainwell.ui.add_diet.AddDietViewModel
import com.example.kainwell.ui.add_diet.view.ViewMealScreen
import com.example.kainwell.ui.add_diet.view.ViewOptimizedDietScreen
import com.example.kainwell.ui.home.Diet
import com.example.kainwell.ui.home.KainWellBottomBar
import com.example.kainwell.ui.home.addHomeGraph
import com.example.kainwell.ui.navigation.rememberKainWellNavController
import com.example.kainwell.ui.theme.KainWellTheme
import com.example.kainwell.ui.welcome.WelcomeScreen
import kotlinx.serialization.Serializable


@Serializable
object Home

@Serializable
object AddDiet

@Serializable
object PickFoodItems

@Serializable
object ViewMeal

@Serializable
object ViewOptimizedDiet

@Serializable
object Welcome

@SuppressLint("RestrictedApi")
@Composable
fun KainWellApp() {
    KainWellTheme {
        val kainWellNavController = rememberKainWellNavController()


        NavHost(
            navController = kainWellNavController.navController,
            startDestination = Home
        ) {
            composableWithTransition<Welcome> { backStackEntry ->
                WelcomeScreen(
                    navigateToDietScreen = {
                        kainWellNavController.navigateAndPopUp(
                            route = Home,
                            popUp = Welcome,
                            from = backStackEntry
                        )
                    },
                )
            }

            composableWithTransition<Home> { backStackEntry ->
                MainContainer(
                    navigateToWelcome = {
                        kainWellNavController.navigateAndPopUp(
                            route = Welcome,
                            popUp = Home,
                            from = backStackEntry
                        )
                    },
                    navigateToAddDiet = {
                        kainWellNavController.navigate(
                            route = AddDiet,
                            from = backStackEntry
                        )
                    },
                )
            }

            navigation<AddDiet>(startDestination = PickFoodItems) {
                composableWithTransition<PickFoodItems> { backStackEntry ->
                    AddDietScreen(
                        onBack = { kainWellNavController.navController.popBackStack() },
                        navigateToViewMeal = {
                            kainWellNavController.navigate(
                                route = ViewMeal,
                                from = backStackEntry
                            )
                        }
                    )
                }

                composableWithTransition<ViewMeal>(
                    enterTransition = {
                        slideInVertically { it / 3 }
                    },
                    exitTransition = {
                        slideOutHorizontally { -it / 3 }
                    },
                    popEnterTransition = {
                        slideInHorizontally { -it / 3 }
                    },
                    popExitTransition = {
                        slideOutVertically { it / 3 }
                    }
                ) { backStackEntry ->
                    val viewModel =
                        hiltViewModel<AddDietViewModel>(
                            kainWellNavController.getBackStackEntry(
                                PickFoodItems
                            )
                        )
                    ViewMealScreen(
                        navigateToViewOptimizedDiet = {
                            kainWellNavController.navigate(
                                route = ViewOptimizedDiet,
                                from = backStackEntry
                            )
                        },
                        onBack = {
                            kainWellNavController.navController.popBackStack()
                        },
                        viewModel = viewModel
                    )
                }

                composableWithTransition<ViewOptimizedDiet>(
                    enterTransition = {
                        slideInHorizontally { it / 3 }
                    },
                    popExitTransition = {
                        slideOutHorizontally { it / 3 }
                    }
                ) { backStackEntry ->
                    val viewModel =
                        hiltViewModel<AddDietViewModel>(
                            kainWellNavController.getBackStackEntry(
                                PickFoodItems
                            )
                        )
                    ViewOptimizedDietScreen(
                        onNavigateToHome = {
                            kainWellNavController.navigate(
                                route = Home,
                                from = backStackEntry
                            )
                        },
                        onBack = {
                            kainWellNavController.navController.popBackStack()
                        },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun MainContainer(
    navigateToWelcome: () -> Unit,
    navigateToAddDiet: () -> Unit,
) {
    val nestedNavController = rememberKainWellNavController()
    val navBackStackEntry by nestedNavController.navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            KainWellBottomBar(
                currentRoute = currentRoute ?: Diet.toString(),
                navigateToAddDiet = navigateToAddDiet,
                navigateToRoute = nestedNavController::navigateToBottomBarRoute
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = nestedNavController.navController,
            startDestination = Diet
        ) {
            addHomeGraph(
                navigateToWelcome = navigateToWelcome,
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
            )
        }
    }
}

inline fun <reified T : Any> NavGraphBuilder.composableWithTransition(
    noinline enterTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
    )? = {
        fadeIn(nonSpatialExpressiveSpring())
    },
    noinline exitTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?
    )? = {
        fadeOut(nonSpatialExpressiveSpring())
    },
    noinline popEnterTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
    )? =
        enterTransition,
    noinline popExitTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?
    )? =
        exitTransition,
    crossinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    composable<T>(
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition,
    ) {
        content(it)
    }
}

fun <T> nonSpatialExpressiveSpring() = spring<T>(
    dampingRatio = 1f,
    stiffness = 1600f
)



