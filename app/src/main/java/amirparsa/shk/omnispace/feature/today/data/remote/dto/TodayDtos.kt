package amirparsa.shk.omnispace.feature.today.data.remote.dto

import amirparsa.shk.omnispace.feature.work.data.remote.dto.IssueDto
import com.google.gson.annotations.SerializedName

data class FocusStatusDto(
    @SerializedName("active")
    val active: FocusSessionDto? = null,
    @SerializedName("recent")
    val recent: List<FocusSessionDto> = emptyList()
)

data class FocusSessionDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("taskId", alternate = ["task_id"])
    val taskId: String? = null,
    @SerializedName("startedAt", alternate = ["started_at"])
    val startedAt: String? = null,
    @SerializedName("endsAt", alternate = ["ends_at"])
    val endsAt: String? = null,
    @SerializedName("minutes")
    val minutes: Int? = null
)

data class HabitDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("schedule")
    val schedule: String? = "daily",
    @SerializedName("streak")
    val streak: Int = 0,
    @SerializedName("doneToday")
    val doneToday: Boolean = false
)

data class NoteDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String? = null,
    @SerializedName("body")
    val body: String? = null,
    @SerializedName("pinned", alternate = ["isPinned", "is_pinned"])
    val pinned: Boolean = false,
    @SerializedName("created_at", alternate = ["createdAt"])
    val createdAt: String? = null,
    @SerializedName("updated_at", alternate = ["updatedAt"])
    val updatedAt: String? = null
)

data class CreateNotePayload(
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("pinned")
    val pinned: Boolean = false
)

data class UpdateNotePayload(
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("pinned")
    val pinned: Boolean = false
)

data class MeetingDto(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("topic")
    val topic: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("startTime")
    val startTime: String? = null,
    @SerializedName("endTime")
    val endTime: String? = null,
    @SerializedName("platform")
    val platform: String? = null,
    @SerializedName("meetUrl")
    val meetUrl: String? = null,
    @SerializedName("roomName")
    val roomName: String? = null,
    @SerializedName("attendees")
    val attendees: List<String> = emptyList()
)

data class SyncOpDto(
    @SerializedName("op_id", alternate = ["opId"])
    val opId: String,
    @SerializedName("table_name", alternate = ["tableName", "table"])
    val tableName: String,
    @SerializedName("record_id", alternate = ["recordId"])
    val recordId: String,
    @SerializedName("base_version", alternate = ["baseVersion"])
    val baseVersion: Long = 0L,
    @SerializedName("payload", alternate = ["payload_json", "payloadJson"])
    val payloadJson: String,
    @SerializedName("created_at", alternate = ["createdAt"])
    val createdAt: Long = System.currentTimeMillis()
)

data class SyncPushPayload(
    @SerializedName("client_id")
    val clientId: String = "android_app",
    @SerializedName("ops")
    val ops: List<SyncOpDto> = emptyList()
)

data class SyncPushResponse(
    @SerializedName("applied_count", alternate = ["appliedCount", "applied_ops", "pushed"])
    val appliedCount: Int? = null,
    @SerializedName("applied_ids", alternate = ["appliedIds"])
    val appliedIds: List<String>? = null,
    @SerializedName("status")
    val status: String? = null
)

data class SyncPullResponse(
    @SerializedName("cursor")
    val cursor: Long = 0L,
    @SerializedName("has_more", alternate = ["hasMore"])
    val hasMore: Boolean = false,
    @SerializedName("notes")
    val notes: List<NoteDto> = emptyList(),
    @SerializedName("tasks")
    val tasks: List<IssueDto> = emptyList(),
    @SerializedName("habits")
    val habits: List<HabitDto> = emptyList(),
    @SerializedName("status")
    val status: String? = null
)

data class SyncResultDto(
    @SerializedName("cursor")
    val cursor: Long = 0L,
    @SerializedName("applied_count", alternate = ["appliedCount", "appliedOpsCount"])
    val appliedCount: Int = 0,
    @SerializedName("pulled_notes_count", alternate = ["pulledNotesCount"])
    val pulledNotesCount: Int = 0,
    @SerializedName("pulled_tasks_count", alternate = ["pulledTasksCount"])
    val pulledTasksCount: Int = 0,
    @SerializedName("status")
    val status: String = "synced",
    @SerializedName("message")
    val message: String? = null
)



