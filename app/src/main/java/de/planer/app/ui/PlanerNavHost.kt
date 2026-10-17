package de.planer.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.planer.app.calendar.CalendarRepository
import de.planer.app.data.TaskRepository
import de.planer.app.ui.screens.CalendarScreen
import de.planer.app.ui.screens.ListScreen
import de.planer.app.ui.screens.ListsScreen
import de.planer.app.ui.screens.SearchScreen
import de.planer.app.ui.screens.TaskDetailScreen
import de.planer.app.ui.screens.TodayScreen

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    Tab("today", "Heute", Icons.Filled.Today),
    Tab("lists", "Listen", Icons.AutoMirrored.Filled.List),
    Tab("calendar", "Kalender", Icons.Filled.CalendarMonth),
)

@Composable
fun PlanerNavHost(repository: TaskRepository, calendar: CalendarRepository) {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val current = backStack?.destination?.route

    Scaffold(
        bottomBar = {
            if (tabs.any { it.route == current }) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = current == tab.route,
                            onClick = {
                                nav.navigate(tab.route) {
                                    popUpTo("today") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = null) },
                            label = { Text(tab.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        val openTask = { id: Long -> nav.navigate("task/$id") }
        val openSearch = { nav.navigate("search") }
        NavHost(nav, startDestination = "today", modifier = Modifier.padding(padding)) {
            composable("today") { TodayScreen(repository, calendar, openTask, openSearch) }
            composable("lists") { ListsScreen(repository, onOpenList = { nav.navigate("list/$it") }, onSearch = openSearch) }
            composable("calendar") { CalendarScreen(repository, calendar, openTask) }
            composable("search") { SearchScreen(repository, openTask, onBack = { nav.popBackStack() }) }
            composable("list/{id}", arguments = listOf(navArgument("id") { type = NavType.LongType })) {
                ListScreen(repository, it.arguments!!.getLong("id"), openTask, onBack = { nav.popBackStack() })
            }
            composable("task/{id}", arguments = listOf(navArgument("id") { type = NavType.LongType })) {
                TaskDetailScreen(repository, it.arguments!!.getLong("id"), onBack = { nav.popBackStack() })
            }
        }
    }
}
