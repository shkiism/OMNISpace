package amirparsa.shk.omnispace.feature.notification.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NotificationListResponseDto(
    @SerializedName("items")
    val items: List<NotificationDto> = emptyList(),
    @SerializedName("unread")
    val unread: Int = 0
)

data class NotificationDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("body")
    val body: String? = null,
    @SerializedName("created_at", alternate = ["createdAt"])
    val createdAt: String? = null
)

