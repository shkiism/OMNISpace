package amirparsa.shk.omnispace.presentation.main.model

/**
 * Arguments required to navigate to a specific Chat room.
 */
data class ChatNavArgs(
    val title: String,
    val subtitle: String,
    val linkedIssueKey: String? = null,
    val linkedIssueTitle: String? = null
)

