package amirparsa.shk.omnispace.feature.work.domain.model

data class KanbanCardItem(
    val id: String,
    val key: String,
    val title: String,
    val type: String,
    val typeColor: Long,
    val status: KanbanColumnStatus,
    val priorityName: String,
    val priorityColor: Long,
    val assigneeInitials: String,
    val dueDate: String
)
