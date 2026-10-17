package de.planer.app.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TaskWithDetails(
    @Embedded val task: Task,
    @Relation(parentColumn = "id", entityColumn = "taskId")
    val checklist: List<ChecklistItem>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(TaskTagCrossRef::class, parentColumn = "taskId", entityColumn = "tagId"),
    )
    val tags: List<Tag>,
)
