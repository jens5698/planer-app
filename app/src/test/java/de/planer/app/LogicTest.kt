package de.planer.app

import de.planer.app.calendar.CalendarEvent
import de.planer.app.data.Priority
import de.planer.app.data.Repeat
import de.planer.app.data.next
import de.planer.app.data.occurrences
import de.planer.app.ui.components.parseQuickAdd
import de.planer.app.ui.screens.visibleDays
import de.planer.app.ui.components.swapped
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
        // Bereits gespeicherte Namen aus früheren Versionen müssen weiter existieren.
        listOf("INBOX", "SHOPPING", "GARDEN", "WATER", "NATURE", "PHONE").forEach {
            assertEquals(it, de.planer.app.ui.ListIcon.of(it).name)
        }
        assertTrue(de.planer.app.ui.ListIcon.entries.size >= 120)
        assertEquals(de.planer.app.ui.IconCategory.entries.toSet(), de.planer.app.ui.ListIcon.byCategory.keys)
    }
}

class AgendaTest {
    private fun task(id: Long, due: LocalDate) = de.planer.app.data.TaskWithDetails(
        de.planer.app.data.Task(
            id = id, listId = 1, title = "T$id",
            dueAt = due.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        ),
        emptyList(), emptyList(),
    )

    @Test fun groupsOverdueAndDaysAndShowsMultiDayEventOnce() {
        val today = LocalDate.of(2026, 10, 17)
        val tasks = listOf(task(1, today.minusDays(2)), task(2, today), task(3, today.plusDays(2)))
        val ferien = CalendarEvent(
            9, "Herbstferien",
            today.plusDays(2).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
            today.plusDays(13).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
            allDay = true, color = 0, calendarName = "",
        )
        val sections = de.planer.app.ui.screens.buildAgenda(today, 7, tasks, listOf(ferien))
        assertEquals("Überfällig", sections.first().title)
        assertEquals("Sa., Heute", sections[1].title)
        assertEquals(listOf(2L), sections[1].tasks.map { it.task.id })
        assertEquals("Mo., 19. Okt.", sections[3].title)
        assertEquals(1, sections.sumOf { it.events.size })
        assertEquals(1, sections[3].events.size)
    }
}

class CompletedTest {
    private fun done(id: Long, at: java.time.LocalDateTime) = de.planer.app.data.TaskWithDetails(
        de.planer.app.data.Task(
            id = id, listId = 1, title = "T$id",
            completedAt = at.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        ),
        emptyList(), emptyList(),
    )

    @Test fun rangesAreRollingAndIncludeToday() {
        val today = LocalDate.of(2026, 10, 19)
        assertEquals(LocalDate.of(2026, 10, 13), de.planer.app.ui.screens.CompletedRange.WEEK.since(today))
        assertEquals(LocalDate.of(2026, 9, 20), de.planer.app.ui.screens.CompletedRange.MONTH.since(today))
        assertEquals(LocalDate.of(2025, 10, 20), de.planer.app.ui.screens.CompletedRange.YEAR.since(today))
    }

    @Test fun weekGroupsByDayNewestFirstYearByMonth() {
        val today = LocalDate.of(2026, 10, 19)
        val tasks = listOf(
            done(1, today.atTime(9, 0)), done(2, today.minusDays(1).atTime(18, 0)),
            done(3, today.atTime(14, 32)), done(4, LocalDate.of(2026, 3, 2).atTime(8, 0)),
        )
        val week = de.planer.app.ui.screens.buildCompleted(de.planer.app.ui.screens.CompletedRange.WEEK, tasks.take(3), today)
        assertEquals(listOf("Mo., Heute", "So., Gestern"), week.map { it.title })
        assertEquals(2, week.first().tasks.size)
        val year = de.planer.app.ui.screens.buildCompleted(de.planer.app.ui.screens.CompletedRange.YEAR, tasks, today)
        assertEquals(listOf("Oktober 2026", "März 2026"), year.map { it.title })
    }

    @Test fun completedLabelShowsDateAndTime() {
        val at = LocalDate.now().withDayOfMonth(5).atTime(14, 32).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        assertTrue(de.planer.app.ui.components.completedLabel(at).matches(Regex("Erledigt 5\\. \\S+, 14:32")))
    }
}

class UntilTest {
    @Test fun daysUntilIncludesBothEnds() {
        val today = LocalDate.of(2026, 10, 19)
        assertEquals(1, de.planer.app.ui.screens.daysUntil(today, today))
        assertEquals(13, de.planer.app.ui.screens.daysUntil(today, LocalDate.of(2026, 10, 31)))
        val sections = de.planer.app.ui.screens.buildAgenda(today, 13, emptyList(), emptyList())
        assertEquals("Sa., 31. Okt.", sections.last().title)
    }
}

class ReorderTest {
    @Test fun swappedMovesNeighbours() {
        val l = listOf("a", "b", "c")
        assertEquals(listOf("b", "a", "c"), l.swapped(0, 1))
        assertEquals(listOf("a", "c", "b"), l.swapped(2, -1))
    }
}

class SeriesTest {
    private val zone = ZoneId.systemDefault()

    @Test fun weeklyOccurrencesStopAtEndDate() {
        val start = LocalDate.of(2026, 10, 20)
        val until = LocalDate.of(2026, 11, 10)
        val occ = Repeat.WEEKLY.occurrences(start, start, start.plusYears(1), until)
        assertEquals(
            listOf(LocalDate.of(2026, 10, 20), LocalDate.of(2026, 10, 27), LocalDate.of(2026, 11, 3), LocalDate.of(2026, 11, 10)),
            occ,
        )
    }

    @Test fun previewsSkipCurrentDueAndRespectEnd() {
        val today = LocalDate.of(2026, 10, 20)
        val task = de.planer.app.data.TaskWithDetails(
            de.planer.app.data.Task(
                id = 7, listId = 1, title = "Chor",
                dueAt = today.atTime(19, 30).atZone(zone).toInstant().toEpochMilli(),
                repeat = Repeat.WEEKLY, repeatUntil = LocalDate.of(2026, 11, 3).toEpochDay(),
            ),
            emptyList(), emptyList(),
        )
        val previews = de.planer.app.ui.screens.seriesPreviews(listOf(task), today, today.plusMonths(2), zone)
        assertEquals(setOf(LocalDate.of(2026, 10, 27), LocalDate.of(2026, 11, 3)), previews.keys)
        val time = java.time.Instant.ofEpochMilli(previews.getValue(LocalDate.of(2026, 10, 27)).single().task.dueAt!!).atZone(zone)
        assertEquals(19, time.hour)
        assertEquals(30, time.minute)
        val agenda = de.planer.app.ui.screens.buildAgenda(today, 14, listOf(task), emptyList())
        assertEquals(1, agenda.first { it.key == "2026-10-27" }.previews.size)
    }
}
