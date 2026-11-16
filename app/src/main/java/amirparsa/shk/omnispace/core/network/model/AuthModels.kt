package amirparsa.shk.omnispace.core.network.model

import com.google.gson.annotations.SerializedName

data class AuthSessionResponse(
    @SerializedName("authenticated")
    val authenticated: Boolean,
    @SerializedName("userId")
    val userId: String? = null,
    @SerializedName("orgId")
    val orgId: String? = null,
    @SerializedName("csrfToken")
    val csrfToken: String? = null
)

data class UserProfileResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("orgId")
    val orgId: String,
    @SerializedName("orgRole")
    val orgRole: String,
    @SerializedName("permissions")
    val permissions: List<String> = emptyList(),
    @SerializedName("projects")
    val projects: List<ProjectRoleItem> = emptyList(),
    @SerializedName("org")
    val org: OrgDetails? = null
)

data class ProjectRoleItem(
    @SerializedName("projectId")
    val projectId: String,
    @SerializedName("role")
    val role: String
)

data class OrgDetails(
    @SerializedName("name")
    val name: String,
    @SerializedName("locale")
    val locale: String,
    @SerializedName("calendar")
    val calendar: String,
    @SerializedName("timezone")
    val timezone: String
)

