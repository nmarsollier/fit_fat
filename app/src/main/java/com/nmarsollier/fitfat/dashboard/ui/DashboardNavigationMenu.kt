package com.nmarsollier.fitfat.dashboard.ui


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nmarsollier.fitfat.R
import com.nmarsollier.fitfat.common.ui.preview.KoinPreview
import com.nmarsollier.fitfat.common.ui.theme.AppColors
import com.nmarsollier.fitfat.common.ui.viewModel.Reducer
import com.nmarsollier.fitfat.dashboard.samples.Samples

@Composable
fun DashboardNavigationMenu(
    state: DashboardState, reducer: Reducer<DashboardEvent>
) {

    BottomNavigation(
        elevation = 0.dp,
        backgroundColor = AppColors.secondary,
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
    ) {
        BottomNavigationItem(icon = {
            Icon(
                painterResource(id = R.drawable.ic_settings_black_24dp),
                contentDescription = stringResource(id = R.string.home_menu_options)
            )
        },
            label = {
                Text(
                    text = stringResource(id = R.string.home_menu_options), fontSize = 9.sp
                )
            },
            unselectedContentColor = AppColors.secondaryVariant,
            selectedContentColor = AppColors.primary,
            alwaysShowLabel = true,
            selected = state.selectedTab == Screen.OPTIONS,
            onClick = {
                reducer.reduce(DashboardEvent.CurrentSelectedTab(Screen.OPTIONS))
            })

        BottomNavigationItem(icon = {
            Icon(
                painterResource(id = R.drawable.ic_home_black_24dp),
                contentDescription = stringResource(id = R.string.home_menu_main)
            )
        },
            label = {
                Text(
                    text = stringResource(id = R.string.home_menu_main), fontSize = 9.sp
                )
            },
            unselectedContentColor = AppColors.secondaryVariant,
            selectedContentColor = AppColors.primary,
            alwaysShowLabel = true,
            selected = state.selectedTab == Screen.MEASURES_LIST,
            onClick = {
                reducer.reduce(DashboardEvent.CurrentSelectedTab(Screen.MEASURES_LIST))
            })

        BottomNavigationItem(icon = {
            Icon(
                painterResource(id = R.drawable.ic_show_chart_black_24dp),
                contentDescription = stringResource(id = R.string.home_menu_progress)
            )
        },
            label = {
                Text(
                    text = stringResource(id = R.string.home_menu_progress), fontSize = 9.sp
                )
            },
            unselectedContentColor = AppColors.secondaryVariant,
            selectedContentColor = AppColors.primary,
            alwaysShowLabel = true,
            selected = state.selectedTab == Screen.STATS,
            onClick = {
                reducer.reduce(DashboardEvent.CurrentSelectedTab(Screen.STATS))
            })
    }
}

@Preview
@Composable
fun DashboardNavigationMenuPreview() {
    KoinPreview {
        Column {
            DashboardNavigationMenu(
                DashboardState.Ready(
                    tab = Screen.MEASURES_LIST
                ),
                DashboardViewModel.Samples.reducer()
            )
        }
    }
}
