package amirparsa.shk.omnispace.feature.people.domain.model

/**
 * Domain entity representing a shared task between colleagues.
 */
data class SharedTask(
    val key: String,
    val title: String,
    val status: String
)

