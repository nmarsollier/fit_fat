package com.nmarsollier.fitfat.ui.common.navigation

import androidx.navigation.*
import com.nmarsollier.fitfat.models.measures.*
import com.nmarsollier.fitfat.utils.*

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
