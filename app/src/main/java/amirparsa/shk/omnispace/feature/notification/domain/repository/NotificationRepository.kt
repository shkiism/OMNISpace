package amirparsa.shk.omnispace.feature.notification.domain.repository

import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.notification.data.remote.dto.NotificationListResponseDto

interface NotificationRepository {
    suspend fun getNotifications(): NetworkResult<NotificationListResponseDto>
}

