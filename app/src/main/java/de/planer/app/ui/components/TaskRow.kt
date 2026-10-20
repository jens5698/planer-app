package de.planer.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.planer.app.data.Priority
import de.planer.app.data.Repeat
import de.planer.app.data.TaskList
import de.planer.app.data.TaskWithDetails
import de.planer.app.ui.ListIcon
import java.time.LocalDate

fun priorityColor(p: Priority): Color = when (p) {
    Priority.HIGH -> Color(0xFFE5484D)
    Priority.MEDIUM -> Color(0xFFFAA80C)
    Priority.LOW -> Color(0xFF4772FA)
    Priority.NONE -> Color(0xFF8A8A8F)
}

fun formatDue(epochMillis: Long): String = relativeDate(epochMillis.toLocalDate())

/**
 * Aufgabenzeile im TickTick-Stil: Checkbox in Prioritätsfarbe, Titel,
 * darunter Datum (blau, überfällig rot) und rechts die Liste mit Symbol.
 */
@Composable
fun TaskRow(
    item: TaskWithDetails,
    list: TaskList?,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    /** Ersetzt das Fälligkeitsdatum, z. B. durch "Erledigt 14:32". */
    dateText: String? = null,
    /** false für Vorschau-Termine einer Serie: Checkbox aus, Zeile abgeschwächt. */
    enabled: Boolean = true,
) {
    val task = item.task
    val scheme = MaterialTheme.colorScheme
    val dueDay = task.dueAt?.toLocalDate()
    val overdue = dueDay != null && !task.isCompleted && dueDay.isBefore(LocalDate.now())
    Row(
        modifier = Modifier.fillMaxWidth().alpha(if (enabled) 1f else 0.55f).clickable(onClick = onClick).padding(start = 8.dp, end = 20.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { onToggle() },
            enabled = enabled,
            colors = CheckboxDefaults.colors(
                uncheckedColor = priorityColor(task.priority),
                checkedColor = scheme.outline,
            ),
        )
        Column(Modifier.weight(1f).padding(top = 10.dp)) {
            Text(
                task.title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (task.isCompleted) scheme.outline else scheme.onSurface,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(Modifier.padding(top = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                val meta = buildList {
                    if (task.repeat != Repeat.NONE) add("↻")
                    if (item.checklist.isNotEmpty()) add("${item.checklist.count { it.done }}/${item.checklist.size}")
                    addAll(item.tags.map { "#${it.name}" })
                }
                if (dateText != null) {
                    Text(dateText, style = MaterialTheme.typography.bodyMedium, color = scheme.tertiary)
                    if (meta.isNotEmpty()) Spacer(Modifier.width(8.dp))
                } else if (dueDay != null) {
                    Text(
                        relativeDate(dueDay),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (overdue) scheme.error else scheme.primary,
                    )
                    if (meta.isNotEmpty()) Spacer(Modifier.width(8.dp))
                }
                Text(
                    meta.joinToString("  "),
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.outline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (list != null) {
                    val icon = ListIcon.of(list.icon)
                    Icon(icon.vector, contentDescription = null, tint = Color(list.color), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        list.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
