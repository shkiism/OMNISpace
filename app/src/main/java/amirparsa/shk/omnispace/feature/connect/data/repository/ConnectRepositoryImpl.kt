package amirparsa.shk.omnispace.feature.connect.data.repository

import android.content.Context
import amirparsa.shk.omnispace.core.database.dao.PersonDao
import amirparsa.shk.omnispace.core.database.entity.toDto
import amirparsa.shk.omnispace.core.database.entity.toEntity
import amirparsa.shk.omnispace.core.network.OmniHttpClient
import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.connect.data.remote.dto.ChatMessageDto
import amirparsa.shk.omnispace.feature.connect.data.remote.dto.ChatStatusDto
import amirparsa.shk.omnispace.feature.connect.data.remote.dto.MeetingTokenDto
import amirparsa.shk.omnispace.feature.connect.data.remote.dto.PersonDto
import amirparsa.shk.omnispace.feature.connect.data.remote.dto.SendChatMessagePayload
import amirparsa.shk.omnispace.feature.connect.data.remote.dto.TeamDto
import amirparsa.shk.omnispace.feature.connect.data.remote.dto.VideoStatusDto
import amirparsa.shk.omnispace.feature.connect.domain.repository.ConnectRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val personDao: PersonDao
) : ConnectRepository {

    private val client = OmniHttpClient.getClient(context)
    private val gson = Gson()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    override suspend fun getPeople(): NetworkResult<List<PersonDto>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${OmniHttpClient.WORK_BASE}/people")
                .build()
            val response = client.newCall(request).execute()
            val json = response.body?.string().orEmpty()

            if (response.isSuccessful) {
                if (json.isNotBlank()) {
                    val listType = object : TypeToken<List<PersonDto>>() {}.type
                    val data: List<PersonDto> = gson.fromJson(json, listType) ?: emptyList()
                    if (data.isNotEmpty()) {
                        personDao.insertAll(data.map { it.toEntity() })
                    }
                    NetworkResult.Success(data)
                } else {
                    NetworkResult.Success(emptyList())
                }
            } else {
                // Room cache fallback if available
                val cached = personDao.getAllPeople().map { it.toDto() }
                if (cached.isNotEmpty()) {
                    NetworkResult.Success(cached)
                } else {
                    NetworkResult.Error(response.code, "خطا در دریافت لیست افراد (${response.code})")
                }
            }
        } catch (e: Exception) {
            val cached = personDao.getAllPeople().map { it.toDto() }
            if (cached.isNotEmpty()) {
                NetworkResult.Success(cached)
            } else {
                NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
            }
        }
    }

    override suspend fun getTeams(): NetworkResult<List<TeamDto>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${OmniHttpClient.WORK_BASE}/people/teams")
                .build()
            val response = client.newCall(request).execute()
            val json = response.body?.string().orEmpty()

            if (response.isSuccessful) {
                if (json.isNotBlank()) {
                    val listType = object : TypeToken<List<TeamDto>>() {}.type
                    val data: List<TeamDto> = gson.fromJson(json, listType) ?: emptyList()
                    NetworkResult.Success(data)
                } else {
                    NetworkResult.Success(emptyList())
                }
            } else {
                NetworkResult.Error(response.code, "خطا در دریافت لیست تیم‌ها (${response.code})")
            }
        } catch (e: Exception) {
            NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور تیم‌ها")
        }
    }

    override suspend fun getChatStatus(): NetworkResult<ChatStatusDto> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${OmniHttpClient.WORK_BASE}/chat/status")
                .build()
            val response = client.newCall(request).execute()
            val json = response.body?.string().orEmpty()

            if (response.isSuccessful && json.isNotBlank()) {
                val data = gson.fromJson(json, ChatStatusDto::class.java)
                NetworkResult.Success(data)
            } else {
                NetworkResult.Error(response.code, "خطا در استعلام وضعیت سرور چت (${response.code})")
            }
        } catch (e: Exception) {
            NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور چت")
        }
    }

    override suspend fun getVideoStatus(): NetworkResult<VideoStatusDto> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${OmniHttpClient.WORK_BASE}/meetings-video/status")
                .build()
            val response = client.newCall(request).execute()
            val json = response.body?.string().orEmpty()

            if (response.isSuccessful && json.isNotBlank()) {
                val data = gson.fromJson(json, VideoStatusDto::class.java)
                NetworkResult.Success(data)
            } else {
                NetworkResult.Error(response.code, "خطا در استعلام وضعیت سرور ویدیو (${response.code})")
            }
        } catch (e: Exception) {
            NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور ویدیو")
        }
    }

    override suspend fun getChatMessages(roomId: String): NetworkResult<List<ChatMessageDto>> =
        withContext(Dispatchers.IO) {
            try {
                val primaryUrl = "${OmniHttpClient.WORK_BASE}/chat/issues/$roomId/messages"
                var request = Request.Builder().url(primaryUrl).build()
                var response = client.newCall(request).execute()

                if (response.code == 404) {
                    val fallbackUrl = "${OmniHttpClient.WORK_BASE}/chat/rooms/$roomId/messages"
                    request = Request.Builder().url(fallbackUrl).build()
                    response = client.newCall(request).execute()
                }

                val json = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    if (json.isNotBlank()) {
                        val listType = object : TypeToken<List<ChatMessageDto>>() {}.type
                        val list: List<ChatMessageDto> = if (json.trim().startsWith("[")) {
                            gson.fromJson(json, listType) ?: emptyList()
                        } else {
                            try {
                                val jsonObject = com.google.gson.JsonParser.parseString(json).asJsonObject
                                val arr = jsonObject.get("messages") ?: jsonObject.get("data")
                                if (arr != null && arr.isJsonArray) {
                                    gson.fromJson(arr, listType) ?: emptyList()
                                } else {
                                    emptyList()
                                }
                            } catch (e: Exception) {
                                emptyList()
                            }
                        }
                        NetworkResult.Success(list)
                    } else {
                        NetworkResult.Success(emptyList())
                    }
                } else {
                    NetworkResult.Error(response.code, "خطا در دریافت پیام‌ها (${response.code})")
                }
            } catch (e: Exception) {
                NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور چت")
            }
        }

    override suspend fun sendChatMessage(roomId: String, text: String): NetworkResult<ChatMessageDto> =
        withContext(Dispatchers.IO) {
            try {
                val payload = SendChatMessagePayload(text = text)
                val body = gson.toJson(payload).toRequestBody(jsonMediaType)
                val primaryUrl = "${OmniHttpClient.WORK_BASE}/chat/issues/$roomId/messages"
                var request = Request.Builder().url(primaryUrl).post(body).build()
                var response = client.newCall(request).execute()

                if (response.code == 404) {
                    val fallbackUrl = "${OmniHttpClient.WORK_BASE}/chat/rooms/$roomId/messages"
                    val fallbackBody = gson.toJson(payload).toRequestBody(jsonMediaType)
                    request = Request.Builder().url(fallbackUrl).post(fallbackBody).build()
                    response = client.newCall(request).execute()
                }

                val json = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    val messageDto: ChatMessageDto = if (json.isNotBlank()) {
                        try {
                            val jsonObject = com.google.gson.JsonParser.parseString(json).asJsonObject
                            if (jsonObject.has("data") && jsonObject.get("data").isJsonObject) {
                                gson.fromJson(jsonObject.get("data"), ChatMessageDto::class.java)
                            } else if (jsonObject.has("message") && jsonObject.get("message").isJsonObject) {
                                gson.fromJson(jsonObject.get("message"), ChatMessageDto::class.java)
                            } else {
                                gson.fromJson(json, ChatMessageDto::class.java)
                            }
                        } catch (e: Exception) {
                            ChatMessageDto(
                                id = "msg_${System.currentTimeMillis()}",
                                roomId = roomId,
                                text = text,
                                createdAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date())
                            )
                        }
                    } else {
                        ChatMessageDto(
                            id = "msg_${System.currentTimeMillis()}",
                            roomId = roomId,
                            text = text,
                            createdAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date())
                        )
                    }
                    NetworkResult.Success(messageDto)
                } else {
                    NetworkResult.Error(response.code, "خطا در ارسال پیام (${response.code})")
                }
            } catch (e: Exception) {
                NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
            }
        }

    override suspend fun getMeetingJoinToken(roomCode: String): NetworkResult<MeetingTokenDto> =
        withContext(Dispatchers.IO) {
            try {
                val primaryUrl = "${OmniHttpClient.WORK_BASE}/meetings-video/$roomCode/join"
                var request = Request.Builder()
                    .url(primaryUrl)
                    .post("{}".toRequestBody(jsonMediaType))
                    .build()
                var response = client.newCall(request).execute()

                if (response.code == 404 || response.code == 405) {
                    val fallbackUrl = "${OmniHttpClient.WORK_BASE}/chat/rooms/$roomCode/call-token"
                    request = Request.Builder().url(fallbackUrl).build()
                    response = client.newCall(request).execute()
                }

                val json = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    val tokenDto: MeetingTokenDto = if (json.isNotBlank()) {
                        try {
                            val jsonObject = com.google.gson.JsonParser.parseString(json).asJsonObject
                            if (jsonObject.has("data") && jsonObject.get("data").isJsonObject) {
                                gson.fromJson(jsonObject.get("data"), MeetingTokenDto::class.java)
                            } else {
                                gson.fromJson(json, MeetingTokenDto::class.java)
                            }
                        } catch (e: Exception) {
                            MeetingTokenDto(token = json, roomCode = roomCode)
                        }
                    } else {
                        MeetingTokenDto(roomCode = roomCode)
                    }
                    NetworkResult.Success(tokenDto)
                } else {
                    NetworkResult.Error(response.code, "خطا در دریافت توکن ورود به جلسه (${response.code})")
                }
            } catch (e: Exception) {
                NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور جلسه")
            }
        }

    override suspend fun leaveMeeting(roomCode: String): NetworkResult<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("${OmniHttpClient.WORK_BASE}/meetings-video/$roomCode/leave")
                    .post("{}".toRequestBody(jsonMediaType))
                    .build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    NetworkResult.Success(true)
                } else {
                    NetworkResult.Error(response.code, "خطا در خروج از جلسه (${response.code})")
                }
            } catch (e: Exception) {
                NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
            }
        }
}
