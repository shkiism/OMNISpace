package amirparsa.shk.omnispace.feature.work.domain.model

data class WorkspaceIssue(
    val id: String,
    val key: String,
    val title: String,
    val priorityName: String,
    val priorityColor: Long,
    val tagCategory: String? = null,
    val assigneeInitials: String? = null,
    val isDone: Boolean = false,
    val subtasksCount: String? = null,
    val commentsCount: Int? = null,
    val dueText: String? = null
)
