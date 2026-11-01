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

/** v4: Notizen je Liste. SQL entspricht dem, was Room für [ListNote] erzeugt. */
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `list_notes` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `listId` INTEGER NOT NULL, " +
                "`title` TEXT NOT NULL, `body` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL, " +
                "FOREIGN KEY(`listId`) REFERENCES `task_lists`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_list_notes_listId` ON `list_notes` (`listId`)")
    }
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
    entities = [TaskList::class, Task::class, ChecklistItem::class, Tag::class, TaskTagCrossRef::class, ListNote::class],
    version = 4,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class PlanerDatabase : RoomDatabase() {
    abstract fun taskListDao(): TaskListDao
    abstract fun taskDao(): TaskDao
    abstract fun checklistDao(): ChecklistDao
    abstract fun tagDao(): TagDao
    abstract fun listNoteDao(): ListNoteDao

    companion object {
        fun create(context: Context): PlanerDatabase =
            Room.databaseBuilder(context, PlanerDatabase::class.java, "planer.db")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .build()
    }
}
