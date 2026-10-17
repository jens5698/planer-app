package de.planer.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Eine Liste ("Projekt" in TickTick), z. B. Eingang, Arbeit, Einkauf. */
@Entity(tableName = "task_lists")
data class TaskList(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val color: Long = 0xFF4772FA,
    val sortOrder: Int = 0,
)

enum class Priority { NONE, LOW, MEDIUM, HIGH }

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = TaskList::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("listId"), Index("dueAt")],
)
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val listId: Long,
    val title: String,
    val notes: String = "",
    val priority: Priority = Priority.NONE,
    /** Fälligkeit als Epoch-Millis; null = ohne Datum. */
    val dueAt: Long? = null,
    val allDay: Boolean = true,
    /** Erinnerung als Epoch-Millis; null = keine Erinnerung. */
    val remindAt: Long? = null,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val sortOrder: Int = 0,
) {
    val isCompleted: Boolean get() = completedAt != null
}

/** Checklisten-Eintrag (Unteraufgabe) innerhalb einer Aufgabe. */
@Entity(
    tableName = "checklist_items",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("taskId")],
)
data class ChecklistItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long,
    val title: String,
    val done: Boolean = false,
    val sortOrder: Int = 0,
)

@Entity(tableName = "tags", indices = [Index(value = ["name"], unique = true)])
data class Tag(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
)

@Entity(
    tableName = "task_tags",
    primaryKeys = ["taskId", "tagId"],
    foreignKeys = [
        ForeignKey(entity = Task::class, parentColumns = ["id"], childColumns = ["taskId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Tag::class, parentColumns = ["id"], childColumns = ["tagId"], onDelete = ForeignKey.CASCADE),
    ],
    indices = [Index("tagId")],
)
data class TaskTagCrossRef(
    val taskId: Long,
    val tagId: Long,
)
