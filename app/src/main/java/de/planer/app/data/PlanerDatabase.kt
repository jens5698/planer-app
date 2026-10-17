package de.planer.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class Converters {
    @TypeConverter fun priorityToInt(p: Priority): Int = p.ordinal
    @TypeConverter fun intToPriority(i: Int): Priority = Priority.entries[i]
}

@Database(
    entities = [TaskList::class, Task::class, ChecklistItem::class, Tag::class, TaskTagCrossRef::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class PlanerDatabase : RoomDatabase() {
    abstract fun taskListDao(): TaskListDao
    abstract fun taskDao(): TaskDao
    abstract fun checklistDao(): ChecklistDao
    abstract fun tagDao(): TagDao

    companion object {
        fun create(context: Context): PlanerDatabase =
            Room.databaseBuilder(context, PlanerDatabase::class.java, "planer.db").build()
    }
}
