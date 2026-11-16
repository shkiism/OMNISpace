package amirparsa.shk.omnispace.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import amirparsa.shk.omnispace.feature.connect.data.remote.dto.PersonDto

@Entity(tableName = "people")
data class PersonEntity(
    @PrimaryKey
    val id: String,
    val displayName: String,
    val email: String? = null,
    val orgRole: String? = null,
    val handle: String? = null,
    val avatarV: Int? = null
)

fun PersonEntity.toDto(): PersonDto {
    return PersonDto(
        id = id,
        displayName = displayName,
        email = email,
        orgRole = orgRole,
        handle = handle,
        avatarV = avatarV
    )
}

fun PersonDto.toEntity(): PersonEntity {
    return PersonEntity(
        id = id,
        displayName = displayName,
        email = email,
        orgRole = orgRole,
        handle = handle,
        avatarV = avatarV
    )
}
