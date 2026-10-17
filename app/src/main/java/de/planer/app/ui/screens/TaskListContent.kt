package de.planer.app.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.planer.app.data.Task
import de.planer.app.data.TaskRepository
import de.planer.app.data.TaskWithDetails
import de.planer.app.ui.components.QuickAddDialog
import de.planer.app.ui.components.TaskRow
import kotlinx.coroutines.launch
import java.time.LocalDate

/** Gemeinsame Aufgabenliste mit "+"-Button für Heute-, Listen- und Kalenderansicht. */
@Composable
fun TaskListContent(
    repository: TaskRepository,
    tasks: List<TaskWithDetails>,
    targetListId: suspend () -> Long,
    defaultDue: LocalDate?,
    onOpenTask: (Long) -> Unit,
    topBar: @Composable () -> Unit,
    empty: String,
) {
    val scope = rememberCoroutineScope()
    var adding by remember { mutableStateOf(false) }

    Scaffold(
        topBar = topBar,
        floatingActionButton = {
            FloatingActionButton(onClick = { adding = true }) { Icon(Icons.Filled.Add, contentDescription = "Aufgabe hinzufügen") }
        },
    ) { padding ->
        if (tasks.isEmpty()) {
            Text(empty, Modifier.padding(padding).padding(24.dp))
        }
        LazyColumn(Modifier.fillMaxSize().padding(padding)) {
            items(tasks, key = { it.task.id }) { item ->
                TaskRow(
                    item = item,
                    onToggle = { scope.launch { repository.toggleCompleted(item.task) } },
                    onClick = { onOpenTask(item.task.id) },
                )
            }
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
                        Task(listId = targetListId(), title = r.title, dueAt = r.dueAt, priority = r.priority),
                    )
                    r.tags.forEach { repository.addTag(id, it) }
                }
            },
        )
    }
}
