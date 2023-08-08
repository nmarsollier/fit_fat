package com.nmarsollier.fitfat.ui.common.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nmarsollier.fitfat.utils.jsonToObject
import com.nmarsollier.fitfat.utils.toJson
import com.nmarsollier.fitfat.models.measures.Measure
import com.nmarsollier.fitfat.ui.dashboard.DashboardScreen
import com.nmarsollier.fitfat.ui.measures.edit.EditMeasureScreen
import org.koin.compose.koinInject

enum class AppGraph {
    HomeGraph, NewMeasure, EditMeasure
}

@Composable
fun AppNavigationHost(
    appNavActionProvider: NavigationProvider = koinInject(),
) {
    val actions by rememberUpdatedState(appNavActionProvider.appNavActions)

    NavHost(
        navController = actions!!.navController,
        startDestination = AppGraph.HomeGraph.name,
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
            val measure = measureJson?.jsonToObject<Measure>()
                ?: throw IllegalArgumentException("Invalid or missing measure data")

            EditMeasureScreen(initialMeasure = measure)
        }
    }
}

class AppNavActions(
    val navController: NavHostController
) {
    fun navigateUp() {
        navController.navigateUp()
    }

    fun navigateEditMeasure(measure: Measure) {
        navController.navigate("${AppGraph.EditMeasure.name}/${measure.toJson()}") {
            launchSingleTop = true
        }
    }

    fun navigateNewMeasure() {
        navController.navigate(AppGraph.NewMeasure.name) {
            launchSingleTop = true
        }
    }
}