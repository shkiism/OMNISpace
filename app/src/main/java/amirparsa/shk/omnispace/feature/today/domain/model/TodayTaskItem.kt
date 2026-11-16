package amirparsa.shk.omnispace.feature.today.domain.model

data class TodayTaskItem(
    val id: String,
    val title: String,
    val projectKey: String,
    val subtitle: String,
    val isDone: Boolean,
    val priorityColor: Long,
    val avatarInitials: String? = null,
    val completedTime: String? = null,
    val dueTime: String? = null
)
