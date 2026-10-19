package de.planer.app.data

import de.planer.app.reminder.ReminderScheduler
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import java.time.Instant
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

    /** Offene Aufgaben mit Fälligkeit vor [endExclusive], inklusive überfälliger. */
    fun observeOpenUntil(endExclusive: LocalDate, zone: ZoneId = ZoneId.systemDefault()) =
        tasks.observeDueBefore(endExclusive.atStartOfDay(zone).toInstant().toEpochMilli())

    fun observeDay(day: LocalDate, zone: ZoneId = ZoneId.systemDefault()): Flow<List<TaskWithDetails>> {
        val start = day.atStartOfDay(zone).toInstant().toEpochMilli()
        val end = day.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
        return tasks.observeBetween(start, end)
    }

    /** Alle Aufgaben mit Fälligkeit im Bereich [start, endExclusive). */
    fun observeRange(start: LocalDate, endExclusive: LocalDate, zone: ZoneId = ZoneId.systemDefault()) =
        tasks.observeBetween(
            start.atStartOfDay(zone).toInstant().toEpochMilli(),
            endExclusive.atStartOfDay(zone).toInstant().toEpochMilli(),
        )

    fun observeCompletedSince(since: LocalDate, zone: ZoneId = ZoneId.systemDefault()) =
        tasks.observeCompletedSince(since.atStartOfDay(zone).toInstant().toEpochMilli())

    fun search(query: String): Flow<List<TaskWithDetails>> = tasks.search(query.trim())

    suspend fun ensureDefaultList() {
        if (lists.count() == 0) lists.insert(TaskList(name = "Eingang", icon = "INBOX"))
    }

    /** Neue Listen kommen ans Ende. */
    suspend fun addList(name: String, icon: String, color: Long) =
        lists.insert(TaskList(name = name, icon = icon, color = color, sortOrder = lists.nextSortOrder()))

    /** Speichert die Reihenfolge der Listen so, wie sie in [ids] stehen. */
    suspend fun reorderLists(ids: List<Long>) = db.withTransaction {
        ids.forEachIndexed { index, id -> lists.setSortOrder(id, index) }
    }

    suspend fun updateList(list: TaskList) = lists.update(list)

    suspend fun countOpenInList(listId: Long) = tasks.countOpenInList(listId)

    /** Löscht die Liste samt Aufgaben; ist danach keine Liste mehr da, entsteht wieder "Eingang". */
    suspend fun deleteList(list: TaskList) {
        tasks.idsInList(list.id).forEach(reminders::cancel)
        lists.delete(list)
        ensureDefaultList()
    }

    suspend fun addTask(task: Task): Long {
        val id = tasks.insert(task)
        reminders.schedule(task.copy(id = id))
        return id
    }

    suspend fun updateTask(task: Task) {
        tasks.update(task)
        reminders.schedule(task)
    }

    /**
     * Abhaken. Wiederkehrende Aufgaben springen wie in TickTick auf den nächsten Termin,
     * statt erledigt zu werden; die Checkliste wird dabei zurückgesetzt.
     */
    suspend fun toggleCompleted(task: Task, zone: ZoneId = ZoneId.systemDefault()) {
        val due = task.dueAt
        if (!task.isCompleted && task.repeat != Repeat.NONE && due != null) {
            val current = Instant.ofEpochMilli(due).atZone(zone)
            val next = current.with(task.repeat.next(current.toLocalDate()))
            val delta = next.toInstant().toEpochMilli() - due
            // Erledigte Instanz festhalten, damit sie in "Erledigt" und im Verlauf der Liste auftaucht.
            tasks.insert(
                task.copy(
                    id = 0, repeat = Repeat.NONE, remindAt = null,
                    completedAt = System.currentTimeMillis(), createdAt = System.currentTimeMillis(),
                ),
            )
            updateTask(task.copy(dueAt = due + delta, remindAt = task.remindAt?.plus(delta)))
            checklist.resetAll(task.id)
            return
        }
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
