package de.planer.app.data

import java.time.DayOfWeek
import java.time.LocalDate

/** Nächster Termin einer wiederkehrenden Aufgabe nach [date]. */
fun Repeat.next(date: LocalDate): LocalDate = when (this) {
    Repeat.NONE -> date
    Repeat.DAILY -> date.plusDays(1)
    Repeat.WEEKDAYS -> {
        var d = date.plusDays(1)
        while (d.dayOfWeek == DayOfWeek.SATURDAY || d.dayOfWeek == DayOfWeek.SUNDAY) d = d.plusDays(1)
        d
    }
    Repeat.WEEKLY -> date.plusWeeks(1)
    Repeat.MONTHLY -> date.plusMonths(1)
    Repeat.YEARLY -> date.plusYears(1)
}

val Repeat.label: String
    get() = when (this) {
        Repeat.NONE -> "Nie"
        Repeat.DAILY -> "Täglich"
        Repeat.WEEKDAYS -> "Werktags"
        Repeat.WEEKLY -> "Wöchentlich"
        Repeat.MONTHLY -> "Monatlich"
        Repeat.YEARLY -> "Jährlich"
    }
