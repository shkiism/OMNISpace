package amirparsa.shk.omnispace.feature.today.domain.model

data class NoteModel(
    val id: String,
    val title: String,
    val content: String,
    val updatedAt: String,
    val isPinned: Boolean = false
)

