package de.planer.app.ui.components

import android.content.ContentUris
import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.planer.app.calendar.CalendarEvent
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private val timeFormat = DateTimeFormatter.ofPattern("HH:mm")
private val dayMonth = DateTimeFormatter.ofPattern("d. MMM", Locale.GERMAN)

/** "18. Okt., 10:00 - 11:00", "19. Okt. - 30. Okt." oder "18. Okt., ganztägig". */
fun formatEventTime(e: CalendarEvent): String {
    return if (e.allDay) {
        val first = Instant.ofEpochMilli(e.begin).atZone(ZoneOffset.UTC).toLocalDate()
        val last = Instant.ofEpochMilli(e.end).atZone(ZoneOffset.UTC).toLocalDate().minusDays(1)
        if (last.isAfter(first)) "${first.format(dayMonth)} - ${last.format(dayMonth)}" else "${first.format(dayMonth)}, ganztägig"
    } else {
        val zone = ZoneId.systemDefault()
        val begin = Instant.ofEpochMilli(e.begin).atZone(zone)
        val end = Instant.ofEpochMilli(e.end).atZone(zone)
        "${begin.format(dayMonth)}, ${begin.format(timeFormat)} - ${end.format(timeFormat)}"
    }
}

/** Kalendertermin im Stil der Aufgabenzeilen; Antippen öffnet ihn in der Kalender-App. */
@Composable
fun EventRow(event: CalendarEvent) {
    val context = LocalContext.current
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.eventId)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.begin)
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.end)
                runCatching { context.startActivity(intent) }
            }
            .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            Icons.Outlined.Event,
            contentDescription = null,
            tint = Color(event.color),
            modifier = Modifier.padding(top = 2.dp).size(22.dp),
        )
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(event.title, style = MaterialTheme.typography.bodyLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Row(Modifier.padding(top = 2.dp)) {
                Text(formatEventTime(event), style = MaterialTheme.typography.bodyMedium, color = scheme.primary)
                Spacer(Modifier.width(12.dp))
                Text(
                    event.calendarName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.End,
                )
            }
        }
    }
}
