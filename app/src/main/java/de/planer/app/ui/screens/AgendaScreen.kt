package de.planer.app.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import de.planer.app.calendar.CalendarEvent
import de.planer.app.calendar.CalendarRepository
import de.planer.app.data.TaskRepository
import de.planer.app.data.TaskWithDetails
import de.planer.app.ui.components.dayTitle
import de.planer.app.ui.components.toLocalDate
import kotlinx.coroutines.flow.first
import java.time.LocalDate

/**
 * Karten "Überfällig" und je Tag mit Aufgaben und Terminen.
 * Mehrtägige Termine erscheinen nur am ersten sichtbaren Tag.
 */
fun buildAgenda(
    today: LocalDate,
    days: Int,
    tasks: List<TaskWithDetails>,
    events: List<CalendarEvent>,
): List<AgendaSection> = buildList {
    val overdue = tasks.filter { it.task.dueAt!!.toLocalDate().isBefore(today) }
    if (overdue.isNotEmpty()) add(AgendaSection("overdue", "Überfällig", overdue, warn = true))
    val shown = mutableSetOf<Pair<Long, Long>>()
    for (i in 0 until days) {
        val day = today.plusDays(i.toLong())
        val dayTasks = tasks.filter { it.task.dueAt!!.toLocalDate() == day }
        val dayEvents = events.filter { it.occursOn(day) && shown.add(it.eventId to it.begin) }
        add(AgendaSection(day.toString(), dayTitle(day, today), dayTasks, dayEvents))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(
    repository: TaskRepository,
    calendar: CalendarRepository,
    days: Int,
    title: String,
    empty: String,
    onOpenTask: (Long) -> Unit,
    onSearch: () -> Unit,
) {
    val today = LocalDate.now()
    val end = today.plusDays(days.toLong())
    val tasks by remember(today, days) { repository.observeOpenUntil(end) }.collectAsState(initial = emptyList())
    val calendarEnabled by calendar.enabled.collectAsState()
    val events by produceState(emptyList<CalendarEvent>(), calendarEnabled, today, days) {
        value = calendar.events(today, end)
    }
    TaskListContent(
        repository = repository,
        sections = buildAgenda(today, days, tasks, events),
        targetListId = { repository.observeLists().first().first().id },
        defaultDue = today,
        onOpenTask = onOpenTask,
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onSearch) { Icon(Icons.Filled.Search, contentDescription = "Suchen") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        empty = empty,
    )
}
