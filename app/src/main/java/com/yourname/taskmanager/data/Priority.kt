package com.yourname.taskmanager.data

enum class Priority(val value: Int) {
    LOW(0),
    MEDIUM(1),
    HIGH(2);

    companion object {
        fun fromInt(value: Int) = entries.first { it.value == value }
    }
}
