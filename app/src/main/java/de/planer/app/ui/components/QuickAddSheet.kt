package de.planer.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.planer.app.data.Priority
import de.planer.app.data.Repeat
import androidx.compose.material3.MaterialTheme
import java.time.LocalDate
import java.time.ZoneId

data class QuickAddResult(
    val title: String,
    val dueAt: Long?,
    val priority: Priority,
    val tags: List<String>,
    val repeat: Repeat = Repeat.NONE,
    val repeatUntil: Long? = null,
)

/**
 * Schnelleingabe wie in TickTick: "#tag" im Titel wird zum Tag, "!1".."!3" setzt die Priorität.
 */
fun parseQuickAdd(input: String, due: LocalDate?): QuickAddResult {
    val words = input.trim().split(Regex("\\s+"))
    val tags = words.filter { it.startsWith("#") && it.length > 1 }.map { it.drop(1) }
    val prio = when {
        "!3" in words -> Priority.HIGH
        "!2" in words -> Priority.MEDIUM
        "!1" in words -> Priority.LOW
        else -> Priority.NONE
    }
    val title = words.filterNot { (it.startsWith("#") && it.length > 1) || it in setOf("!1", "!2", "!3") }.joinToString(" ")
    val dueAt = due?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    return QuickAddResult(title, dueAt, prio, tags)
}

@Composable
fun QuickAddDialog(
    defaultDue: LocalDate?,
    onDismiss: () -> Unit,
    onAdd: (QuickAddResult) -> Unit,
) {
    var text by remember { mutableStateOf("") }
    var due by remember { mutableStateOf(defaultDue) }
    var repeat by remember { mutableStateOf(Repeat.NONE) }
    var until by remember { mutableStateOf<LocalDate?>(null) }
    val today = LocalDate.now()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Neue Aufgabe") },
        text = {
            Column {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("z. B. Milch kaufen #einkauf !2") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(due == today, { due = if (due == today) null else today }, { Text("Heute") })
                    FilterChip(due == today.plusDays(1), {
                        due = if (due == today.plusDays(1)) null else today.plusDays(1)
                    }, { Text("Morgen") })
                }
                Text("Wiederholen", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 12.dp, bottom = 4.dp))
                RepeatPicker(repeat, until, start = due ?: today) { r, u ->
                    repeat = r
                    until = u
                    // Eine Serie braucht einen ersten Termin.
                    if (r != Repeat.NONE && due == null) due = today
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = text.isNotBlank(),
                onClick = {
                    onAdd(parseQuickAdd(text, due).copy(repeat = repeat, repeatUntil = until?.toEpochDay()))
                },
            ) { Text("Hinzufügen") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Abbrechen") } },
    )
}
