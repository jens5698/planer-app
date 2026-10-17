package de.planer.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.planer.app.data.Priority
import de.planer.app.data.TaskRepository
import de.planer.app.ui.components.formatDue
import de.planer.app.ui.components.priorityColor
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(repository: TaskRepository, taskId: Long, onBack: () -> Unit) {
    val details by remember(taskId) { repository.observeTask(taskId) }.collectAsState(initial = null)
    val scope = rememberCoroutineScope()
    val item = details ?: return
    val task = item.task

    var title by remember(taskId) { mutableStateOf(task.title) }
    var notes by remember(taskId) { mutableStateOf(task.notes) }
    var newChecklist by remember { mutableStateOf("") }
    var newTag by remember { mutableStateOf("") }
    var pickDate by remember { mutableStateOf(false) }
    var pickReminder by remember { mutableStateOf(false) }

    // Titel/Notizen beim Verlassen speichern.
    val saveAndBack = {
        scope.launch {
            repository.updateTask(task.copy(title = title.ifBlank { task.title }, notes = notes))
            onBack()
        }
        Unit
    }
    BackHandler(onBack = saveAndBack)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = saveAndBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück") }
                },
                actions = {
                    IconButton(onClick = { scope.launch { repository.deleteTask(task); onBack() } }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Löschen")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(task.isCompleted, onCheckedChange = { scope.launch { repository.toggleCompleted(task) } })
                OutlinedTextField(title, { title = it }, Modifier.fillMaxWidth(), textStyle = MaterialTheme.typography.titleLarge)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = { pickDate = true },
                    label = { Text(task.dueAt?.let { "Fällig ${formatDue(it)}" } ?: "Datum") },
                )
                AssistChip(
                    onClick = { pickReminder = true },
                    label = {
                        Text(
                            task.remindAt?.let {
                                "⏰ " + Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault())
                                    .format(DateTimeFormatter.ofPattern("dd.MM. HH:mm"))
                            } ?: "Erinnerung",
                        )
                    },
                )
            }

            Text("Priorität", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Priority.entries.forEach { p ->
                    FilterChip(
                        selected = task.priority == p,
                        onClick = { scope.launch { repository.updateTask(task.copy(priority = p)) } },
                        label = {
                            Text(
                                when (p) {
                                    Priority.NONE -> "Keine"; Priority.LOW -> "Niedrig"
                                    Priority.MEDIUM -> "Mittel"; Priority.HIGH -> "Hoch"
                                },
                                color = priorityColor(p),
                            )
                        },
                    )
                }
            }

            OutlinedTextField(notes, { notes = it }, Modifier.fillMaxWidth(), label = { Text("Notizen") }, minLines = 3)

            Text("Checkliste", style = MaterialTheme.typography.labelLarge)
            item.checklist.sortedBy { it.sortOrder }.forEach { c ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(c.done, onCheckedChange = { scope.launch { repository.toggleChecklistItem(c) } })
                    Text(c.title, Modifier.weight(1f))
                    IconButton(onClick = { scope.launch { repository.deleteChecklistItem(c) } }) {
                        Icon(Icons.Filled.Close, contentDescription = "Entfernen")
                    }
                }
            }
            OutlinedTextField(
                newChecklist, { newChecklist = it }, Modifier.fillMaxWidth(),
                placeholder = { Text("Eintrag hinzufügen") }, singleLine = true,
                trailingIcon = {
                    TextButton(enabled = newChecklist.isNotBlank(), onClick = {
                        val t = newChecklist.trim(); newChecklist = ""
                        scope.launch { repository.addChecklistItem(task.id, t) }
                    }) { Text("+") }
                },
            )

            Text("Tags", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item.tags.forEach { tag ->
                    InputChip(
                        selected = false,
                        onClick = { scope.launch { repository.removeTag(task.id, tag) } },
                        label = { Text("#${tag.name}") },
                        trailingIcon = { Icon(Icons.Filled.Close, contentDescription = "Tag entfernen") },
                    )
                }
            }
            OutlinedTextField(
                newTag, { newTag = it }, Modifier.fillMaxWidth(),
                placeholder = { Text("Tag hinzufügen") }, singleLine = true,
                trailingIcon = {
                    TextButton(enabled = newTag.isNotBlank(), onClick = {
                        val t = newTag; newTag = ""
                        scope.launch { repository.addTag(task.id, t) }
                    }) { Text("+") }
                },
            )
        }
    }

    if (pickDate) {
        // DatePicker arbeitet in UTC-Mitternacht.
        val state = rememberDatePickerState(
            initialSelectedDateMillis = task.dueAt?.let {
                Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    .atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            },
        )
        DatePickerDialog(
            onDismissRequest = { pickDate = false },
            confirmButton = {
                TextButton(onClick = {
                    val due = state.selectedDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
                            .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    }
                    scope.launch { repository.updateTask(task.copy(dueAt = due)) }
                    pickDate = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = {
                    scope.launch { repository.updateTask(task.copy(dueAt = null)) }
                    pickDate = false
                }) { Text("Entfernen") }
            },
        ) { DatePicker(state) }
    }

    if (pickReminder) {
        val zone = ZoneId.systemDefault()
        val initial = task.remindAt?.let { Instant.ofEpochMilli(it).atZone(zone).toLocalTime() } ?: LocalTime.of(9, 0)
        val state = rememberTimePickerState(initial.hour, initial.minute, is24Hour = true)
        AlertDialog(
            onDismissRequest = { pickReminder = false },
            title = { Text("Erinnerung am Fälligkeitstag") },
            text = { TimePicker(state) },
            confirmButton = {
                TextButton(onClick = {
                    val day = task.dueAt?.let { Instant.ofEpochMilli(it).atZone(zone).toLocalDate() }
                        ?: java.time.LocalDate.now(zone)
                    val at = day.atTime(state.hour, state.minute).atZone(zone).toInstant().toEpochMilli()
                    scope.launch { repository.updateTask(task.copy(remindAt = at)) }
                    pickReminder = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = {
                    scope.launch { repository.updateTask(task.copy(remindAt = null)) }
                    pickReminder = false
                }) { Text("Entfernen") }
            },
        )
    }

}
