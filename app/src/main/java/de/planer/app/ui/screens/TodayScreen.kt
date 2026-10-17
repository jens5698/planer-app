package de.planer.app.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import de.planer.app.calendar.CalendarEvent
import de.planer.app.calendar.CalendarRepository
import de.planer.app.data.TaskRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    repository: TaskRepository,
    calendar: CalendarRepository,
    onOpenTask: (Long) -> Unit,
    onSearch: () -> Unit,
) {
    val flow = remember { repository.observeToday() }
    val tasks by flow.collectAsState(initial = emptyList())
    val today = LocalDate.now()
    val calendarEnabled by calendar.enabled.collectAsState()
    val events by produceState(emptyList<CalendarEvent>(), calendarEnabled, today) {
        value = calendar.events(today, today.plusDays(1)).filter { it.occursOn(today) }
    }
    TaskListContent(
        repository = repository,
        tasks = tasks,
        targetListId = { repository.observeLists().first().first().id },
        defaultDue = today,
        onOpenTask = onOpenTask,
        topBar = {
            TopAppBar(
                title = { Text("Heute") },
                actions = {
                    IconButton(onClick = onSearch) { Icon(Icons.Filled.Search, contentDescription = "Suchen") }
                },
            )
        },
        empty = "Nichts mehr für heute.",
        events = events,
    )
}
