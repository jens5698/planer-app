package de.planer.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import de.planer.app.ui.ListIcon

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IconPicker(selected: ListIcon, onSelect: (ListIcon) -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        ListIcon.entries.forEach { icon ->
            FilledIconToggleButton(checked = icon == selected, onCheckedChange = { onSelect(icon) }) {
                Icon(icon.vector, contentDescription = icon.label)
            }
        }
    }
}
