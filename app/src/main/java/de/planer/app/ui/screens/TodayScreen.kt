package de.planer.app.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import de.planer.app.data.TaskRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(repository: TaskRepository, onOpenTask: (Long) -> Unit) {
    val flow = remember { repository.observeToday() }
    val tasks by flow.collectAsState(initial = emptyList())
    TaskListContent(
        repository = repository,
        tasks = tasks,
        targetListId = { repository.observeLists().first().first().id },
        defaultDue = LocalDate.now(),
        onOpenTask = onOpenTask,
        topBar = { TopAppBar(title = { Text("Heute") }) },
        empty = "Nichts mehr für heute.",
    )
}
