package de.planer.app.ui.components

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val shortDay = DateTimeFormatter.ofPattern("EE", Locale.GERMAN)
private val dayMonth = DateTimeFormatter.ofPattern("d. MMM", Locale.GERMAN)

fun Long.toLocalDate(zone: ZoneId = ZoneId.systemDefault()): LocalDate =
    Instant.ofEpochMilli(this).atZone(zone).toLocalDate()

/** "Heute", "Morgen", "Gestern" oder "19. Okt." – wie die blaue Datumszeile in TickTick. */
fun relativeDate(day: LocalDate, today: LocalDate = LocalDate.now()): String = when (day) {
    today -> "Heute"
    today.plusDays(1) -> "Morgen"
    today.minusDays(1) -> "Gestern"
    else -> day.format(dayMonth)
}

/** Kartenüberschrift: "Sa., Heute", "So., Morgen", "Mo., 19. Okt.". */
fun dayTitle(day: LocalDate, today: LocalDate = LocalDate.now()): String =
    "${day.format(shortDay)}, ${relativeDate(day, today)}"
