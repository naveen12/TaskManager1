package com.yourname.taskmanager.data

import androidx.room.TypeConverter

class TaskTypeConverters {
    @TypeConverter
    fun fromPriority(priority: Priority): Int {
        return priority.value
    }

    @TypeConverter
    fun toPriority(value: Int): Priority {
        return Priority.fromInt(value)
    }

    @TypeConverter
    fun fromStringSet(set: Set<String>): String {
        return set.joinToString(",")
    }

    @TypeConverter
    fun toStringSet(string: String): Set<String> {
        return string.split(",").toSet()
    }
}
