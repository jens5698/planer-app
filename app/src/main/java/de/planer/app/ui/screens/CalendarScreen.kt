package de.planer.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.planer.app.calendar.CalendarEvent
import de.planer.app.calendar.CalendarRepository
import de.planer.app.data.TaskRepository
import kotlinx.coroutines.flow.first
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

/** Sichtbare Tage: eine Woche oder das 6-Wochen-Raster eines Monats, jeweils ab Montag. */
fun visibleDays(selected: LocalDate, month: Boolean): List<LocalDate> {
    val first = if (month) selected.withDayOfMonth(1) else selected
    val start = first.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    return (0L until if (month) 42L else 7L).map(start::plusDays)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(repository: TaskRepository, calendar: CalendarRepository, onOpenTask: (Long) -> Unit) {
    var selected by remember { mutableStateOf(LocalDate.now()) }
    var monthView by rememberSaveable { mutableStateOf(true) }
    val days = visibleDays(selected, monthView)
    val rangeStart = days.first()
    val rangeEnd = days.last().plusDays(1)
    val zone = ZoneId.systemDefault()

    val rangeTasks by remember(rangeStart, rangeEnd) { repository.observeRange(rangeStart, rangeEnd) }
        .collectAsState(initial = emptyList())
    val recurring by remember { repository.observeOpenRecurring() }.collectAsState(initial = emptyList())
    val previews = seriesPreviews(recurring, rangeStart, rangeEnd)
    val calendarEnabled by calendar.enabled.collectAsState()
    val rangeEvents by produceState(emptyList<CalendarEvent>(), rangeStart, rangeEnd, calendarEnabled) {
        value = calendar.events(rangeStart, rangeEnd)
    }

    val taskDays = rangeTasks.mapNotNull { t ->
        t.task.dueAt?.takeIf { !t.task.isCompleted }?.let { Instant.ofEpochMilli(it).atZone(zone).toLocalDate() }
    }.toSet() + previews.keys
    val dayTasks = rangeTasks.filter { t ->
        t.task.dueAt?.let { Instant.ofEpochMilli(it).atZone(zone).toLocalDate() } == selected
    }
    val dayEvents = rangeEvents.filter { it.occursOn(selected) }

    TaskListContent(
        repository = repository,
        sections = listOf(AgendaSection("day", de.planer.app.ui.components.dayTitle(selected), dayTasks, dayEvents, previews = previews[selected].orEmpty())),
        targetListId = { repository.observeLists().first().first().id },
        defaultDue = selected,
        onOpenTask = onOpenTask,
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(selected.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.GERMAN))) },
                    actions = {
                        TextButton(onClick = { selected = LocalDate.now() }) { Text("Heute") }
                        IconButton(onClick = { selected = if (monthView) selected.minusMonths(1) else selected.minusWeeks(1) }) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Zurück")
                        }
                        IconButton(onClick = { selected = if (monthView) selected.plusMonths(1) else selected.plusWeeks(1) }) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Weiter")
                        }
                    },
                )
                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    SegmentedButton(
                        selected = !monthView, onClick = { monthView = false },
                        shape = SegmentedButtonDefaults.itemShape(0, 2),
                    ) { Text("Woche") }
                    SegmentedButton(
                        selected = monthView, onClick = { monthView = true },
                        shape = SegmentedButtonDefaults.itemShape(1, 2),
                    ) { Text("Monat") }
                }
                Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    days.take(7).forEach { d ->
                        Text(
                            d.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.GERMAN),
                            Modifier.weight(1f),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        )
                    }
                }
                days.chunked(7).forEach { week ->
                    Row(Modifier.fillMaxWidth()) {
                        week.forEach { d ->
                            DayCell(
                                day = d,
                                selected = d == selected,
                                inMonth = !monthView || d.month == selected.month,
                                hasTasks = d in taskDays,
                                hasEvents = rangeEvents.any { it.occursOn(d) },
                                onClick = { selected = d },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        },
        empty = "Keine Aufgaben oder Termine an diesem Tag.",
    )
}

@Composable
private fun DayCell(
    day: LocalDate,
    selected: Boolean,
    inMonth: Boolean,
    hasTasks: Boolean,
    hasEvents: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val isToday = day == LocalDate.now()
    Column(
        modifier.aspectRatio(1.3f).clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            Modifier.size(32.dp).background(if (selected) scheme.primary else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                day.dayOfMonth.toString(),
                color = when {
                    selected -> scheme.onPrimary
                    isToday -> scheme.primary
                    !inMonth -> scheme.outline.copy(alpha = 0.5f)
                    else -> scheme.onSurface
                },
                fontWeight = if (isToday) FontWeight.Bold else null,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.padding(top = 2.dp)) {
            if (hasTasks) Box(Modifier.size(4.dp).background(scheme.primary, CircleShape))
            if (hasEvents) Box(Modifier.size(4.dp).background(scheme.tertiary, CircleShape))
        }
    }
}
