package amirparsa.shk.omnispace.feature.connect.domain.repository

import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.connect.data.remote.dto.ChatMessageDto
import amirparsa.shk.omnispace.feature.connect.data.remote.dto.ChatStatusDto
import amirparsa.shk.omnispace.feature.connect.data.remote.dto.MeetingTokenDto
import amirparsa.shk.omnispace.feature.connect.data.remote.dto.PersonDto
import amirparsa.shk.omnispace.feature.connect.data.remote.dto.TeamDto
import amirparsa.shk.omnispace.feature.connect.data.remote.dto.VideoStatusDto

interface ConnectRepository {
    suspend fun getPeople(): NetworkResult<List<PersonDto>>
    suspend fun getTeams(): NetworkResult<List<TeamDto>>
    suspend fun getChatStatus(): NetworkResult<ChatStatusDto>
    suspend fun getVideoStatus(): NetworkResult<VideoStatusDto>

    suspend fun getChatMessages(roomId: String): NetworkResult<List<ChatMessageDto>>
    suspend fun sendChatMessage(roomId: String, text: String): NetworkResult<ChatMessageDto>
    suspend fun getMeetingJoinToken(roomCode: String): NetworkResult<MeetingTokenDto>
    suspend fun leaveMeeting(roomCode: String): NetworkResult<Boolean>
}

