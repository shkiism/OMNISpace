package amirparsa.shk.omnispace.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import amirparsa.shk.omnispace.feature.work.data.remote.dto.ProjectDto

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey
    val id: String,
    val key: String,
    val name: String,
    val weight: String? = null,
    val leadId: String? = null
)

fun ProjectEntity.toDto(): ProjectDto {
    return ProjectDto(
        id = id,
        key = key,
        name = name,
        weight = weight,
        leadId = leadId
    )
}

fun ProjectDto.toEntity(): ProjectEntity {
    return ProjectEntity(
        id = id,
        key = key,
        name = name,
        weight = weight,
        leadId = leadId
    )
}

