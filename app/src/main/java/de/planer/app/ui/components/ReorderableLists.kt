package de.planer.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.zIndex
import de.planer.app.data.TaskList

/** Tauscht das Element an [index] mit seinem Nachbarn in Richtung [step] (+1 oder -1). */
fun <T> List<T>.swapped(index: Int, step: Int): List<T> =
    toMutableList().also { val other = index + step; it[index] = this[other]; it[other] = this[index] }

/**
 * Listen untereinander, per Ziehen am Griff umsortierbar. [row] bekommt den
 * Modifier für den Griff; nach dem Loslassen wird [onReorder] mit der neuen Reihenfolge gerufen.
 */
@Composable
fun ReorderableLists(
    lists: List<TaskList>,
    onReorder: (List<Long>) -> Unit,
    row: @Composable (list: TaskList, handle: Modifier) -> Unit,
) {
    var order by remember(lists) { mutableStateOf(lists) }
    var draggingId by remember { mutableStateOf<Long?>(null) }
    var offset by remember { mutableFloatStateOf(0f) }
    val heights = remember { mutableStateMapOf<Long, Int>() }

    fun finish() {
        if (order.map { it.id } != lists.map { it.id }) onReorder(order.map { it.id })
        draggingId = null
        offset = 0f
    }

    Column {
        order.forEach { list ->
            val dragging = list.id == draggingId
            val handle = Modifier.pointerInput(list.id, lists) {
                detectDragGestures(
                    onDragStart = { draggingId = list.id; offset = 0f },
                    onDragEnd = { finish() },
                    onDragCancel = { finish() },
                    onDrag = { change, amount ->
                        change.consume()
                        offset += amount.y
                        val index = order.indexOfFirst { it.id == list.id }
                        // Sobald die Zeile über die Hälfte des Nachbarn gezogen ist, Plätze tauschen.
                        if (offset > 0 && index < order.lastIndex) {
                            val h = heights[order[index + 1].id] ?: return@detectDragGestures
                            if (offset > h / 2f) { order = order.swapped(index, 1); offset -= h }
                        } else if (offset < 0 && index > 0) {
                            val h = heights[order[index - 1].id] ?: return@detectDragGestures
                            if (-offset > h / 2f) { order = order.swapped(index, -1); offset += h }
                        }
                    },
                )
            }
            Box(
                Modifier
                    .onSizeChanged { heights[list.id] = it.height }
                    .zIndex(if (dragging) 1f else 0f)
                    .graphicsLayer { translationY = if (dragging) offset else 0f }
                    .background(if (dragging) MaterialTheme.colorScheme.surfaceContainerHigh else Color.Transparent),
            ) {
                row(list, handle)
            }
        }
    }
}
