package de.planer.app

import de.planer.app.calendar.CalendarEvent
import de.planer.app.data.Priority
import de.planer.app.data.Repeat
import de.planer.app.data.next
import de.planer.app.ui.components.parseQuickAdd
import de.planer.app.ui.screens.visibleDays
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

class LogicTest {
    @Test fun weekdaysSkipWeekend() {
        val friday = LocalDate.of(2026, 10, 16)
        assertEquals(LocalDate.of(2026, 10, 19), Repeat.WEEKDAYS.next(friday))
    }

    @Test fun monthlyClampsToMonthEnd() {
        assertEquals(LocalDate.of(2026, 2, 28), Repeat.MONTHLY.next(LocalDate.of(2026, 1, 31)))
    }

    @Test fun quickAddParsesTagsAndPriority() {
        val r = parseQuickAdd("Milch kaufen #einkauf !2", null)
        assertEquals("Milch kaufen", r.title)
        assertEquals(listOf("einkauf"), r.tags)
        assertEquals(Priority.MEDIUM, r.priority)
    }

    @Test fun monthGridStartsMondayAndHasSixWeeks() {
        val days = visibleDays(LocalDate.of(2026, 10, 17), month = true)
        assertEquals(42, days.size)
        assertEquals(DayOfWeek.MONDAY, days.first().dayOfWeek)
        assertTrue(LocalDate.of(2026, 10, 1) in days && LocalDate.of(2026, 10, 31) in days)
    }

    @Test fun allDayEventOnlyOnItsDay() {
        val berlin = ZoneId.of("Europe/Berlin")
        val day = LocalDate.of(2026, 10, 18)
        val e = CalendarEvent(
            1, "Geburtstag",
            day.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
            day.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
            allDay = true, color = 0, calendarName = "",
        )
        assertTrue(e.occursOn(day, berlin))
        assertFalse(e.occursOn(day.plusDays(1), berlin))
        assertFalse(e.occursOn(day.minusDays(1), berlin))
    }
}

class ListIconTest {
    @Test fun iconNamesAreUniqueAndUnknownFallsBack() {
        val names = de.planer.app.ui.ListIcon.entries.map { it.name }
        assertEquals(names.size, names.toSet().size)
        assertEquals(de.planer.app.ui.ListIcon.LIST, de.planer.app.ui.ListIcon.of("GIBTS_NICHT"))
    }
}
