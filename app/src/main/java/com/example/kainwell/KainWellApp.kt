@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.example.kainwell

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
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

@SuppressLint("RestrictedApi")
@Composable
fun KainWellApp() {
    KainWellTheme {
        val kainWellNavController = rememberKainWellNavController()
        val durationMillis = 300

        SharedTransitionLayout {
            CompositionLocalProvider(
                LocalSharedTransitionScope provides this
            ) {
                NavHost(
                    navController = kainWellNavController.navController,
                    startDestination = Home
                ) {
                    composableWithCompositionLocal<Home> { backStackEntry ->
                        MainContainer(
                            navigateToAddDiet = {
                                kainWellNavController.navigate(
                                    route = AddDiet,
                                    from = backStackEntry
                                )
                            }
                        )
                    }

                    navigation<AddDiet>(startDestination = PickFoodItems) {
                        composable<PickFoodItems> { backStackEntry ->
                            AddDietScreen(
                                navigateToViewMeal = {
                                    kainWellNavController.navigate(
                                        route = ViewMeal,
                                        from = backStackEntry
                                    )
                                }
                            )
                        }

                        composable<ViewMeal>(
                            popEnterTransition = {
                                slideInHorizontally()
                            },
                            exitTransition = {
                                slideOutHorizontally()
                            }
                        ) { backStackEntry ->
                            val viewModel =
                                hiltViewModel<AddDietViewModel>(
                                    kainWellNavController.getBackStackEntry(
                                        PickFoodItems
                                    )
                                )
                            ViewMealScreen(
                                onNavigateToViewOptimizedDiet = {
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

                        composable<ViewOptimizedDiet>(
                            enterTransition = {
                                slideInHorizontally()
                            },
                            popExitTransition = {
                                slideOutHorizontally()
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
    }
}

@Composable
fun MainContainer(
    navigateToAddDiet: (NavBackStackEntry) -> Unit,
) {
    val nestedNavController = rememberKainWellNavController()
    val navBackStackEntry by nestedNavController.navController.currentBackStackEntryAsState()
    Scaffold(
        bottomBar = {
            KainWellBottomBar(
                navigateToAddDiet = {
                    navigateToAddDiet(navBackStackEntry!!)
                },
                navigateToRoute = nestedNavController::navigateToBottomBarRoute
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = nestedNavController.navController,
            startDestination = Diet
        ) {
            addHomeGraph(
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
            )
        }
    }
}

inline fun <reified T : Any> NavGraphBuilder.composableWithCompositionLocal(
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
        CompositionLocalProvider(
            LocalNavAnimatedVisibilityScope provides this@composable
        ) {
            content(it)
        }
    }
}

fun <T> spatialExpressiveSpring() = spring<T>(
    dampingRatio = 0.8f,
    stiffness = 380f
)

fun <T> nonSpatialExpressiveSpring() = spring<T>(
    dampingRatio = 1f,
    stiffness = 1600f
)

val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }


