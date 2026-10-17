package de.planer.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import de.planer.app.data.Priority
import de.planer.app.data.TaskWithDetails
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun priorityColor(p: Priority): Color = when (p) {
    Priority.HIGH -> Color(0xFFD52B24)
    Priority.MEDIUM -> Color(0xFFFAA80C)
    Priority.LOW -> Color(0xFF4772FA)
    Priority.NONE -> Color.Gray
}

private val dateFormat = DateTimeFormatter.ofPattern("dd.MM.")

fun formatDue(epochMillis: Long): String =
    Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormat)

@Composable
fun TaskRow(
    item: TaskWithDetails,
    onToggle: () -> Unit,
    onClick: () -> Unit,
) {
    val task = item.task
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(uncheckedColor = priorityColor(task.priority)),
        )
        Column(Modifier.weight(1f)) {
            Text(
                task.title,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
            )
            val meta = buildList {
                if (item.checklist.isNotEmpty()) add("${item.checklist.count { it.done }}/${item.checklist.size}")
                addAll(item.tags.map { "#${it.name}" })
            }
            if (meta.isNotEmpty()) {
                Text(meta.joinToString("  "), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            }
        }
        task.dueAt?.let {
            Text(formatDue(it), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}
