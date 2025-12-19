package com.yourname.taskmanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Task::class, Alarm::class, Reminder::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(TaskTypeConverters::class)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun alarmDao(): AlarmDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getDatabase(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database"
                )
                    .addMigrations(MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE tasks ADD COLUMN notes TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE tasks ADD COLUMN duration INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE tasks ADD COLUMN repeat TEXT NOT NULL DEFAULT 'Does not repeat'")
                database.execSQL("ALTER TABLE tasks ADD COLUMN backgroundColor TEXT NOT NULL DEFAULT '#FFFFFF'")
                database.execSQL("ALTER TABLE reminders ADD COLUMN notes TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE reminders ADD COLUMN dueDate INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE reminders ADD COLUMN repeat TEXT NOT NULL DEFAULT 'Does not repeat'")
                database.execSQL("ALTER TABLE reminders ADD COLUMN category TEXT NOT NULL DEFAULT 'Default'")
                database.execSQL("ALTER TABLE reminders ADD COLUMN backgroundColor TEXT NOT NULL DEFAULT '#FFFFFF'")
            }
        }

        fun getDatabasePath(context: Context): String {
            return context.getDatabasePath("task_database").absolutePath
        }
    }
}
