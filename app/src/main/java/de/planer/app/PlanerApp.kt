package de.planer.app

import android.app.Application
import de.planer.app.calendar.CalendarRepository
import de.planer.app.data.PlanerDatabase
import de.planer.app.data.TaskRepository
import de.planer.app.reminder.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PlanerApp : Application() {
    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    lateinit var repository: TaskRepository
        private set
    lateinit var calendar: CalendarRepository
        private set

    override fun onCreate() {
        super.onCreate()
        repository = TaskRepository(PlanerDatabase.create(this), ReminderScheduler(this))
        calendar = CalendarRepository(this)
        appScope.launch { repository.ensureDefaultList() }
    }
}
