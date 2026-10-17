package de.planer.app.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.planer.app.ui.ListIcon
import de.planer.app.data.TaskRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(repository: TaskRepository, listId: Long, onOpenTask: (Long) -> Unit, onBack: () -> Unit) {
    val tasks by remember(listId) { repository.observeList(listId) }.collectAsState(initial = emptyList())
    val lists by remember { repository.observeLists() }.collectAsState(initial = emptyList())
    val list = lists.firstOrNull { it.id == listId }
    TaskListContent(
        repository = repository,
        sections = listOf(
            AgendaSection("open", "Offen", tasks.filter { !it.task.isCompleted }),
            AgendaSection("done", "Erledigt", tasks.filter { it.task.isCompleted }),
        ),
        initiallyCollapsed = setOf("done"),
        targetListId = { listId },
        defaultDue = null,
        onOpenTask = onOpenTask,
        topBar = {
            TopAppBar(
                title = {
                    if (list != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(ListIcon.of(list.icon).vector, contentDescription = null, tint = androidx.compose.ui.graphics.Color(list.color))
                            Spacer(Modifier.width(12.dp))
                            Text(list.name, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück") }
                },
            )
        },
        empty = "Diese Liste ist leer.",
    )
}
