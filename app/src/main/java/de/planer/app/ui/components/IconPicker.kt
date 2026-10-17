package de.planer.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.planer.app.ui.ListColors
import de.planer.app.ui.ListIcon

/** Symbolraster; jedes Symbol in seiner Vorschlagsfarbe, das gewählte in der Listenfarbe hinterlegt. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IconPicker(selected: ListIcon, color: Long, onSelect: (ListIcon) -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        ListIcon.entries.forEach { icon ->
            val isSelected = icon == selected
            Box(
                Modifier
                    .size(40.dp)
                    .background(if (isSelected) Color(color) else Color.Transparent, CircleShape)
                    .clickable { onSelect(icon) },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon.vector,
                    contentDescription = icon.label,
                    tint = if (isSelected) Color.White else Color(icon.color),
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorPicker(selected: Long, onSelect: (Long) -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ListColors.all.forEach { c ->
            Box(
                Modifier
                    .size(32.dp)
                    .background(Color(c), CircleShape)
                    .then(
                        if (c == selected) Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape) else Modifier,
                    )
                    .clickable { onSelect(c) },
                contentAlignment = Alignment.Center,
            ) {
                if (c == selected) Icon(Icons.Filled.Check, contentDescription = "Gewählt", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}
