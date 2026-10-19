package de.planer.app.ui.components

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val shortDay = DateTimeFormatter.ofPattern("EE", Locale.GERMAN)
private val dayMonth = DateTimeFormatter.ofPattern("d. MMM", Locale.GERMAN)
private val dayMonthYear = DateTimeFormatter.ofPattern("d. MMM yyyy", Locale.GERMAN)
private val time = DateTimeFormatter.ofPattern("HH:mm")

/** "Erledigt 19. Okt., 14:32" (mit Jahr, wenn nicht das aktuelle). */
fun completedLabel(epochMillis: Long, zone: ZoneId = ZoneId.systemDefault()): String {
    val at = Instant.ofEpochMilli(epochMillis).atZone(zone)
    val date = if (at.year == LocalDate.now(zone).year) at.format(dayMonth) else at.format(dayMonthYear)
    return "Erledigt $date, ${at.format(time)}"
}

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
