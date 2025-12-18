package com.yourname.taskmanager.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarView(
    onDateSelected: (LocalDate) -> Unit
) {
    val currentDate = LocalDate.now()
    val currentMonth = YearMonth.now()
    val startDate = currentMonth.minusMonths(100).atStartOfMonth()
    val endDate = currentMonth.plusMonths(100).atEndOfMonth()
    val firstDayOfWeek = firstDayOfWeekFromLocale()

    val state = rememberWeekCalendarState(
        startDate = startDate,
        endDate = endDate,
        firstVisibleWeekDate = currentDate,
        firstDayOfWeek = firstDayOfWeek
    )

    Column {
        WeekCalendar(
            state = state,
            modifier = Modifier.fillMaxWidth(),
            dayContent = { day ->
                Day(day.date, onDateSelected = onDateSelected)
            }
        )
    }
}
