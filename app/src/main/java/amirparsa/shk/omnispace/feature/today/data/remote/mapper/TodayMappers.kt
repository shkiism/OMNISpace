package amirparsa.shk.omnispace.feature.today.data.remote.mapper

import amirparsa.shk.omnispace.feature.today.data.remote.dto.HabitDto
import amirparsa.shk.omnispace.feature.today.data.remote.dto.NoteDto
import amirparsa.shk.omnispace.feature.today.domain.model.HabitItem
import amirparsa.shk.omnispace.feature.today.domain.model.NoteModel

fun HabitDto.toDomainModel(): HabitItem {
    return HabitItem(
        id = id,
        title = name,
        isDone = doneToday
    )
}

fun NoteDto.toDomainModel(): NoteModel {
    return NoteModel(
        id = id,
        title = title,
        content = content ?: body ?: "",
        updatedAt = updatedAt?.take(10) ?: createdAt?.take(10) ?: "امروز",
        isPinned = pinned
    )
}

