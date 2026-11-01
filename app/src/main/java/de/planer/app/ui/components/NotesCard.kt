package de.planer.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.StickyNote2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.planer.app.data.ListNote

private val NoteYellow = Color(0xFFFBC02D)

/** Karte "Notizen" oben in einer Liste: Vorschau jeder Notiz und eine Zeile zum Anlegen. */
@Composable
fun NotesCard(
    notes: List<ListNote>,
    collapsed: Boolean,
    onToggle: () -> Unit,
    onOpen: (ListNote) -> Unit,
    onAdd: () -> Unit,
) {
    SectionCard("Notizen", notes.size, collapsed, onToggle) {
        notes.forEach { note ->
            Row(
                Modifier.fillMaxWidth().clickable { onOpen(note) }.padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(Icons.Outlined.StickyNote2, contentDescription = null, tint = NoteYellow, modifier = Modifier.padding(top = 2.dp).size(22.dp))
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    if (note.title.isNotBlank()) {
                        Text(note.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    if (note.body.isNotBlank()) {
                        Text(
                            note.body,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
        Row(
            Modifier.fillMaxWidth().clickable(onClick = onAdd).padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(14.dp))
            Text("Notiz hinzufügen", color = MaterialTheme.colorScheme.primary)
        }
    }
}
