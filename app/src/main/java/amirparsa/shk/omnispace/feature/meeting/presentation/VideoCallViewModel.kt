package amirparsa.shk.omnispace.feature.meeting.presentation

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import amirparsa.shk.omnispace.core.network.SessionManager
import amirparsa.shk.omnispace.core.base.mvi.MviViewModel
import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.connect.domain.repository.ConnectRepository
import amirparsa.shk.omnispace.feature.meeting.domain.model.CallParticipant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * MVI ViewModel managing video meeting state, controls, and timer.
 */
@HiltViewModel
class VideoCallViewModel @Inject constructor(
    private val connectRepository: ConnectRepository,
    private val sessionManager: SessionManager
) : MviViewModel<VideoCallUiState, VideoCallIntent, VideoCallEffect>(
    initialState = VideoCallUiState()
) {

    init {
        val myName = sessionManager.displayName?.ifBlank { "شما" } ?: "شما"
        val myInitials = if (myName.length >= 2) myName.take(2) else "شما"
        val selfParticipant = CallParticipant(
            id = sessionManager.userId ?: "self",
            name = "$myName (شما)",
            initials = myInitials,
            isMicMuted = false,
            isCameraOff = false,
            isSelf = true,
            avatarBgColor = 0xFF701A75L
        )
        setState { copy(participants = listOf(selfParticipant)) }
        startTimer()
        joinMeeting(currentState.roomCode)
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (isActive) {
                delay(1000)
                setState { copy(elapsedSeconds = elapsedSeconds + 1) }
            }
        }
    }

    fun joinMeeting(roomCode: String = currentState.roomCode) {
        onIntent(VideoCallIntent.JoinMeeting(roomCode))
    }

    fun leaveMeeting(roomCode: String = currentState.roomCode) {
        onIntent(VideoCallIntent.LeaveMeeting(roomCode))
    }

    override fun processIntent(intent: VideoCallIntent) {
        when (intent) {
            is VideoCallIntent.JoinMeeting -> {
                setState { copy(roomCode = intent.roomCode, isLoading = true, errorMessage = null) }
                viewModelScope.launch {
                    when (val result = connectRepository.getMeetingJoinToken(intent.roomCode)) {
                        is NetworkResult.Success -> {
                            setState {
                                copy(
                                    isLoading = false,
                                    meetingToken = result.data.token,
                                    serverUrl = result.data.serverUrl ?: result.data.url,
                                    errorMessage = null
                                )
                            }
                        }
                        is NetworkResult.Error -> {
                            setState {
                                copy(
                                    isLoading = false,
                                    errorMessage = result.message
                                )
                            }
                        }
                        NetworkResult.Loading -> {
                            setState { copy(isLoading = true) }
                        }
                    }
                }
            }

            is VideoCallIntent.LeaveMeeting -> {
                handleLeaveMeeting(intent.roomCode ?: currentState.roomCode)
            }

            VideoCallIntent.LeaveCallClicked -> {
                handleLeaveMeeting(currentState.roomCode)
            }

            VideoCallIntent.ToggleMic -> toggleMic()
            VideoCallIntent.ToggleCamera -> toggleCamera()
            is VideoCallIntent.SetMicMuted -> setMicMuted(intent.isMuted)
            is VideoCallIntent.SetCameraOff -> setCameraOff(intent.isCameraOff)
            VideoCallIntent.SwitchCamera -> setState { copy(isFrontCamera = !isFrontCamera) }
            VideoCallIntent.ToggleScreenShare -> setState { copy(isScreenSharing = !isScreenSharing) }
            VideoCallIntent.OpenChatClicked -> sendEffect(VideoCallEffect.OpenChat)
            VideoCallIntent.RequestPictureInPicture -> sendEffect(VideoCallEffect.EnterPictureInPicture)
            VideoCallIntent.TickTimer -> setState { copy(elapsedSeconds = elapsedSeconds + 1) }
        }
    }

    private fun handleLeaveMeeting(roomCode: String) {
        setState { copy(isLeaving = true) }
        viewModelScope.launch {
            try {
                connectRepository.leaveMeeting(roomCode)
            } finally {
                setState { copy(isLeaving = false) }
                sendEffect(VideoCallEffect.NavigateBack)
            }
        }
    }

    fun toggleMic() {
        setState {
            val newMuted = !isMicMuted
            copy(
                isMicMuted = newMuted,
                participants = participants.map {
                    if (it.isSelf) it.copy(isMicMuted = newMuted) else it
                }
            )
        }
    }

    fun toggleCamera() {
        setState {
            val newCameraOff = !isCameraOff
            copy(
                isCameraOff = newCameraOff,
                participants = participants.map {
                    if (it.isSelf) it.copy(isCameraOff = newCameraOff) else it
                }
            )
        }
    }

    fun setMicMuted(isMuted: Boolean) {
        setState {
            copy(
                isMicMuted = isMuted,
                participants = participants.map {
                    if (it.isSelf) it.copy(isMicMuted = isMuted) else it
                }
            )
        }
    }

    fun setCameraOff(isCameraOff: Boolean) {
        setState {
            copy(
                isCameraOff = isCameraOff,
                participants = participants.map {
                    if (it.isSelf) it.copy(isCameraOff = isCameraOff) else it
                }
            )
        }
    }
}

