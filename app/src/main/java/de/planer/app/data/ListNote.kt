package de.planer.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/** Freie Notiz (Gedächtnisstütze) innerhalb einer Liste. */
@Entity(
    tableName = "list_notes",
    foreignKeys = [
        ForeignKey(entity = TaskList::class, parentColumns = ["id"], childColumns = ["listId"], onDelete = ForeignKey.CASCADE),
    ],
    indices = [Index("listId")],
)
data class ListNote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val listId: Long,
    val title: String,
    val body: String,
    val updatedAt: Long = System.currentTimeMillis(),
) {
    val isBlank get() = title.isBlank() && body.isBlank()
}

@Dao
interface ListNoteDao {
    @Query("SELECT * FROM list_notes WHERE listId = :listId ORDER BY updatedAt DESC")
    fun observeByList(listId: Long): Flow<List<ListNote>>

    @Query("SELECT * FROM list_notes WHERE id = :id")
    suspend fun get(id: Long): ListNote?

    @Insert
    suspend fun insert(note: ListNote): Long

    @Update
    suspend fun update(note: ListNote)

    @Delete
    suspend fun delete(note: ListNote)
}
