package de.planer.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Converters {
    @TypeConverter fun priorityToInt(p: Priority): Int = p.ordinal
    @TypeConverter fun intToPriority(i: Int): Priority = Priority.entries[i]
    @TypeConverter fun repeatToInt(r: Repeat): Int = r.ordinal
    @TypeConverter fun intToRepeat(i: Int): Repeat = Repeat.entries[i]
}

/** v3: Enddatum für Serien. */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE tasks ADD COLUMN repeatUntil INTEGER")
    }
}

/** v2: Listensymbol und Wiederholung. */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE task_lists ADD COLUMN icon TEXT NOT NULL DEFAULT 'LIST'")
        db.execSQL("ALTER TABLE tasks ADD COLUMN repeat INTEGER NOT NULL DEFAULT 0")
    }
}

@Database(
    entities = [TaskList::class, Task::class, ChecklistItem::class, Tag::class, TaskTagCrossRef::class],
    version = 3,
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
            Room.databaseBuilder(context, PlanerDatabase::class.java, "planer.db")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build()
    }
}
