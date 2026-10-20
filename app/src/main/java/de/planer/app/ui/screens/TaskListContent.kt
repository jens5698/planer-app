package de.planer.app.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.planer.app.calendar.CalendarEvent
import de.planer.app.data.Task
import de.planer.app.data.TaskList
import de.planer.app.data.TaskRepository
import de.planer.app.data.TaskWithDetails
import de.planer.app.ui.components.EventRow
import de.planer.app.ui.components.QuickAddDialog
import de.planer.app.ui.components.SectionCard
import de.planer.app.ui.components.TaskRow
import de.planer.app.ui.components.completedLabel
import kotlinx.coroutines.launch
import java.time.LocalDate

/** Eine Karte in der Übersicht, z. B. ein Tag oder "Überfällig". */
data class AgendaSection(
    val key: String,
    val title: String,
    val tasks: List<TaskWithDetails>,
    val events: List<CalendarEvent> = emptyList(),
    val warn: Boolean = false,
    /** Zeilen zeigen die Erledigungszeit statt des Fälligkeitsdatums. */
    val showCompletion: Boolean = false,
    /** Künftige Termine von Serien; nur zur Ansicht, abhaken geht am aktuellen Termin. */
    val previews: List<TaskWithDetails> = emptyList(),
) {
    val count get() = tasks.size + events.size + previews.size
}

@Composable
fun rememberListsById(repository: TaskRepository): Map<Long, TaskList> {
    val lists by remember { repository.observeLists() }.collectAsState(initial = emptyList())
    return remember(lists) { lists.associateBy { it.id } }
}

/** Gemeinsame Kartenansicht mit "+"-Button für Übersicht, Listen und Kalender. Leere Karten werden ausgeblendet. */
@Composable
fun TaskListContent(
    repository: TaskRepository,
    sections: List<AgendaSection>,
    targetListId: suspend () -> Long,
    defaultDue: LocalDate?,
    onOpenTask: (Long) -> Unit,
    topBar: @Composable () -> Unit,
    empty: String,
    initiallyCollapsed: Set<String> = emptySet(),
    showAdd: Boolean = true,
) {
    val scope = rememberCoroutineScope()
    var adding by remember { mutableStateOf(false) }
    var collapsed by rememberSaveable { mutableStateOf(initiallyCollapsed.toList()) }
    val listsById = rememberListsById(repository)
    val visible = sections.filter { it.count > 0 }

    Scaffold(
        topBar = topBar,
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            if (showAdd) FloatingActionButton(
                onClick = { adding = true },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
            ) { Icon(Icons.Filled.Add, contentDescription = "Aufgabe hinzufügen") }
        },
    ) { padding ->
        if (visible.isEmpty()) {
            Text(empty, Modifier.padding(padding).padding(24.dp), color = MaterialTheme.colorScheme.outline)
        }
        LazyColumn(Modifier.fillMaxSize().padding(padding)) {
            items(visible, key = { it.key }) { section ->
                val isCollapsed = section.key in collapsed
                SectionCard(
                    title = section.title,
                    count = section.count,
                    collapsed = isCollapsed,
                    onToggle = { collapsed = if (isCollapsed) collapsed - section.key else collapsed + section.key },
                    titleColor = if (section.warn) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                ) {
                    section.tasks.forEach { item ->
                        TaskRow(
                            item = item,
                            list = listsById[item.task.listId],
                            onToggle = { scope.launch { repository.toggleCompleted(item.task) } },
                            onClick = { onOpenTask(item.task.id) },
                            dateText = if (section.showCompletion) item.task.completedAt?.let(::completedLabel) else null,
                        )
                    }
                    section.previews.forEach { item ->
                        TaskRow(
                            item = item,
                            list = listsById[item.task.listId],
                            onToggle = {},
                            onClick = { onOpenTask(item.task.id) },
                            enabled = false,
                        )
                    }
                    section.events.forEach { EventRow(it) }
                }
            }
            // Platz, damit der "+"-Button die letzte Zeile nicht verdeckt.
            item { Spacer(Modifier.height(88.dp)) }
        }
    }

    if (adding) {
        QuickAddDialog(
            defaultDue = defaultDue,
            onDismiss = { adding = false },
            onAdd = { r ->
                adding = false
                scope.launch {
                    val id = repository.addTask(
                        Task(
                            listId = targetListId(), title = r.title, dueAt = r.dueAt, priority = r.priority,
                            repeat = r.repeat, repeatUntil = r.repeatUntil,
                        ),
                    )
                    r.tags.forEach { repository.addTag(id, it) }
                }
            },
        )
    }
}
