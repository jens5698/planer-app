package de.planer.app.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import de.planer.app.data.TaskList
import de.planer.app.data.TaskRepository
import kotlinx.coroutines.launch

/** Sicherheitsabfrage vor dem Löschen einer Liste mit Anzahl der offenen Aufgaben. */
@Composable
fun DeleteListDialog(
    repository: TaskRepository,
    list: TaskList,
    onDismiss: () -> Unit,
    onDeleted: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val open by produceState<Int?>(null, list.id) { value = repository.countOpenInList(list.id) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Liste „${list.name}“ löschen?") },
        text = {
            Text(
                when (open) {
                    null -> ""
                    0 -> "Die Liste und ihre erledigten Aufgaben werden gelöscht."
                    1 -> "Die Liste und 1 offene Aufgabe werden endgültig gelöscht."
                    else -> "Die Liste und $open offene Aufgaben werden endgültig gelöscht."
                },
            )
        },
        confirmButton = {
            TextButton(onClick = {
                scope.launch {
                    repository.deleteList(list)
                    onDeleted()
                }
                onDismiss()
            }) { Text("Löschen", color = MaterialTheme.colorScheme.error) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Abbrechen") } },
    )
}
