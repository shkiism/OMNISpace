package amirparsa.shk.omnispace.feature.notification.data.repository

import android.content.Context
import amirparsa.shk.omnispace.core.network.OmniHttpClient
import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.notification.data.remote.dto.NotificationListResponseDto
import amirparsa.shk.omnispace.feature.notification.domain.repository.NotificationRepository
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : NotificationRepository {

    private val client = OmniHttpClient.getClient(context)
    private val gson = Gson()

    override suspend fun getNotifications(): NetworkResult<NotificationListResponseDto> =
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("${OmniHttpClient.WORK_BASE}/notifications")
                    .build()
                val response = client.newCall(request).execute()
                val json = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    val data = gson.fromJson(json, NotificationListResponseDto::class.java)
                    NetworkResult.Success(data)
                } else {
                    NetworkResult.Error(response.code, "خطا در دریافت اعلانات")
                }
            } catch (e: Exception) {
                NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
            }
        }
}

