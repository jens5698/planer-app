package de.planer.app.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

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

private val untilFormat = DateTimeFormatter.ofPattern("d. MMM yyyy", Locale.GERMAN)

/** Anzahl Tage von heute bis einschließlich [until]. */
fun daysUntil(today: LocalDate, until: LocalDate): Int = (ChronoUnit.DAYS.between(today, until) + 1).toInt()

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
    /** Erlaubt im Titel die Wahl "Nächste 7 Tage" oder "Bis <Datum>". */
    rangeSelectable: Boolean = false,
) {
    val today = LocalDate.now()
    // Gewählter Endtag als Epoch-Day; null = Standardzeitraum [days].
    var untilEpochDay by rememberSaveable { mutableStateOf<Long?>(null) }
    val until = untilEpochDay?.let(LocalDate::ofEpochDay)?.takeIf { !it.isBefore(today) }
    val shownDays = until?.let { daysUntil(today, it) } ?: days
    val shownTitle = until?.let { "Bis ${it.format(untilFormat)}" } ?: title
    var menuOpen by remember { mutableStateOf(false) }
    var picking by remember { mutableStateOf(false) }

    val end = today.plusDays(shownDays.toLong())
    val tasks by remember(today, shownDays) { repository.observeOpenUntil(end) }.collectAsState(initial = emptyList())
    val calendarEnabled by calendar.enabled.collectAsState()
    val events by produceState(emptyList<CalendarEvent>(), calendarEnabled, today, shownDays) {
        value = calendar.events(today, end)
    }
    TaskListContent(
        repository = repository,
        sections = buildAgenda(today, shownDays, tasks, events),
        targetListId = { repository.observeLists().first().first().id },
        defaultDue = today,
        onOpenTask = onOpenTask,
        topBar = {
            TopAppBar(
                title = {
                    if (!rangeSelectable) {
                        Text(shownTitle, fontWeight = FontWeight.Bold)
                    } else {
                        Box {
                            Row(Modifier.clickable { menuOpen = true }, verticalAlignment = Alignment.CenterVertically) {
                                Text(shownTitle, fontWeight = FontWeight.Bold)
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Zeitraum wählen")
                            }
                            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                                DropdownMenuItem(
                                    text = { Text(title, fontWeight = if (until == null) FontWeight.Bold else null) },
                                    onClick = { untilEpochDay = null; menuOpen = false },
                                )
                                DropdownMenuItem(
                                    text = { Text("Bis zu einem Tag …", fontWeight = if (until != null) FontWeight.Bold else null) },
                                    onClick = { menuOpen = false; picking = true },
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onSearch) { Icon(Icons.Filled.Search, contentDescription = "Suchen") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        empty = if (until != null) "Keine Aufgaben bis ${until.format(untilFormat)}." else empty,
    )

    if (picking) {
        // DatePicker rechnet in UTC-Mitternacht; nur heute und spätere Tage wählbar.
        val todayUtc = today.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        val state = rememberDatePickerState(
            initialSelectedDateMillis = (until ?: today.plusDays(13)).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis >= todayUtc
                override fun isSelectableYear(year: Int) = year >= today.year
            },
        )
        DatePickerDialog(
            onDismissRequest = { picking = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        untilEpochDay = Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate().toEpochDay()
                    }
                    picking = false
                }) { Text("Anzeigen") }
            },
            dismissButton = { TextButton(onClick = { picking = false }) { Text("Abbrechen") } },
        ) { DatePicker(state, title = { Text("Aufgaben anzeigen bis", Modifier.padding(start = 24.dp, top = 16.dp)) }) }
    }
}
