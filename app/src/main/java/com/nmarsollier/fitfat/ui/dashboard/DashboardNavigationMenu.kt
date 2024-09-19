package com.nmarsollier.fitfat.ui.dashboard


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.ui.common.preview.*

@Composable
fun DashboardNavigationMenu(
    screen: Screen, reduce: (Screen) -> Unit
) {
    val colors = NavigationBarItemDefaults.colors(
        indicatorColor = Color.Transparent,
        unselectedIconColor = MaterialTheme.colorScheme.primaryContainer,
        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        tonalElevation = 8.dp,
        modifier = Modifier
            .shadow(elevation = 8.dp)
            .fillMaxWidth()
            .height(60.dp)
    ) {
        NavigationBarItem(icon = {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(id = R.string.home_menu_options),
                )
                Text(
                    text = stringResource(id = R.string.home_menu_options),
                    fontSize = 12.sp,
                )
            }
        }, alwaysShowLabel = true, selected = screen == Screen.OPTIONS, onClick = {
            reduce(Screen.OPTIONS)
        }, colors = colors
        )

        NavigationBarItem(icon = {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = stringResource(id = R.string.home_menu_main),
                )
                Text(
                    text = stringResource(id = R.string.home_menu_main),
                    fontSize = 12.sp,
                )
            }
        }, alwaysShowLabel = true, selected = screen == Screen.MEASURES_LIST, onClick = {
            reduce(Screen.MEASURES_LIST)
        }, colors = colors
        )

        NavigationBarItem(icon = {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_show_chart_black_24dp),
                    contentDescription = stringResource(id = R.string.home_menu_progress),
                )
                Text(
                    text = stringResource(id = R.string.home_menu_progress),
                    fontSize = 12.sp,
                )
            }
        }, alwaysShowLabel = true, selected = screen == Screen.STATS, onClick = {
            reduce(Screen.STATS)
        }, colors = colors
        )
    }
}

@Preview
@Composable
private fun DashboardNavigationMenuPreview() {
    KoinPreview {
        Column {
            DashboardNavigationMenu(
                screen = Screen.MEASURES_LIST
            ) {}
        }
    }
}
