package amirparsa.shk.omnispace.feature.meeting.domain.model

/**
 * Domain entity representing a participant in a video meeting.
 */
data class CallParticipant(
    val id: String,
    val name: String,
    val initials: String,
    val isMicMuted: Boolean = false,
    val isCameraOff: Boolean = false,
    val isSpeaking: Boolean = false,
    val isSelf: Boolean = false,
    val avatarBgColor: Long = 0xFF3B2E58L
)
