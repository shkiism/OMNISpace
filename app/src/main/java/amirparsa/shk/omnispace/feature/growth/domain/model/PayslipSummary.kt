package amirparsa.shk.omnispace.feature.growth.domain.model

/**
 * Domain entity representing an employee's monthly payslip summary.
 */
data class PayslipSummary(
    val month: String,
    val year: String,
    val netSalary: String,
    val deductions: String,
    val isPaid: Boolean
)

