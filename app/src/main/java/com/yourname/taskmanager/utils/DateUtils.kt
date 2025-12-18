package com.yourname.taskmanager.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

// --- Conversion Functions ---

fun Long.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
}

fun LocalDateTime.toMillis(): Long {
    return this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

// --- Formatting Functions ---

fun Long.toPrettyDateString(): String {
    val date = toLocalDateTime()
    val today = LocalDate.now()
    val daysBetween = ChronoUnit.DAYS.between(today, date.toLocalDate())

    return when (daysBetween) {
        0L -> "Today"
        1L -> "Tomorrow"
        -1L -> "Yesterday"
        else -> {
            val pattern = if (date.year == today.year) "MMM d" else "MMM d, yyyy"
            date.format(DateTimeFormatter.ofPattern(pattern))
        }
    }
}

fun Long.toDateString(): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(this))
}

fun Long.toTimeString(): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(this))
}

fun Long.toDateTimeString(): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
    return sdf.format(Date(this))
}

fun Long.toRelativeDateString(): String {
    return when {
        isToday() -> "Today"
        isTomorrow() -> "Tomorrow"
        isOverdue() -> "Overdue"
        else -> toDateString()
    }
}

fun LocalDate.toPrettyDateString(): String {
    val today = LocalDate.now()
    val daysBetween = ChronoUnit.DAYS.between(today, this)

    return when (daysBetween) {
        0L -> "Today"
        1L -> "Tomorrow"
        -1L -> "Yesterday"
        else -> {
            val pattern = if (this.year == today.year) "MMM d" else "MMM d, yyyy"
            this.format(DateTimeFormatter.ofPattern(pattern))
        }
    }
}

// --- Utility Functions ---

fun Long.isOverdue(): Boolean = this < System.currentTimeMillis() && !toLocalDate().isEqual(LocalDate.now())

fun Long.isToday(): Boolean {
    val today = Calendar.getInstance()
    val targetDate = Calendar.getInstance().apply { timeInMillis = this@isToday }
    return today.get(Calendar.YEAR) == targetDate.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) == targetDate.get(Calendar.DAY_OF_YEAR)
}

fun Long.isTomorrow(): Boolean {
    val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
    val targetDate = Calendar.getInstance().apply { timeInMillis = this@isTomorrow }
    return tomorrow.get(Calendar.YEAR) == targetDate.get(Calendar.YEAR) &&
            tomorrow.get(Calendar.DAY_OF_YEAR) == targetDate.get(Calendar.DAY_OF_YEAR)
}

fun Long.isSameDay(other: Long): Boolean = toLocalDate().isEqual(other.toLocalDate())

fun LocalDate.isSameDay(other: LocalDate): Boolean = isEqual(other)
