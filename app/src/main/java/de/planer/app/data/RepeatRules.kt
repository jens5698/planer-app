package de.planer.app.data

import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Vollständige Wiederholungsregel einer Aufgabe.
 * [weekdays]: Bitmaske Mo = Bit 0 … So = Bit 6, nur für WEEKLY (0 = Wochentag des Starttermins).
 * [monthDay]: 1–31, für MONTHLY und YEARLY (null = Tag des Starttermins); zu kurze Monate nehmen den letzten Tag.
 * [month]: 1–12, nur für YEARLY (null = Monat des Starttermins).
 */
data class RepeatRule(val repeat: Repeat, val weekdays: Int = 0, val monthDay: Int? = null, val month: Int? = null) {

    /** Jahrestermin in [year] für eine jährliche Regel mit festem Monat/Tag. */
    private fun yearlyIn(year: Int, fallback: LocalDate): LocalDate {
        val first = LocalDate.of(year, month ?: fallback.monthValue, 1)
        return first.withDayOfMonth(clampDay(first, monthDay ?: fallback.dayOfMonth))
    }

    private val yearlyFixed get() = repeat == Repeat.YEARLY && (month != null || monthDay != null)

    fun hasWeekday(day: DayOfWeek) = weekdays and (1 shl (day.value - 1)) != 0

    fun toggleWeekday(day: DayOfWeek) = copy(weekdays = weekdays xor (1 shl (day.value - 1)))

    /** Passt [date] zur Regel? Ohne Wochentage bzw. Monatstag passt jeder Tag. */
    fun matches(date: LocalDate): Boolean = when (repeat) {
        Repeat.WEEKLY -> weekdays == 0 || hasWeekday(date.dayOfWeek)
        Repeat.WEEKDAYS -> date.dayOfWeek != DayOfWeek.SATURDAY && date.dayOfWeek != DayOfWeek.SUNDAY
        Repeat.MONTHLY -> monthDay == null || date.dayOfMonth == clampDay(date, monthDay)
        Repeat.YEARLY -> !yearlyFixed || date == yearlyIn(date.year, date)
        else -> true
    }

    /** Erster passender Tag ab [date] (einschließlich), z. B. wenn die Serie mittwochs laufen soll. */
    fun align(date: LocalDate): LocalDate {
        if (repeat == Repeat.MONTHLY && monthDay != null) {
            val same = date.withDayOfMonth(clampDay(date, monthDay))
            return if (!same.isBefore(date)) same else date.plusMonths(1).let { it.withDayOfMonth(clampDay(it, monthDay)) }
        }
        if (yearlyFixed) {
            val same = yearlyIn(date.year, date)
            return if (!same.isBefore(date)) same else yearlyIn(date.year + 1, date)
        }
        var d = date
        repeat(7) { if (matches(d)) return d; d = d.plusDays(1) }
        return date
    }

    /** Nächster Termin nach [date]. */
    fun next(date: LocalDate): LocalDate = when (repeat) {
        Repeat.NONE -> date
        Repeat.DAILY -> date.plusDays(1)
        Repeat.WEEKDAYS -> align(date.plusDays(1))
        Repeat.WEEKLY -> if (weekdays == 0) date.plusWeeks(1) else align(date.plusDays(1))
        Repeat.MONTHLY -> {
            val m = date.plusMonths(1)
            m.withDayOfMonth(clampDay(m, monthDay ?: date.dayOfMonth))
        }
        Repeat.YEARLY -> if (yearlyFixed) yearlyIn(date.year + 1, date) else date.plusYears(1)
    }

    /**
     * Alle Termine ab [start] im Bereich [from, toExclusive), höchstens bis [until].
     * [start] selbst zählt mit, wenn es im Bereich liegt.
     */
    fun occurrences(start: LocalDate, from: LocalDate, toExclusive: LocalDate, until: LocalDate?): List<LocalDate> {
        if (repeat == Repeat.NONE) return if (!start.isBefore(from) && start.isBefore(toExclusive)) listOf(start) else emptyList()
        val result = mutableListOf<LocalDate>()
        var d = start
        var guard = 0
        while (d.isBefore(toExclusive) && (until == null || !d.isAfter(until)) && guard++ < 1000) {
            if (!d.isBefore(from)) result += d
            d = next(d)
        }
        return result
    }

    /** Kurzbeschreibung, z. B. "Wöchentlich: Mo, Do" oder "Monatlich am 15.". */
    val description: String
        get() = when {
            repeat == Repeat.WEEKLY && weekdays != 0 ->
                "Wöchentlich: " + DayOfWeek.entries.filter(::hasWeekday).joinToString(", ") { WEEKDAY_SHORT[it.value - 1] }
            repeat == Repeat.MONTHLY && monthDay != null -> "Monatlich am $monthDay."
            repeat == Repeat.YEARLY && monthDay != null && month != null -> "Jährlich am $monthDay. ${MONTH_NAMES[month - 1]}"
            else -> repeat.label
        }

    companion object {
        val WEEKDAY_SHORT = listOf("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So")
        val MONTH_NAMES = listOf(
            "Januar", "Februar", "März", "April", "Mai", "Juni",
            "Juli", "August", "September", "Oktober", "November", "Dezember",
        )

        /** Höchster sinnvoller Tag in einem Monat (Februar: 29, damit Schaltjahre gehen). */
        fun maxDay(month: Int) = java.time.Month.of(month).maxLength()

        fun bit(day: DayOfWeek) = 1 shl (day.value - 1)

        private fun clampDay(inMonth: LocalDate, day: Int) = minOf(day, inMonth.lengthOfMonth())
    }
}

val Task.rule: RepeatRule get() = RepeatRule(repeat, repeatWeekdays, repeatMonthDay, repeatMonth)

/** Kurzform ohne Wochentage/Monatstag, z. B. für Tests und alte Aufgaben. */
fun Repeat.next(date: LocalDate): LocalDate = RepeatRule(this).next(date)

fun Repeat.occurrences(start: LocalDate, from: LocalDate, toExclusive: LocalDate, until: LocalDate?) =
    RepeatRule(this).occurrences(start, from, toExclusive, until)

val Repeat.label: String
    get() = when (this) {
        Repeat.NONE -> "Nie"
        Repeat.DAILY -> "Täglich"
        Repeat.WEEKDAYS -> "Werktags"
        Repeat.WEEKLY -> "Wöchentlich"
        Repeat.MONTHLY -> "Monatlich"
        Repeat.YEARLY -> "Jährlich"
    }
