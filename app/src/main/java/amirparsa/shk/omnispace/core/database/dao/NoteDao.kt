package amirparsa.shk.omnispace.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import amirparsa.shk.omnispace.core.database.entity.NoteEntity

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY isPinned DESC, id DESC")
    fun getAllNotes(): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    fun getNoteById(id: String): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(notes: List<NoteEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: NoteEntity): Long

    @Query("UPDATE notes SET isPinned = :isPinned WHERE id = :id")
    fun updatePinned(id: String, isPinned: Boolean): Int

    @Query("UPDATE notes SET title = :title, content = :content, updatedAt = :updatedAt, isPinned = :isPinned WHERE id = :id")
    fun updateNote(id: String, title: String, content: String, updatedAt: String, isPinned: Boolean): Int

    @Query("DELETE FROM notes WHERE id = :id")
    fun deleteById(id: String): Int

    @Query("DELETE FROM notes")
    fun clearAll(): Int

    @Query("SELECT COUNT(*) FROM notes")
    fun getCount(): Int
}
