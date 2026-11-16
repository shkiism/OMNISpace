package amirparsa.shk.omnispace.feature.growth.domain.model

/**
 * Domain entity representing an employee's leave record.
 */
data class LeaveRecord(
    val id: String,
    val leaveType: String,
    val startDate: String,
    val endDate: String,
    val daysCount: Int,
    val substituteColleagueName: String,
    val status: String,
    val reason: String? = null
)

