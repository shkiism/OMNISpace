package amirparsa.shk.omnispace.feature.meeting.presentation

import androidx.compose.ui.graphics.Color
import amirparsa.shk.omnispace.core.base.mvi.UiEffect
import amirparsa.shk.omnispace.core.base.mvi.UiIntent
import amirparsa.shk.omnispace.core.base.mvi.UiState
import amirparsa.shk.omnispace.feature.meeting.domain.model.CallParticipant

/**
 * Immutable UI State for Video Call.
 */
data class VideoCallUiState(
    val roomCode: String = "general",
    val roomTitle: String = "جلسه ویدیویی",
    val meetingToken: String? = null,
    val serverUrl: String? = null,
    val elapsedSeconds: Int = 0,
    val isMicMuted: Boolean = false,
    val isCameraOff: Boolean = false,
    val isScreenSharing: Boolean = false,
    val isFrontCamera: Boolean = true,
    val isLeaving: Boolean = false,
    val participants: List<CallParticipant> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : UiState {
    val durationText: String
        get() {
            val minutes = elapsedSeconds / 60
            val seconds = elapsedSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
}

/**
 * User intents for Video Call.
 */
sealed interface VideoCallIntent : UiIntent {
    data class JoinMeeting(val roomCode: String) : VideoCallIntent
    data class LeaveMeeting(val roomCode: String? = null) : VideoCallIntent
    data class SetMicMuted(val isMuted: Boolean) : VideoCallIntent
    data class SetCameraOff(val isCameraOff: Boolean) : VideoCallIntent
    data object ToggleMic : VideoCallIntent
    data object ToggleCamera : VideoCallIntent
    data object SwitchCamera : VideoCallIntent
    data object ToggleScreenShare : VideoCallIntent
    data object LeaveCallClicked : VideoCallIntent
    data object OpenChatClicked : VideoCallIntent
    data object RequestPictureInPicture : VideoCallIntent
    data object TickTimer : VideoCallIntent
}

/**
 * One-time side-effects for Video Call.
 */
sealed interface VideoCallEffect : UiEffect {
    data object NavigateBack : VideoCallEffect
    data object OpenChat : VideoCallEffect
    data object EnterPictureInPicture : VideoCallEffect
}

