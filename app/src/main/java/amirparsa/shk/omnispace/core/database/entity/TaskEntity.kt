package amirparsa.shk.omnispace.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import amirparsa.shk.omnispace.feature.work.data.remote.dto.IssueDto

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val key: String,
    val title: String,
    val description: String? = null,
    val projectId: String,
    val statusId: String? = null,
    val priorityId: String? = null,
    val typeId: String? = null,
    val assigneeId: String? = null,
    val reporterId: String? = null,
    val dueAt: String? = null,
    val createdAt: String? = null
)

fun TaskEntity.toDto(): IssueDto {
    return IssueDto(
        id = id,
        key = key,
        title = title,
        description = description,
        projectId = projectId,
        statusId = statusId,
        priorityId = priorityId,
        typeId = typeId,
        assigneeId = assigneeId,
        reporterId = reporterId,
        dueAt = dueAt,
        createdAt = createdAt
    )
}

fun IssueDto.toEntity(fallbackProjectId: String = ""): TaskEntity {
    return TaskEntity(
        id = id,
        key = key,
        title = title,
        description = description,
        projectId = if (projectId.isNotBlank()) projectId else fallbackProjectId,
        statusId = statusId,
        priorityId = priorityId,
        typeId = typeId,
        assigneeId = assigneeId,
        reporterId = reporterId,
        dueAt = dueAt,
        createdAt = createdAt
    )
}
