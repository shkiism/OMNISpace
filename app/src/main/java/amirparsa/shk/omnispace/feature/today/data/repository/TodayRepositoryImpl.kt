package amirparsa.shk.omnispace.feature.today.data.repository

import android.content.Context
import amirparsa.shk.omnispace.core.database.dao.MeetingDao
import amirparsa.shk.omnispace.core.database.dao.NoteDao
import amirparsa.shk.omnispace.core.database.dao.SyncDao
import amirparsa.shk.omnispace.core.database.dao.TaskDao
import amirparsa.shk.omnispace.core.database.entity.NoteEntity
import amirparsa.shk.omnispace.core.database.entity.SyncOpEntity
import amirparsa.shk.omnispace.core.database.entity.toDomainModel
import amirparsa.shk.omnispace.core.database.entity.toEntity
import amirparsa.shk.omnispace.core.network.OmniHttpClient
import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.today.data.remote.dto.CreateNotePayload
import amirparsa.shk.omnispace.feature.today.data.remote.dto.FocusSessionDto
import amirparsa.shk.omnispace.feature.today.data.remote.dto.FocusStatusDto
import amirparsa.shk.omnispace.feature.today.data.remote.dto.HabitDto
import amirparsa.shk.omnispace.feature.today.data.remote.dto.MeetingDto
import amirparsa.shk.omnispace.feature.today.data.remote.dto.NoteDto
import amirparsa.shk.omnispace.feature.today.data.remote.dto.SyncOpDto
import amirparsa.shk.omnispace.feature.today.data.remote.dto.SyncPullResponse
import amirparsa.shk.omnispace.feature.today.data.remote.dto.SyncPushPayload
import amirparsa.shk.omnispace.feature.today.data.remote.dto.SyncPushResponse
import amirparsa.shk.omnispace.feature.today.data.remote.dto.SyncResultDto
import amirparsa.shk.omnispace.feature.today.data.remote.dto.UpdateNotePayload
import amirparsa.shk.omnispace.feature.today.data.remote.mapper.toDomainModel
import amirparsa.shk.omnispace.feature.today.domain.model.HabitItem
import amirparsa.shk.omnispace.feature.today.domain.model.NoteModel
import amirparsa.shk.omnispace.feature.today.domain.model.UpcomingMeetingModel
import amirparsa.shk.omnispace.feature.today.domain.repository.TodayRepository
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
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodayRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val noteDao: NoteDao,
    private val meetingDao: MeetingDao,
    private val taskDao: TaskDao,
    private val syncDao: SyncDao
) : TodayRepository {

    private val client = OmniHttpClient.getClient(context)
    private val gson = Gson()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val syncPrefs = context.getSharedPreferences("omnispace_sync_prefs", Context.MODE_PRIVATE)
    private var syncCursor: Long
        get() = syncPrefs.getLong("sync_cursor", 0L)
        set(value) = syncPrefs.edit().putLong("sync_cursor", value).apply()

    private fun getTodayIsoDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    override suspend fun getFocusStatus(): NetworkResult<FocusStatusDto> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${OmniHttpClient.PRIVATE_BASE}/focus")
                .build()
            val response = client.newCall(request).execute()
            val json = response.body?.string().orEmpty()
            if (response.isSuccessful) {
                val data = gson.fromJson(json, FocusStatusDto::class.java)
                NetworkResult.Success(data)
            } else {
                NetworkResult.Error(response.code, "خطا در دریافت وضعیت فوکوس")
            }
        } catch (e: Exception) {
            NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
        }
    }

    override suspend fun startFocus(minutes: Int, taskId: String?): NetworkResult<FocusSessionDto> =
        withContext(Dispatchers.IO) {
            try {
                val payload = HashMap<String, Any>()
                payload["minutes"] = minutes
                if (!taskId.isNullOrBlank()) {
                    payload["taskId"] = taskId
                }

                val request = Request.Builder()
                    .url("${OmniHttpClient.PRIVATE_BASE}/focus/start")
                    .post(gson.toJson(payload).toRequestBody(jsonMediaType))
                    .build()

                val response = client.newCall(request).execute()
                val json = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    val data = gson.fromJson(json, FocusSessionDto::class.java)
                    NetworkResult.Success(data)
                } else {
                    NetworkResult.Error(response.code, "خطا در آغاز تایمر فوکوس")
                }
            } catch (e: Exception) {
                NetworkResult.Error(-1, e.localizedMessage ?: "خطای شبکه")
            }
        }

    override suspend fun stopFocus(): NetworkResult<FocusSessionDto> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${OmniHttpClient.PRIVATE_BASE}/focus/stop")
                .post("{}".toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val json = response.body?.string().orEmpty()
            if (response.isSuccessful) {
                val data = gson.fromJson(json, FocusSessionDto::class.java)
                NetworkResult.Success(data)
            } else {
                NetworkResult.Error(response.code, "خطا در متوقف‌سازی فوکوس")
            }
        } catch (e: Exception) {
            NetworkResult.Error(-1, e.localizedMessage ?: "خطای شبکه")
        }
    }

    override suspend fun getHabits(): NetworkResult<List<HabitItem>> = withContext(Dispatchers.IO) {
        try {
            val todayStr = getTodayIsoDate()
            val request = Request.Builder()
                .url("${OmniHttpClient.PRIVATE_BASE}/habits?today=$todayStr")
                .build()

            val response = client.newCall(request).execute()
            val json = response.body?.string().orEmpty()
            if (response.isSuccessful) {
                val listType = object : TypeToken<List<HabitDto>>() {}.type
                val dtos: List<HabitDto> = gson.fromJson(json, listType) ?: emptyList()
                val items = dtos.map { it.toDomainModel() }
                NetworkResult.Success(items)
            } else {
                NetworkResult.Error(response.code, "خطا در بارگذاری عادت‌ها")
            }
        } catch (e: Exception) {
            NetworkResult.Error(-1, e.localizedMessage ?: "خطای شبکه")
        }
    }

    override suspend fun toggleHabit(habitId: String, isDone: Boolean): NetworkResult<Boolean> =
        withContext(Dispatchers.IO) {
            // Queue sync operation in Outbox for offline resilience
            val op = SyncOpEntity(
                opId = UUID.randomUUID().toString(),
                tableName = "habits",
                recordId = habitId,
                baseVersion = 1L,
                payloadJson = gson.toJson(mapOf("done" to isDone, "date" to getTodayIsoDate())),
                status = "pending",
                createdAt = System.currentTimeMillis()
            )
            syncDao.insertOp(op)

            try {
                val todayStr = getTodayIsoDate()
                val payload = mapOf("done" to isDone)
                val request = Request.Builder()
                    .url("${OmniHttpClient.PRIVATE_BASE}/habits/$habitId/checks/$todayStr")
                    .put(gson.toJson(payload).toRequestBody(jsonMediaType))
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    syncDao.deleteOp(op.opId)
                    NetworkResult.Success(true)
                } else {
                    NetworkResult.Success(true)
                }
            } catch (e: Exception) {
                NetworkResult.Success(true)
            }
        }

    override suspend fun createHabit(title: String, schedule: String): NetworkResult<String> =
        withContext(Dispatchers.IO) {
            val localId = "habit-${System.currentTimeMillis()}"
            val op = SyncOpEntity(
                opId = UUID.randomUUID().toString(),
                tableName = "habits",
                recordId = localId,
                baseVersion = 1L,
                payloadJson = gson.toJson(mapOf("name" to title, "schedule" to schedule)),
                status = "pending",
                createdAt = System.currentTimeMillis()
            )
            syncDao.insertOp(op)

            try {
                val payload = mapOf("name" to title, "schedule" to schedule)
                val request = Request.Builder()
                    .url("${OmniHttpClient.PRIVATE_BASE}/habits")
                    .post(gson.toJson(payload).toRequestBody(jsonMediaType))
                    .build()

                val response = client.newCall(request).execute()
                val json = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    val resMap = gson.fromJson(json, Map::class.java)
                    val id = resMap["id"] as? String ?: localId
                    syncDao.deleteOp(op.opId)
                    NetworkResult.Success(id)
                } else {
                    NetworkResult.Success(localId)
                }
            } catch (e: Exception) {
                NetworkResult.Success(localId)
            }
        }

    // --- Notes with Room Database caching and Room Outbox Sync ---

    override suspend fun getNotes(): NetworkResult<List<NoteModel>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${OmniHttpClient.PRIVATE_BASE}/notes")
                .build()

            val response = client.newCall(request).execute()
            val json = response.body?.string().orEmpty()
            if (response.isSuccessful && json.isNotBlank()) {
                val listType = object : TypeToken<List<NoteDto>>() {}.type
                val dtos: List<NoteDto> = gson.fromJson(json, listType) ?: emptyList()
                val notes = dtos.map { it.toDomainModel() }
                if (notes.isNotEmpty()) {
                    noteDao.insertAll(notes.map { it.toEntity() })
                    return@withContext NetworkResult.Success(notes)
                }
            }
            // Room cache fallback (No static fallback)
            val cached = noteDao.getAllNotes().map { it.toDomainModel() }
            if (cached.isNotEmpty()) {
                NetworkResult.Success(cached)
            } else {
                NetworkResult.Error(response.code, "یادداشتی در حافظه محلی یا سرور یافت نشد")
            }
        } catch (e: Exception) {
            val cached = noteDao.getAllNotes().map { it.toDomainModel() }
            if (cached.isNotEmpty()) {
                NetworkResult.Success(cached)
            } else {
                NetworkResult.Error(-1, e.localizedMessage ?: "خطای شبکه")
            }
        }
    }

    override suspend fun createNote(title: String, content: String): NetworkResult<NoteModel> =
        withContext(Dispatchers.IO) {
            val localEntity = NoteEntity(
                id = "note-${System.currentTimeMillis()}",
                title = title,
                content = content,
                updatedAt = "هم‌اکنون",
                isPinned = false
            )
            // Save to local Room immediately
            noteDao.insert(localEntity)

            // Queue a SyncOpEntity in SyncDao
            val op = SyncOpEntity(
                opId = UUID.randomUUID().toString(),
                tableName = "notes",
                recordId = localEntity.id,
                baseVersion = 1L,
                payloadJson = gson.toJson(CreateNotePayload(title = title, content = content)),
                status = "pending",
                createdAt = System.currentTimeMillis()
            )
            syncDao.insertOp(op)

            // Trigger sync engine
            try {
                syncPrivateZone()
            } catch (_: Exception) {}

            NetworkResult.Success(localEntity.toDomainModel())
        }

    override suspend fun updateNote(
        id: String,
        title: String,
        content: String,
        isPinned: Boolean
    ): NetworkResult<Boolean> = withContext(Dispatchers.IO) {
        // Update Room local cache immediately
        noteDao.updateNote(id, title, content, "هم‌اکنون", isPinned)

        // Queue in Room Outbox
        val op = SyncOpEntity(
            opId = UUID.randomUUID().toString(),
            tableName = "notes",
            recordId = id,
            baseVersion = 1L,
            payloadJson = gson.toJson(UpdateNotePayload(title = title, content = content, pinned = isPinned)),
            status = "pending",
            createdAt = System.currentTimeMillis()
        )
        syncDao.insertOp(op)

        // Trigger sync engine
        try {
            syncPrivateZone()
        } catch (_: Exception) {}

        NetworkResult.Success(true)
    }

    override suspend fun togglePinNote(id: String, isPinned: Boolean): NetworkResult<Boolean> =
        withContext(Dispatchers.IO) {
            noteDao.updatePinned(id, isPinned)

            val op = SyncOpEntity(
                opId = UUID.randomUUID().toString(),
                tableName = "notes",
                recordId = id,
                baseVersion = 1L,
                payloadJson = gson.toJson(mapOf("pinned" to isPinned)),
                status = "pending",
                createdAt = System.currentTimeMillis()
            )
            syncDao.insertOp(op)

            try {
                syncPrivateZone()
            } catch (_: Exception) {}

            NetworkResult.Success(true)
        }

    override suspend fun deleteNote(id: String): NetworkResult<Boolean> = withContext(Dispatchers.IO) {
        noteDao.deleteById(id)

        val op = SyncOpEntity(
            opId = UUID.randomUUID().toString(),
            tableName = "notes",
            recordId = id,
            baseVersion = 1L,
            payloadJson = gson.toJson(mapOf("action" to "delete", "id" to id)),
            status = "pending",
            createdAt = System.currentTimeMillis()
        )
        syncDao.insertOp(op)

        try {
            syncPrivateZone()
        } catch (_: Exception) {}

        NetworkResult.Success(true)
    }

    override suspend fun toggleTask(taskId: String, isDone: Boolean): NetworkResult<Boolean> =
        withContext(Dispatchers.IO) {
            val existing = taskDao.getTaskByIdOrKey(taskId)
            if (existing != null) {
                taskDao.insert(existing.copy(statusId = if (isDone) "done" else "in_progress"))
            }

            val op = SyncOpEntity(
                opId = UUID.randomUUID().toString(),
                tableName = "tasks",
                recordId = taskId,
                baseVersion = 1L,
                payloadJson = gson.toJson(mapOf("taskId" to taskId, "done" to isDone)),
                status = "pending",
                createdAt = System.currentTimeMillis()
            )
            syncDao.insertOp(op)

            try {
                syncPrivateZone()
            } catch (_: Exception) {}

            NetworkResult.Success(true)
        }

    override suspend fun syncPrivateZone(): NetworkResult<SyncResultDto> = withContext(Dispatchers.IO) {
        var appliedCount = 0
        try {
            // 1. PUSH: Read pending operations from SyncDao
            val pendingOps = syncDao.getPendingOps()
            if (pendingOps.isNotEmpty()) {
                val opsPayload = pendingOps.map { op ->
                    SyncOpDto(
                        opId = op.opId,
                        tableName = op.tableName,
                        recordId = op.recordId,
                        baseVersion = op.baseVersion,
                        payloadJson = op.payloadJson,
                        createdAt = op.createdAt
                    )
                }
                val pushPayload = SyncPushPayload(
                    clientId = "android_app",
                    ops = opsPayload
                )
                val pushRequest = Request.Builder()
                    .url("${OmniHttpClient.PRIVATE_BASE}/sync")
                    .post(gson.toJson(pushPayload).toRequestBody(jsonMediaType))
                    .build()

                val pushResponse = client.newCall(pushRequest).execute()
                if (pushResponse.isSuccessful) {
                    val pushBody = pushResponse.body?.string().orEmpty()
                    val appliedIds = try {
                        val parsed = gson.fromJson(pushBody, SyncPushResponse::class.java)
                        parsed?.appliedIds?.takeIf { it.isNotEmpty() } ?: pendingOps.map { it.opId }
                    } catch (_: Exception) {
                        pendingOps.map { it.opId }
                    }
                    appliedCount = appliedIds.size
                    for (id in appliedIds) {
                        syncDao.deleteOp(id)
                    }
                }
            }

            // 2. PULL: Call GET ${OmniHttpClient.PRIVATE_BASE}/sync?cursor=0&limit=500
            val pullUrl = "${OmniHttpClient.PRIVATE_BASE}/sync?cursor=$syncCursor&limit=500"
            val pullRequest = Request.Builder()
                .url(pullUrl)
                .get()
                .build()

            val pullResponse = client.newCall(pullRequest).execute()
            val pullJson = pullResponse.body?.string().orEmpty()

            var pulledNotesCount = 0
            var pulledTasksCount = 0

            if (pullResponse.isSuccessful && pullJson.isNotBlank()) {
                try {
                    val pullResult = gson.fromJson(pullJson, SyncPullResponse::class.java)
                    if (pullResult != null) {
                        if (pullResult.cursor > 0) {
                            syncCursor = pullResult.cursor
                        }
                        if (pullResult.notes.isNotEmpty()) {
                            noteDao.insertAll(pullResult.notes.map { it.toDomainModel().toEntity() })
                            pulledNotesCount = pullResult.notes.size
                        }
                        if (pullResult.tasks.isNotEmpty()) {
                            taskDao.insertAll(pullResult.tasks.map { it.toEntity() })
                            pulledTasksCount = pullResult.tasks.size
                        }
                    }
                } catch (_: Exception) {}
            }

            NetworkResult.Success(
                SyncResultDto(
                    cursor = syncCursor,
                    appliedCount = appliedCount,
                    pulledNotesCount = pulledNotesCount,
                    pulledTasksCount = pulledTasksCount,
                    status = "synced",
                    message = "همگام‌سازی با موفقیت انجام شد"
                )
            )
        } catch (e: Exception) {
            NetworkResult.Error(
                code = -1,
                message = e.localizedMessage ?: "عدم دسترسی به شبکه - عملیات در صف آفلاین ذخیره شد"
            )
        }
    }

    override suspend fun getUpcomingMeeting(): UpcomingMeetingModel? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${OmniHttpClient.WORK_BASE}/meetings/upcoming")
                .build()
            val response = client.newCall(request).execute()
            val json = response.body?.string().orEmpty()
            if (response.isSuccessful && json.isNotBlank()) {
                val dto = gson.fromJson(json, MeetingDto::class.java)
                if (dto != null && !dto.title.isNullOrBlank()) {
                    val meeting = UpcomingMeetingModel(
                        id = dto.id ?: "meet-livekit",
                        title = dto.title,
                        subtitle = dto.topic ?: dto.description ?: "",
                        timeFormatted = dto.startTime ?: "",
                        durationText = "مدت: ۴۵ دقیقه",
                        platformName = dto.platform ?: "LiveKit Meet",
                        meetUrl = dto.meetUrl ?: "",
                        attendeeInitials = dto.attendees.map { it.take(2) }
                    )
                    meetingDao.insertMeeting(meeting.toEntity())
                    return@withContext meeting
                }
            }
            meetingDao.getUpcomingMeeting()?.toDomainModel()
        } catch (e: Exception) {
            meetingDao.getUpcomingMeeting()?.toDomainModel()
        }
    }
}
