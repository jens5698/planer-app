package de.planer.app.ui.components

import androidx.compose.foundation.layout.Arrangement
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
import de.planer.app.data.label
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private val untilFormat = DateTimeFormatter.ofPattern("d. MMM yyyy", Locale.GERMAN)

/**
 * Wiederholung plus optionales Enddatum ("Endet: Nie / am …").
 * [start] ist der erste Termin; das Enddatum kann nicht davor liegen.
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RepeatPicker(
    repeat: Repeat,
    until: LocalDate?,
    start: LocalDate,
    onChange: (Repeat, LocalDate?) -> Unit,
) {
    var picking by remember { mutableStateOf(false) }
    Column {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Repeat.entries.forEach { r ->
                FilterChip(
                    selected = repeat == r,
                    onClick = { onChange(r, if (r == Repeat.NONE) null else until) },
                    label = { Text(r.label) },
                )
            }
        }
        if (repeat != Repeat.NONE) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Endet", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(end = 4.dp))
                FilterChip(selected = until == null, onClick = { onChange(repeat, null) }, label = { Text("Nie") })
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
                        onChange(repeat, Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate())
                    }
                    picking = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { picking = false }) { Text("Abbrechen") } },
        ) { DatePicker(state, title = { Text("Letzter Termin der Serie", Modifier.padding(start = 24.dp, top = 16.dp)) }) }
    }
}
