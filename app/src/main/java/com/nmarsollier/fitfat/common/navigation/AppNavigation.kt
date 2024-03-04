package com.nmarsollier.fitfat.common.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nmarsollier.fitfat.common.converters.jsonToObject
import com.nmarsollier.fitfat.common.converters.toJson
import com.nmarsollier.fitfat.dashboard.ui.DashboardScreen
import com.nmarsollier.fitfat.measures.model.db.MeasureData
import com.nmarsollier.fitfat.measures.ui.edit.EditMeasureScreen

enum class AppGraph {
    HomeGraph, NewMeasure, EditMeasure
}

@Composable
fun AppNavigationHost(
    mainNavActions: AppNavActions,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = mainNavActions.navController,
        startDestination = AppGraph.HomeGraph.name,
        modifier = modifier,
    ) {
        // Dashboard
        composable(
            route = AppGraph.HomeGraph.name,
        ) {
            DashboardScreen()
        }

        // Edit New Measure
        composable(
            route = AppGraph.NewMeasure.name,
        ) {
            EditMeasureScreen(initialMeasure = null)
        }

        // Edit Measure
        composable(
            route = "${AppGraph.EditMeasure.name}/{measureData}",
            arguments = listOf(navArgument("measureData") { type = NavType.StringType }),
        ) {
            val measureJson = it.arguments?.getString("measureData")
            val measure = measureJson?.jsonToObject<MeasureData>()
                ?: throw IllegalArgumentException("Invalid or missing measure data")

            EditMeasureScreen(initialMeasure = measure)
        }
    }
}

class AppNavActions(
    val navController: NavHostController
) {
    fun navigateToHome() {
        navController.navigate(AppGraph.HomeGraph.name) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateEditMeasure(measure: MeasureData) {
        navController.navigate("${AppGraph.EditMeasure.name}/${measure.toJson()}") {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateNewMeasure() {
        navController.navigate(AppGraph.NewMeasure.name) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}