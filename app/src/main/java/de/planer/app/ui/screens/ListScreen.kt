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
import de.planer.app.data.TaskRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(repository: TaskRepository, listId: Long, onOpenTask: (Long) -> Unit, onBack: () -> Unit) {
    val tasks by remember(listId) { repository.observeList(listId) }.collectAsState(initial = emptyList())
    val lists by remember { repository.observeLists() }.collectAsState(initial = emptyList())
    val name = lists.firstOrNull { it.id == listId }?.name.orEmpty()
    TaskListContent(
        repository = repository,
        tasks = tasks,
        targetListId = { listId },
        defaultDue = null,
        onOpenTask = onOpenTask,
        topBar = {
            TopAppBar(
                title = { Text(name) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück") }
                },
            )
        },
        empty = "Diese Liste ist leer.",
    )
}
