package com.nmarsollier.fitfat.ui.common.navigation

import androidx.compose.runtime.*
import androidx.navigation.*
import androidx.navigation.compose.*
import com.nmarsollier.fitfat.models.measures.*
import com.nmarsollier.fitfat.ui.dashboard.*
import com.nmarsollier.fitfat.ui.measures.edit.*
import com.nmarsollier.fitfat.utils.*
import org.koin.compose.*

@Composable
fun AppNavigationHost(
    appNavActionProvider: AppNavActions = koinInject(),
) {
    val actions by rememberUpdatedState(appNavActionProvider)

    NavHost(
        navController = actions.navController,
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
