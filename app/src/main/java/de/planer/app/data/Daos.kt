package de.planer.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskListDao {
    @Query("SELECT * FROM task_lists ORDER BY sortOrder, id")
    fun observeAll(): Flow<List<TaskList>>

    @Query("SELECT COUNT(*) FROM task_lists")
    suspend fun count(): Int

    @Insert
    suspend fun insert(list: TaskList): Long

    @Update
    suspend fun update(list: TaskList)

    @Delete
    suspend fun delete(list: TaskList)
}

@Dao
interface TaskDao {
    @Transaction
    @Query("SELECT * FROM tasks WHERE listId = :listId ORDER BY completedAt IS NOT NULL, priority DESC, dueAt IS NULL, dueAt, sortOrder")
    fun observeByList(listId: Long): Flow<List<TaskWithDetails>>

    /** Offene Aufgaben mit Fälligkeit bis [endExclusive] (inkl. überfällige). */
    @Transaction
    @Query("SELECT * FROM tasks WHERE completedAt IS NULL AND dueAt IS NOT NULL AND dueAt < :endExclusive ORDER BY dueAt, priority DESC")
    fun observeDueBefore(endExclusive: Long): Flow<List<TaskWithDetails>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE dueAt >= :start AND dueAt < :endExclusive ORDER BY dueAt")
    fun observeBetween(start: Long, endExclusive: Long): Flow<List<TaskWithDetails>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :id")
    fun observe(id: Long): Flow<TaskWithDetails?>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun get(id: Long): Task?

    /** Volltextsuche in Titel, Notizen und Tag-Namen. */
    @Transaction
    @Query(
        """
        SELECT * FROM tasks
        WHERE title LIKE '%' || :q || '%' OR notes LIKE '%' || :q || '%'
           OR id IN (SELECT task_tags.taskId FROM task_tags JOIN tags ON tags.id = task_tags.tagId
                     WHERE tags.name LIKE '%' || :q || '%')
        ORDER BY completedAt IS NOT NULL, dueAt IS NULL, dueAt
        """,
    )
    fun search(q: String): Flow<List<TaskWithDetails>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE completedAt >= :since ORDER BY completedAt DESC")
    fun observeCompletedSince(since: Long): Flow<List<TaskWithDetails>>

    @Query("SELECT * FROM tasks WHERE completedAt IS NULL AND remindAt > :now")
    suspend fun upcomingReminders(now: Long): List<Task>

    @Insert
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}

@Dao
interface ChecklistDao {
    @Query("UPDATE checklist_items SET done = 0 WHERE taskId = :taskId")
    suspend fun resetAll(taskId: Long)

    @Insert
    suspend fun insert(item: ChecklistItem): Long

    @Update
    suspend fun update(item: ChecklistItem)

    @Delete
    suspend fun delete(item: ChecklistItem)
}

@Dao
interface TagDao {
    @Query("SELECT * FROM tags ORDER BY name")
    fun observeAll(): Flow<List<Tag>>

    @Query("SELECT * FROM tags WHERE name = :name")
    suspend fun findByName(name: String): Tag?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tag: Tag): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun attach(ref: TaskTagCrossRef)

    @Delete
    suspend fun detach(ref: TaskTagCrossRef)

    @Transaction
    @Query("SELECT tasks.* FROM tasks JOIN task_tags ON tasks.id = task_tags.taskId WHERE task_tags.tagId = :tagId AND tasks.completedAt IS NULL ORDER BY tasks.dueAt IS NULL, tasks.dueAt")
    fun observeTasksWithTag(tagId: Long): Flow<List<TaskWithDetails>>
}
