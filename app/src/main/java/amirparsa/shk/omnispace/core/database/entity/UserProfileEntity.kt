package amirparsa.shk.omnispace.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import amirparsa.shk.omnispace.core.network.model.OrgDetails
import amirparsa.shk.omnispace.core.network.model.UserProfileResponse

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val displayName: String,
    val orgId: String,
    val orgRole: String,
    val permissionsCsv: String = "",
    val orgName: String? = null,
    val orgLocale: String? = null,
    val orgCalendar: String? = null,
    val orgTimezone: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)

fun UserProfileEntity.toResponse(): UserProfileResponse {
    return UserProfileResponse(
        id = id,
        email = email,
        displayName = displayName,
        orgId = orgId,
        orgRole = orgRole,
        permissions = if (permissionsCsv.isBlank()) emptyList() else permissionsCsv.split(","),
        projects = emptyList(),
        org = if (orgName != null) {
            OrgDetails(
                name = orgName,
                locale = orgLocale ?: "fa-IR",
                calendar = orgCalendar ?: "jalali",
                timezone = orgTimezone ?: "Asia/Tehran"
            )
        } else null
    )
}

fun UserProfileResponse.toEntity(): UserProfileEntity {
    return UserProfileEntity(
        id = id,
        email = email,
        displayName = displayName,
        orgId = orgId,
        orgRole = orgRole,
        permissionsCsv = permissions.joinToString(","),
        orgName = org?.name,
        orgLocale = org?.locale,
        orgCalendar = org?.calendar,
        orgTimezone = org?.timezone,
        lastUpdated = System.currentTimeMillis()
    )
}

