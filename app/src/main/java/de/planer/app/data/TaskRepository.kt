package de.planer.app.data

import de.planer.app.reminder.ReminderScheduler
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId

class TaskRepository(
    private val db: PlanerDatabase,
    private val reminders: ReminderScheduler,
) {
    private val lists = db.taskListDao()
    private val tasks = db.taskDao()
    private val checklist = db.checklistDao()
    private val tags = db.tagDao()

    fun observeLists(): Flow<List<TaskList>> = lists.observeAll()
    fun observeTags(): Flow<List<Tag>> = tags.observeAll()
    fun observeList(listId: Long) = tasks.observeByList(listId)
    fun observeTask(id: Long) = tasks.observe(id)
    fun observeTag(tagId: Long) = tags.observeTasksWithTag(tagId)

    /** Heute fällig plus alles Überfällige. */
    fun observeToday(zone: ZoneId = ZoneId.systemDefault()): Flow<List<TaskWithDetails>> {
        val tomorrow = LocalDate.now(zone).plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
        return tasks.observeDueBefore(tomorrow)
    }

    fun observeDay(day: LocalDate, zone: ZoneId = ZoneId.systemDefault()): Flow<List<TaskWithDetails>> {
        val start = day.atStartOfDay(zone).toInstant().toEpochMilli()
        val end = day.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
        return tasks.observeBetween(start, end)
    }

    suspend fun ensureDefaultList() {
        if (lists.count() == 0) lists.insert(TaskList(name = "Eingang"))
    }

    suspend fun addList(name: String) = lists.insert(TaskList(name = name))

    suspend fun addTask(task: Task): Long {
        val id = tasks.insert(task)
        reminders.schedule(task.copy(id = id))
        return id
    }

    suspend fun updateTask(task: Task) {
        tasks.update(task)
        reminders.schedule(task)
    }

    suspend fun toggleCompleted(task: Task) {
        updateTask(task.copy(completedAt = if (task.isCompleted) null else System.currentTimeMillis()))
    }

    suspend fun deleteTask(task: Task) {
        reminders.cancel(task.id)
        tasks.delete(task)
    }

    suspend fun addChecklistItem(taskId: Long, title: String) =
        checklist.insert(ChecklistItem(taskId = taskId, title = title))

    suspend fun toggleChecklistItem(item: ChecklistItem) = checklist.update(item.copy(done = !item.done))

    suspend fun deleteChecklistItem(item: ChecklistItem) = checklist.delete(item)

    suspend fun addTag(taskId: Long, name: String) {
        val clean = name.trim().removePrefix("#")
        if (clean.isEmpty()) return
        val tagId = tags.findByName(clean)?.id ?: tags.insert(Tag(name = clean))
        tags.attach(TaskTagCrossRef(taskId, tagId))
    }

    suspend fun removeTag(taskId: Long, tag: Tag) = tags.detach(TaskTagCrossRef(taskId, tag.id))

    /** Nach Neustart/Update alle zukünftigen Erinnerungen neu planen. */
    suspend fun rescheduleAllReminders() {
        tasks.upcomingReminders(System.currentTimeMillis()).forEach(reminders::schedule)
    }
}
