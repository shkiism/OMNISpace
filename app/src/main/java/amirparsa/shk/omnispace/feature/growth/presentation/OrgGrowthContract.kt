package amirparsa.shk.omnispace.feature.growth.presentation

import amirparsa.shk.omnispace.core.base.mvi.UiEffect
import amirparsa.shk.omnispace.core.base.mvi.UiIntent
import amirparsa.shk.omnispace.core.base.mvi.UiState
import amirparsa.shk.omnispace.feature.growth.domain.model.LeaveRecord
import amirparsa.shk.omnispace.feature.growth.domain.model.OkrGoal
import amirparsa.shk.omnispace.feature.growth.presentation.components.CompanyAssetItem
import amirparsa.shk.omnispace.feature.growth.presentation.components.PayoutPoolItem
import amirparsa.shk.omnispace.feature.growth.presentation.components.PayslipItem

/**
 * UI sub-tabs for Organization & Growth dashboard.
 * Belonging to Presentation Layer (NOT Domain).
 */
enum class HrSubTab(val titleFa: String) {
    PAYROLL_LEAVES("امور اداری و حقوق"),
    PERFORMANCE_GOALS("عملکرد و اهداف"),
    REWARDS_ASSETS("امتیازات و پاداش")
}

/**
 * Immutable UI State for Organization & Growth screen.
 */
data class OrgGrowthUiState(
    val selectedSubTab: HrSubTab = HrSubTab.PAYROLL_LEAVES,
    val isPayslipsArchiveOpen: Boolean = false,
    val isPerformanceReviewOpen: Boolean = false,
    val isMyAssetsOpen: Boolean = false,
    val isRewardsWalletOpen: Boolean = false,
    val isLeaveRequestOpen: Boolean = false,
    val payslips: List<PayslipItem> = emptyList(),
    val assets: List<CompanyAssetItem> = emptyList(),
    val rewardPools: List<PayoutPoolItem> = emptyList(),
    val leaves: List<LeaveRecord> = emptyList(),
    val goals: List<OkrGoal> = emptyList(),
    val remainingLeaveDays: Int = 0,
    val monthlySalaryNet: String = "—",
    val rewardPoints: Int = 0,
    val currentUserName: String = "",
    val organizationName: String = "",
    val userRole: String = "",
    val employeeCode: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : UiState

/**
 * User Intents for OrgGrowth screen.
 */
sealed interface OrgGrowthIntent : UiIntent {
    data class SelectSubTab(val tab: HrSubTab) : OrgGrowthIntent
    data object OpenPayslipsArchive : OrgGrowthIntent
    data object ClosePayslipsArchive : OrgGrowthIntent
    data object OpenPerformanceReview : OrgGrowthIntent
    data object ClosePerformanceReview : OrgGrowthIntent
    data object OpenMyAssets : OrgGrowthIntent
    data object CloseMyAssets : OrgGrowthIntent
    data object OpenRewardsWallet : OrgGrowthIntent
    data object CloseRewardsWallet : OrgGrowthIntent
    data object OpenLeaveRequest : OrgGrowthIntent
    data object CloseLeaveRequest : OrgGrowthIntent
    data object NewLeaveClicked : OrgGrowthIntent
    data object DownloadPayslipClicked : OrgGrowthIntent
    data object Refresh : OrgGrowthIntent
}

/**
 * Side Effects for OrgGrowth screen.
 */
sealed interface OrgGrowthEffect : UiEffect {
    data object OpenNewLeaveRequest : OrgGrowthEffect
    data object DownloadPayslip : OrgGrowthEffect
    data class ShowToast(val message: String) : OrgGrowthEffect
}
