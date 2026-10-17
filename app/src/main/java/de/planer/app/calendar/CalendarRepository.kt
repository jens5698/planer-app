package de.planer.app.calendar

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract.Instances
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

/** Termin aus einem Gerätekalender (z. B. dem synchronisierten Google Kalender). Nur lesend. */
data class CalendarEvent(
    val eventId: Long,
    val title: String,
    val begin: Long,
    val end: Long,
    val allDay: Boolean,
    val color: Int,
    val calendarName: String,
) {
    /** Ganztägige Termine speichert Android in UTC, deshalb getrennt behandeln. */
    fun occursOn(day: LocalDate, zone: ZoneId = ZoneId.systemDefault()): Boolean =
        if (allDay) {
            val first = Instant.ofEpochMilli(begin).atZone(ZoneOffset.UTC).toLocalDate()
            val lastExclusive = Instant.ofEpochMilli(end).atZone(ZoneOffset.UTC).toLocalDate()
            !day.isBefore(first) && (day.isBefore(lastExclusive) || first == lastExclusive && day == first)
        } else {
            val start = day.atStartOfDay(zone).toInstant().toEpochMilli()
            val stop = day.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
            begin < stop && end > start || begin == end && begin in start until stop
        }
}

/**
 * Liest Termine über den Android-Kalenderanbieter. Damit erscheinen alle Kalender,
 * die auf dem Gerät synchronisiert sind, ohne eigene Google-Anmeldung in der App.
 */
class CalendarRepository(private val context: Context) {
    private val _enabled = MutableStateFlow(hasPermission())
    val enabled: StateFlow<Boolean> = _enabled

    fun refreshPermission() {
        _enabled.value = hasPermission()
    }

    private fun hasPermission() =
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED

    suspend fun events(from: LocalDate, toExclusive: LocalDate, zone: ZoneId = ZoneId.systemDefault()): List<CalendarEvent> {
        if (!hasPermission()) return emptyList()
        // Einen Tag Puffer, damit ganztägige UTC-Termine an den Rändern nicht fehlen.
        val start = from.minusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
        val end = toExclusive.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
        val uri = Instances.CONTENT_URI.buildUpon().also {
            ContentUris.appendId(it, start)
            ContentUris.appendId(it, end)
        }.build()
        val projection = arrayOf(
            Instances.EVENT_ID, Instances.TITLE, Instances.BEGIN, Instances.END,
            Instances.ALL_DAY, Instances.DISPLAY_COLOR, Instances.CALENDAR_DISPLAY_NAME,
        )
        return withContext(Dispatchers.IO) {
            val result = mutableListOf<CalendarEvent>()
            context.contentResolver.query(uri, projection, "${Instances.VISIBLE} = 1", null, "${Instances.BEGIN} ASC")
                ?.use { c ->
                    while (c.moveToNext()) {
                        result += CalendarEvent(
                            eventId = c.getLong(0),
                            title = c.getString(1) ?: "(Ohne Titel)",
                            begin = c.getLong(2),
                            end = c.getLong(3),
                            allDay = c.getInt(4) == 1,
                            color = c.getInt(5),
                            calendarName = c.getString(6) ?: "",
                        )
                    }
                }
            result
        }
    }
}
