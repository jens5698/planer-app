package de.planer.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.planer.app.data.TaskRepository
import de.planer.app.data.TaskWithDetails
import de.planer.app.ui.components.dayTitle
import de.planer.app.ui.components.toLocalDate
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Zeitraum rückwärts ab heute (rollierend, heute eingeschlossen). */
enum class CompletedRange(val label: String) {
    WEEK("Letzte Woche"),
    MONTH("Letzter Monat"),
    YEAR("Letztes Jahr");

    fun since(today: LocalDate): LocalDate = when (this) {
        WEEK -> today.minusDays(6)
        MONTH -> today.minusMonths(1).plusDays(1)
        YEAR -> today.minusYears(1).plusDays(1)
    }
}

private val monthTitle = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.GERMAN)

/** Woche und Monat gruppiert nach Tag, das Jahr nach Monat. Neueste zuerst. */
fun buildCompleted(range: CompletedRange, tasks: List<TaskWithDetails>, today: LocalDate): List<AgendaSection> {
    val byDay = range != CompletedRange.YEAR
    return tasks
        .groupBy { t ->
            val day = t.task.completedAt!!.toLocalDate()
            if (byDay) day else day.withDayOfMonth(1)
        }
        .toSortedMap(compareByDescending { it })
        .map { (key, items) ->
            AgendaSection(
                key = key.toString(),
                title = if (byDay) dayTitle(key, today) else key.format(monthTitle),
                tasks = items,
                showCompletion = true,
            )
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompletedScreen(repository: TaskRepository, onOpenTask: (Long) -> Unit, onBack: () -> Unit) {
    var range by rememberSaveable { mutableStateOf(CompletedRange.WEEK) }
    var menuOpen by remember { mutableStateOf(false) }
    val today = LocalDate.now()
    val tasks by remember(range, today) { repository.observeCompletedSince(range.since(today)) }
        .collectAsState(initial = emptyList())

    TaskListContent(
        repository = repository,
        sections = buildCompleted(range, tasks, today),
        targetListId = { repository.observeLists().first().first().id },
        defaultDue = null,
        onOpenTask = onOpenTask,
        showAdd = false,
        topBar = {
            TopAppBar(
                title = { Text("Erledigt", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück") }
                },
                actions = {
                    Box {
                        TextButton(onClick = { menuOpen = true }) {
                            Text(range.label)
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Zeitraum wählen")
                        }
                        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                            CompletedRange.entries.forEach { r ->
                                DropdownMenuItem(
                                    text = { Text(r.label, fontWeight = if (r == range) FontWeight.Bold else null) },
                                    onClick = { range = r; menuOpen = false },
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        empty = "Im Zeitraum „${range.label}“ wurde nichts erledigt.",
    )
}
