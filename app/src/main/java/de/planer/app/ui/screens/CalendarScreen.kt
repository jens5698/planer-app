package de.planer.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import de.planer.app.data.TaskRepository
import kotlinx.coroutines.flow.first
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

/** Wochenleiste mit Tagesauswahl; Monats-/Zeitrasteransicht folgt später. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(repository: TaskRepository, onOpenTask: (Long) -> Unit) {
    var selected by remember { mutableStateOf(LocalDate.now()) }
    val tasks by remember(selected) { repository.observeDay(selected) }.collectAsState(initial = emptyList())
    val weekStart = selected.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    TaskListContent(
        repository = repository,
        tasks = tasks,
        targetListId = { repository.observeLists().first().first().id },
        defaultDue = selected,
        onOpenTask = onOpenTask,
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(selected.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.GERMAN))) },
                    actions = {
                        IconButton(onClick = { selected = selected.minusWeeks(1) }) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Vorige Woche")
                        }
                        IconButton(onClick = { selected = selected.plusWeeks(1) }) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Nächste Woche")
                        }
                    },
                )
                Row(Modifier.fillMaxWidth()) {
                    (0L until 7L).map(weekStart::plusDays).forEach { day ->
                        FilterChip(
                            selected = day == selected,
                            onClick = { selected = day },
                            label = {
                                Text("${day.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.GERMAN)}\n${day.dayOfMonth}")
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        },
        empty = "Keine Aufgaben an diesem Tag.",
    )
}
