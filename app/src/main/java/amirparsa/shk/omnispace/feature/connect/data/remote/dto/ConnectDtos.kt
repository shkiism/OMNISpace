package amirparsa.shk.omnispace.feature.connect.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PersonDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("displayName")
    val displayName: String,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("orgRole")
    val orgRole: String? = null,
    @SerializedName("handle")
    val handle: String? = null,
    @SerializedName("avatarV")
    val avatarV: Int? = null
)

data class TeamDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("departmentName")
    val departmentName: String? = null,
    @SerializedName("leadName")
    val leadName: String? = null,
    @SerializedName("members")
    val members: Int = 0
)

data class ChatStatusDto(
    @SerializedName("enabled")
    val enabled: Boolean = false,
    @SerializedName("serverName")
    val serverName: String? = null,
    @SerializedName("baseUrl")
    val baseUrl: String? = null
)

data class VideoStatusDto(
    @SerializedName("enabled")
    val enabled: Boolean = false,
    @SerializedName("max")
    val max: Int = 100
)

data class ChatMessageDto(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("roomId")
    val roomId: String? = null,
    @SerializedName("senderId")
    val senderId: String? = null,
    @SerializedName("senderName")
    val senderName: String? = null,
    @SerializedName("authorName")
    val authorName: String? = null,
    @SerializedName("text")
    val text: String? = null,
    @SerializedName("content")
    val content: String? = null,
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("timestamp")
    val timestamp: String? = null,
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("isFromMe")
    val isFromMe: Boolean? = null
)

data class MeetingTokenDto(
    @SerializedName("token")
    val token: String? = null,
    @SerializedName("url")
    val url: String? = null,
    @SerializedName("serverUrl")
    val serverUrl: String? = null,
    @SerializedName("room")
    val room: String? = null,
    @SerializedName("roomCode")
    val roomCode: String? = null,
    @SerializedName("participantName")
    val participantName: String? = null
)

data class SendChatMessagePayload(
    @SerializedName("text")
    val text: String
)

