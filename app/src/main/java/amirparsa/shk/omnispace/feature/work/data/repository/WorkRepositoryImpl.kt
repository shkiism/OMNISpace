package amirparsa.shk.omnispace.feature.work.data.repository

import android.content.Context
import amirparsa.shk.omnispace.core.database.dao.ProjectDao
import amirparsa.shk.omnispace.core.database.dao.TaskDao
import amirparsa.shk.omnispace.core.database.entity.TaskEntity
import amirparsa.shk.omnispace.core.database.entity.toDto
import amirparsa.shk.omnispace.core.database.entity.toEntity
import amirparsa.shk.omnispace.core.network.OmniHttpClient
import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.work.data.remote.dto.AddCommentPayload
import amirparsa.shk.omnispace.feature.work.data.remote.dto.ConfigDto
import amirparsa.shk.omnispace.feature.work.data.remote.dto.CreateIssuePayload
import amirparsa.shk.omnispace.feature.work.data.remote.dto.CreateIssueResponseDto
import amirparsa.shk.omnispace.feature.work.data.remote.dto.IssueCommentDto
import amirparsa.shk.omnispace.feature.work.data.remote.dto.IssueCommentsResponseDto
import amirparsa.shk.omnispace.feature.work.data.remote.dto.IssueDto
import amirparsa.shk.omnispace.feature.work.data.remote.dto.IssueListResponseDto
import amirparsa.shk.omnispace.feature.work.data.remote.dto.IssueTransitionDto
import amirparsa.shk.omnispace.feature.work.data.remote.dto.IssueTransitionsResponseDto
import amirparsa.shk.omnispace.feature.work.data.remote.dto.LogWorkPayload
import amirparsa.shk.omnispace.feature.work.data.remote.dto.ProjectDto
import amirparsa.shk.omnispace.feature.work.data.remote.dto.TestCaseDto
import amirparsa.shk.omnispace.feature.work.data.remote.dto.TransitionPayload
import amirparsa.shk.omnispace.feature.work.data.remote.dto.WorkflowDto
import amirparsa.shk.omnispace.feature.work.domain.repository.WorkRepository
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
class WorkRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val projectDao: ProjectDao,
    private val taskDao: TaskDao
) : WorkRepository {

    private val client = OmniHttpClient.getClient(context)
    private val gson = Gson()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    override suspend fun getProjects(): NetworkResult<List<ProjectDto>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${OmniHttpClient.WORK_BASE}/projects")
                .build()
            val response = client.newCall(request).execute()
            val json = response.body?.string().orEmpty()
            if (response.isSuccessful && json.isNotBlank()) {
                val listType = object : TypeToken<List<ProjectDto>>() {}.type
                val data: List<ProjectDto> = gson.fromJson(json, listType) ?: emptyList()
                if (data.isNotEmpty()) {
                    projectDao.insertAll(data.map { it.toEntity() })
                }
                return@withContext NetworkResult.Success(data)
            }
            // Room cache fallback on network failure
            val cached = projectDao.getAllProjects().map { it.toDto() }
            if (cached.isNotEmpty()) {
                NetworkResult.Success(cached)
            } else {
                NetworkResult.Error(response.code, "پروژه‌ای در حافظه محلی یا سرور یافت نشد")
            }
        } catch (e: Exception) {
            // Room cache fallback on error
            val cached = projectDao.getAllProjects().map { it.toDto() }
            if (cached.isNotEmpty()) {
                NetworkResult.Success(cached)
            } else {
                NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
            }
        }
    }

    override suspend fun getConfig(): NetworkResult<ConfigDto> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${OmniHttpClient.WORK_BASE}/config")
                .build()
            val response = client.newCall(request).execute()
            val json = response.body?.string().orEmpty()
            if (response.isSuccessful) {
                val data = gson.fromJson(json, ConfigDto::class.java)
                NetworkResult.Success(data)
            } else {
                NetworkResult.Error(response.code, "خطا در دریافت تنظیمات سیستم")
            }
        } catch (e: Exception) {
            NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
        }
    }

    override suspend fun getProjectWorkflow(projectId: String): NetworkResult<WorkflowDto> =
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("${OmniHttpClient.WORK_BASE}/projects/$projectId/workflow")
                    .build()
                val response = client.newCall(request).execute()
                val json = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    val data = gson.fromJson(json, WorkflowDto::class.java)
                    NetworkResult.Success(data)
                } else {
                    NetworkResult.Error(response.code, "خطا در دریافت جریان کار پروژه")
                }
            } catch (e: Exception) {
                NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
            }
        }

    override suspend fun getIssues(projectId: String): NetworkResult<List<IssueDto>> =
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("${OmniHttpClient.WORK_BASE}/issues?project=$projectId")
                    .build()
                val response = client.newCall(request).execute()
                val json = response.body?.string().orEmpty()
                if (response.isSuccessful && json.isNotBlank()) {
                    val responseObj = gson.fromJson(json, IssueListResponseDto::class.java)
                    val items = responseObj?.items ?: emptyList()
                    if (items.isNotEmpty()) {
                        taskDao.insertAll(items.map { it.toEntity(projectId) })
                    }
                    return@withContext NetworkResult.Success(items)
                }
                // Room cache fallback on network failure
                val cached = taskDao.getTasksByProject(projectId).map { it.toDto() }
                if (cached.isNotEmpty()) {
                    NetworkResult.Success(cached)
                } else {
                    NetworkResult.Error(response.code, "تسک فعالی در حافظه محلی یا سرور یافت نشد")
                }
            } catch (e: Exception) {
                // Room cache fallback on error
                val cached = taskDao.getTasksByProject(projectId).map { it.toDto() }
                if (cached.isNotEmpty()) {
                    NetworkResult.Success(cached)
                } else {
                    NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
                }
            }
        }

    override suspend fun getIssueByKey(key: String): NetworkResult<IssueDto> =
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("${OmniHttpClient.WORK_BASE}/issues/by-key/$key")
                    .build()
                val response = client.newCall(request).execute()
                val json = response.body?.string().orEmpty()
                if (response.isSuccessful && json.isNotBlank()) {
                    val data = gson.fromJson(json, IssueDto::class.java)
                    taskDao.insert(data.toEntity())
                    NetworkResult.Success(data)
                } else {
                    val cached = taskDao.getTaskByIdOrKey(key)?.toDto()
                    if (cached != null) {
                        NetworkResult.Success(cached)
                    } else {
                        NetworkResult.Error(response.code, "خطا در دریافت جزئیات تسک $key")
                    }
                }
            } catch (e: Exception) {
                val cached = taskDao.getTaskByIdOrKey(key)?.toDto()
                if (cached != null) {
                    NetworkResult.Success(cached)
                } else {
                    NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
                }
            }
        }

    override suspend fun createIssue(payload: CreateIssuePayload): NetworkResult<CreateIssueResponseDto> =
        withContext(Dispatchers.IO) {
            try {
                val jsonBody = gson.toJson(payload)
                val request = Request.Builder()
                    .url("${OmniHttpClient.WORK_BASE}/issues")
                    .post(jsonBody.toRequestBody(jsonMediaType))
                    .build()
                val response = client.newCall(request).execute()
                val json = response.body?.string().orEmpty()
                if (response.isSuccessful && json.isNotBlank()) {
                    val data = gson.fromJson(json, CreateIssueResponseDto::class.java)
                    val newEntity = TaskEntity(
                        id = data.id,
                        key = data.key,
                        title = payload.title,
                        description = payload.description,
                        projectId = payload.projectId,
                        priorityId = payload.priorityId,
                        typeId = payload.typeId,
                        assigneeId = payload.assigneeId
                    )
                    taskDao.insert(newEntity)
                    NetworkResult.Success(data)
                } else {
                    NetworkResult.Error(response.code, "خطا در ایجاد تسک جدید: $json")
                }
            } catch (e: Exception) {
                NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
            }
        }

    override suspend fun getIssueTransitions(issueId: String): NetworkResult<List<IssueTransitionDto>> =
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("${OmniHttpClient.WORK_BASE}/issues/$issueId/transitions")
                    .build()
                val response = client.newCall(request).execute()
                val json = response.body?.string().orEmpty()
                if (response.isSuccessful && json.isNotBlank()) {
                    val data: List<IssueTransitionDto> = try {
                        val listType = object : TypeToken<List<IssueTransitionDto>>() {}.type
                        gson.fromJson(json, listType)
                    } catch (e: Exception) {
                        val wrapper = gson.fromJson(json, IssueTransitionsResponseDto::class.java)
                        wrapper?.transitions ?: emptyList()
                    } ?: emptyList()
                    NetworkResult.Success(data)
                } else {
                    NetworkResult.Error(response.code, "خطا در دریافت مراحل تغییر وضعیت تسک")
                }
            } catch (e: Exception) {
                NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
            }
        }

    override suspend fun transitionIssue(issueId: String, transitionId: String): NetworkResult<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val payload = TransitionPayload(transitionId)
                val request = Request.Builder()
                    .url("${OmniHttpClient.WORK_BASE}/issues/$issueId/transitions")
                    .post(gson.toJson(payload).toRequestBody(jsonMediaType))
                    .build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    NetworkResult.Success(true)
                } else {
                    val json = response.body?.string().orEmpty()
                    NetworkResult.Error(response.code, "خطا در انتقال ستون تسک: $json")
                }
            } catch (e: Exception) {
                NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
            }
        }

    override suspend fun getIssueComments(issueId: String): NetworkResult<List<IssueCommentDto>> =
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("${OmniHttpClient.WORK_BASE}/issues/$issueId/comments")
                    .build()
                val response = client.newCall(request).execute()
                val json = response.body?.string().orEmpty()
                if (response.isSuccessful && json.isNotBlank()) {
                    val data: List<IssueCommentDto> = try {
                        val listType = object : TypeToken<List<IssueCommentDto>>() {}.type
                        gson.fromJson(json, listType)
                    } catch (e: Exception) {
                        val wrapper = gson.fromJson(json, IssueCommentsResponseDto::class.java)
                        wrapper?.comments ?: emptyList()
                    } ?: emptyList()
                    NetworkResult.Success(data)
                } else {
                    NetworkResult.Error(response.code, "خطا در دریافت دیدگاه‌ها")
                }
            } catch (e: Exception) {
                NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
            }
        }

    override suspend fun addIssueComment(issueId: String, body: String): NetworkResult<IssueCommentDto> =
        withContext(Dispatchers.IO) {
            try {
                val payload = AddCommentPayload(body)
                val request = Request.Builder()
                    .url("${OmniHttpClient.WORK_BASE}/issues/$issueId/comments")
                    .post(gson.toJson(payload).toRequestBody(jsonMediaType))
                    .build()
                val response = client.newCall(request).execute()
                val json = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    val commentDto = try {
                        if (json.isNotBlank()) gson.fromJson(json, IssueCommentDto::class.java) else null
                    } catch (e: Exception) {
                        null
                    } ?: IssueCommentDto(
                        id = "c_${System.currentTimeMillis()}",
                        body = body,
                        createdAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date())
                    )
                    NetworkResult.Success(commentDto)
                } else {
                    NetworkResult.Error(response.code, "خطا در ثبت دیدگاه: $json")
                }
            } catch (e: Exception) {
                NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
            }
        }

    override suspend fun getComments(issueId: String): NetworkResult<List<IssueCommentDto>> =
        getIssueComments(issueId)

    override suspend fun addComment(issueId: String, body: String): NetworkResult<Unit> =
        when (val res = addIssueComment(issueId, body)) {
            is NetworkResult.Success -> NetworkResult.Success(Unit)
            is NetworkResult.Error -> NetworkResult.Error(res.code, res.message)
            is NetworkResult.Loading -> NetworkResult.Loading
        }

    override suspend fun logWorkTime(
        issueId: String,
        timeSpentMinutes: Int,
        comment: String?
    ): NetworkResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            val payload = LogWorkPayload(timeSpentMinutes = timeSpentMinutes, comment = comment)
            val request = Request.Builder()
                .url("${OmniHttpClient.WORK_BASE}/issues/$issueId/worklogs")
                .post(gson.toJson(payload).toRequestBody(jsonMediaType))
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                NetworkResult.Success(true)
            } else {
                val json = response.body?.string().orEmpty()
                NetworkResult.Error(response.code, "خطا در ثبت کارکرد: $json")
            }
        } catch (e: Exception) {
            NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
        }
    }

    override suspend fun getTests(projectId: String?): NetworkResult<List<TestCaseDto>> =
        withContext(Dispatchers.IO) {
            try {
                val url = if (projectId != null) {
                    "${OmniHttpClient.WORK_BASE}/tests?project=$projectId"
                } else {
                    "${OmniHttpClient.WORK_BASE}/tests"
                }
                val request = Request.Builder()
                    .url(url)
                    .build()
                val response = client.newCall(request).execute()
                val json = response.body?.string().orEmpty()
                if (response.isSuccessful && json.isNotBlank()) {
                    val listType = object : TypeToken<List<TestCaseDto>>() {}.type
                    val data: List<TestCaseDto> = gson.fromJson(json, listType) ?: emptyList()
                    NetworkResult.Success(data)
                } else {
                    NetworkResult.Error(response.code, "خطا در دریافت آزمون‌های کیفی (QA)")
                }
            } catch (e: Exception) {
                NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
            }
        }
}
