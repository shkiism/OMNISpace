package amirparsa.shk.omnispace.feature.issue.domain.model

data class IssueCommentUi(
    val id: String,
    val authorName: String,
    val authorInitials: String,
    val time: String,
    val text: String,
    val repliesCount: Int = 0
)

