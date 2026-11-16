package amirparsa.shk.omnispace.feature.growth.domain.model

/**
 * Domain entity representing an OKR (Objective and Key Results) Goal.
 */
data class OkrGoal(
    val id: String,
    val title: String,
    val progress: Double,
    val target: Double,
    val progressPercent: Int,
    val ownerName: String,
    val weight: Double,
    val isOrganizationGoal: Boolean,
    val category: String? = null
)
