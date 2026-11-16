package amirparsa.shk.omnispace.feature.connect.domain.model

/**
 * Domain entity representing an active LiveKit room item for audio/video meetings.
 */
data class LiveKitRoomItem(
    val name: String,
    val info: String,
    val participantsCount: Int = 0
)
