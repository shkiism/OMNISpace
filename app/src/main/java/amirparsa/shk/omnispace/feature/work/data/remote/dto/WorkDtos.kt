package amirparsa.shk.omnispace.feature.work.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProjectDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("weight")
    val weight: String? = null,
    @SerializedName("lead_id")
    val leadId: String? = null
)

data class ConfigDto(
    @SerializedName("issueTypes")
    val issueTypes: List<IssueTypeDto> = emptyList(),
    @SerializedName("priorities")
    val priorities: List<PriorityDto> = emptyList()
)

data class IssueTypeDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("level")
    val level: Int = 1
)

data class PriorityDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("rank")
    val rank: Int = 1,
    @SerializedName("color")
    val color: String? = null
)

data class WorkflowDto(
    @SerializedName("workflowId")
    val workflowId: String,
    @SerializedName("statuses")
    val statuses: List<WorkflowStatusDto> = emptyList(),
    @SerializedName("transitions")
    val transitions: List<WorkflowTransitionDto> = emptyList()
)

data class WorkflowStatusDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("is_blocked")
    val isBlocked: Boolean = false,
    @SerializedName("position")
    val position: Int = 0
)

data class WorkflowTransitionDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("from_status_id")
    val fromStatusId: String? = null,
    @SerializedName("to_status_id")
    val toStatusId: String
)

data class IssueListResponseDto(
    @SerializedName("items")
    val items: List<IssueDto> = emptyList(),
    @SerializedName("next_cursor")
    val nextCursor: String? = null
)

data class IssueDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("project_id", alternate = ["projectId"])
    val projectId: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("number")
    val number: Int = 0,
    @SerializedName("type_id", alternate = ["typeId"])
    val typeId: String? = null,
    @SerializedName("status_id", alternate = ["statusId"])
    val statusId: String? = null,
    @SerializedName("priority_id", alternate = ["priorityId"])
    val priorityId: String? = null,
    @SerializedName("parent_id", alternate = ["parentId"])
    val parentId: String? = null,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("assignee_id", alternate = ["assigneeId"])
    val assigneeId: String? = null,
    @SerializedName("reporter_id", alternate = ["reporterId"])
    val reporterId: String? = null,
    @SerializedName("sprint_id", alternate = ["sprintId"])
    val sprintId: String? = null,
    @SerializedName("estimate_points", alternate = ["estimatePoints"])
    val estimatePoints: String? = null,
    @SerializedName("due_at", alternate = ["dueAt"])
    val dueAt: String? = null,
    @SerializedName("labels")
    val labels: List<String> = emptyList(),
    @SerializedName("created_at", alternate = ["createdAt"])
    val createdAt: String? = null
)

data class CreateIssuePayload(
    @SerializedName("projectId")
    val projectId: String,
    @SerializedName("typeId")
    val typeId: String,
    @SerializedName("priorityId")
    val priorityId: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("assigneeId")
    val assigneeId: String? = null
)

data class CreateIssueResponseDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("key")
    val key: String
)

data class TransitionPayload(
    @SerializedName("transitionId")
    val transitionId: String
)

data class IssueTransitionDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("to_status_id", alternate = ["toStatusId", "targetStatusId", "to_id"])
    val toStatusId: String? = null,
    @SerializedName("to_status_name", alternate = ["toStatusName", "targetStatusName", "to_name"])
    val toStatusName: String? = null,
    @SerializedName("to_status", alternate = ["toStatus", "targetStatus", "status"])
    val toStatus: WorkflowStatusDto? = null
) {
    val displayName: String
        get() = name.ifBlank { toStatus?.name ?: toStatusName ?: id }

    val resolvedToStatusId: String?
        get() = toStatus?.id ?: toStatusId
}

data class IssueTransitionsResponseDto(
    @SerializedName("transitions", alternate = ["items"])
    val transitions: List<IssueTransitionDto> = emptyList()
)

data class IssueCommentDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("author_id", alternate = ["authorId"])
    val authorId: String? = null,
    @SerializedName("imported_author", alternate = ["importedAuthor"])
    val importedAuthor: String? = null,
    @SerializedName("body")
    val body: String,
    @SerializedName("created_at", alternate = ["createdAt"])
    val createdAt: String? = null
)

data class IssueCommentsResponseDto(
    @SerializedName("comments", alternate = ["items"])
    val comments: List<IssueCommentDto> = emptyList()
)

data class AddCommentPayload(
    @SerializedName("body")
    val body: String
)

data class LogWorkPayload(
    @SerializedName("timeSpentMinutes", alternate = ["time_spent_minutes", "minutes", "timeSpent"])
    val timeSpentMinutes: Int,
    @SerializedName("comment")
    val comment: String? = null
)

data class TestCaseDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("module")
    val module: String? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("stepsSummary", alternate = ["steps_summary", "steps"])
    val stepsSummary: String? = null,
    @SerializedName("executedBy", alternate = ["executed_by"])
    val executedBy: String? = null,
    @SerializedName("executionTime", alternate = ["execution_time"])
    val executionTime: String? = null,
    @SerializedName("errorMessage", alternate = ["error_message"])
    val errorMessage: String? = null,
    @SerializedName("bugIssueKey", alternate = ["bug_issue_key", "bugKey"])
    val bugIssueKey: String? = null
)


