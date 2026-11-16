package amirparsa.shk.omnispace.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import amirparsa.shk.omnispace.feature.today.domain.model.NoteModel

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
    val updatedAt: String,
    val isPinned: Boolean = false
)

fun NoteEntity.toDomainModel(): NoteModel {
    return NoteModel(
        id = id,
        title = title,
        content = content,
        updatedAt = updatedAt,
        isPinned = isPinned
    )
}

fun NoteModel.toEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        updatedAt = updatedAt,
        isPinned = isPinned
    )
}

