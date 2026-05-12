package com.done.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.done.feature.detail.ui.DetailScreen
import com.done.feature.manage.ui.ManageHabitScreen
import com.done.feature.stats.ui.StatsScreen
import com.done.feature.today.ui.TodayScreen

data class BottomNavItem<T : Any>(
    val route: T,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun DoneNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem(
            route = TodayRoute,
            label = "Hoje",
            selectedIcon = Icons.Filled.CheckCircle,
            unselectedIcon = Icons.Outlined.CheckCircle
        ),
        BottomNavItem(
            route = ManageRoute(),
            label = "Gerenciar",
            selectedIcon = Icons.Filled.List,
            unselectedIcon = Icons.Outlined.List
        ),
        BottomNavItem(
            route = StatsRoute,
            label = "Estatísticas",
            selectedIcon = Icons.Filled.BarChart,
            unselectedIcon = Icons.Outlined.BarChart
        )
    )

    val showBottomBar = currentDestination?.let { dest ->
        dest.hasRoute<TodayRoute>() ||
        dest.hasRoute<StatsRoute>() ||
        (dest.hasRoute<ManageRoute>() && navBackStackEntry?.toRoute<ManageRoute>()?.habitId == 0L)
    } ?: true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hasRoute(item.route::class) == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon
                                    else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TodayRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<TodayRoute> {
                TodayScreen(
                    onNavigateToDetail = { habitId ->
                        navController.navigate(DetailRoute(habitId))
                    },
                    onNavigateToCreate = {
                        navController.navigate(ManageRoute())
                    }
                )
            }

            composable<DetailRoute> {
                DetailScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToEdit = { habitId ->
                        navController.navigate(ManageRoute(habitId))
                    }
                )
            }

            composable<ManageRoute> {
                ManageHabitScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable<StatsRoute> {
                StatsScreen()
            }
        }
    }
}
