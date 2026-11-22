package de.planer.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.planer.app.data.Repeat
import de.planer.app.data.RepeatRule
import de.planer.app.data.label
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private val untilFormat = DateTimeFormatter.ofPattern("d. MMM yyyy", Locale.GERMAN)

/** Regel beim Wechsel der Art mit dem Starttermin vorbelegen, z. B. wöchentlich am Wochentag des Starts. */
fun RepeatRule.withRepeat(r: Repeat, start: LocalDate): RepeatRule = when (r) {
    Repeat.WEEKLY -> RepeatRule(r, weekdays = RepeatRule.bit(start.dayOfWeek))
    Repeat.MONTHLY -> RepeatRule(r, monthDay = start.dayOfMonth)
    Repeat.YEARLY -> RepeatRule(r, monthDay = start.dayOfMonth, month = start.monthValue)
    else -> RepeatRule(r)
}

/**
 * Wiederholung plus Details (Wochentage, Tag im Monat, Datum im Jahr) und optionales Enddatum.
 * [start] ist der erste Termin; das Enddatum kann nicht davor liegen.
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RepeatPicker(
    rule: RepeatRule,
    until: LocalDate?,
    start: LocalDate,
    onChange: (RepeatRule, LocalDate?) -> Unit,
) {
    var picking by remember { mutableStateOf(false) }
    val repeat = rule.repeat
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Repeat.entries.forEach { r ->
                FilterChip(
                    selected = repeat == r,
                    onClick = { if (r != repeat) onChange(rule.withRepeat(r, start), if (r == Repeat.NONE) null else until) },
                    label = { Text(r.label) },
                )
            }
        }
        when (repeat) {
            Repeat.WEEKLY -> {
                Text("An diesen Tagen", style = MaterialTheme.typography.labelLarge)
                // Ältere Serien ohne Wochentage laufen am Wochentag des Starttermins.
                val shown = if (rule.weekdays == 0) rule.copy(weekdays = RepeatRule.bit(start.dayOfWeek)) else rule
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    DayOfWeek.entries.forEach { day ->
                        val on = shown.hasWeekday(day)
                        FilterChip(
                            selected = on,
                            onClick = {
                                val next = shown.toggleWeekday(day)
                                if (next.weekdays != 0) onChange(next, until) // mindestens ein Tag bleibt
                            },
                            label = { Text(RepeatRule.WEEKDAY_SHORT[day.value - 1]) },
                        )
                    }
                }
            }
            Repeat.MONTHLY -> Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Am Tag", style = MaterialTheme.typography.labelLarge)
                val day = rule.monthDay ?: start.dayOfMonth
                NumberMenu("$day.", (1..31).toList(), { "$it." }) { onChange(rule.copy(monthDay = it), until) }
            }
            Repeat.YEARLY -> Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Am", style = MaterialTheme.typography.labelLarge)
                val month = rule.month ?: start.monthValue
                val day = minOf(rule.monthDay ?: start.dayOfMonth, RepeatRule.maxDay(month))
                NumberMenu("$day.", (1..RepeatRule.maxDay(month)).toList(), { "$it." }) {
                    onChange(rule.copy(monthDay = it, month = month), until)
                }
                NumberMenu(RepeatRule.MONTH_NAMES[month - 1], (1..12).toList(), { RepeatRule.MONTH_NAMES[it - 1] }) {
                    onChange(rule.copy(month = it, monthDay = minOf(day, RepeatRule.maxDay(it))), until)
                }
            }
            else -> {}
        }
        if (repeat == Repeat.MONTHLY && (rule.monthDay ?: 0) > 28) {
            Text(
                "In kürzeren Monaten am letzten Tag.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (repeat != Repeat.NONE) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Endet", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(end = 4.dp))
                FilterChip(selected = until == null, onClick = { onChange(rule, null) }, label = { Text("Nie") })
                FilterChip(
                    selected = until != null,
                    onClick = { picking = true },
                    label = { Text(until?.let { "am ${it.format(untilFormat)}" } ?: "Am Datum …") },
                )
            }
        }
    }

    if (picking) {
        // DatePicker rechnet in UTC-Mitternacht.
        val startUtc = start.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        val state = rememberDatePickerState(
            initialSelectedDateMillis = (until ?: start.plusMonths(3)).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis >= startUtc
                override fun isSelectableYear(year: Int) = year >= start.year
            },
        )
        DatePickerDialog(
            onDismissRequest = { picking = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        onChange(rule, Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate())
                    }
                    picking = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { picking = false }) { Text("Abbrechen") } },
        ) { DatePicker(state, title = { Text("Letzter Termin der Serie", Modifier.padding(start = 24.dp, top = 16.dp)) }) }
    }
}

/** Knopf mit Auswahlmenü, z. B. für den Tag im Monat. */
@Composable
private fun NumberMenu(current: String, values: List<Int>, label: (Int) -> String, onPick: (Int) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { open = true }) { Text(current) }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }, modifier = Modifier.heightIn(max = 320.dp)) {
            values.forEach { v ->
                DropdownMenuItem(text = { Text(label(v)) }, onClick = { open = false; onPick(v) })
            }
        }
    }
}
