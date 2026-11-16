package amirparsa.shk.omnispace.feature.growth.presentation

import androidx.lifecycle.viewModelScope
import amirparsa.shk.omnispace.core.network.SessionManager
import amirparsa.shk.omnispace.core.base.mvi.MviViewModel
import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.growth.data.remote.mapper.toDomainModel
import amirparsa.shk.omnispace.feature.growth.domain.model.LeaveRecord
import amirparsa.shk.omnispace.feature.growth.domain.model.OkrGoal
import amirparsa.shk.omnispace.feature.growth.domain.repository.GrowthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * MVI ViewModel managing Organization & Growth dashboard tabs and sheets.
 * Connects directly to backend HR and administrative services.
 */
@HiltViewModel
class OrgGrowthViewModel @Inject constructor(
    private val growthRepo: GrowthRepository,
    private val sessionManager: SessionManager
) : MviViewModel<OrgGrowthUiState, OrgGrowthIntent, OrgGrowthEffect>(
    initialState = OrgGrowthUiState()
) {

    init {
        setState {
            copy(
                currentUserName = sessionManager.displayName ?: "شما",
                organizationName = sessionManager.organizationName ?: "سازمان",
                userRole = when (sessionManager.orgRole) {
                    "org_admin", "admin" -> "مدیر ارشد سازمان"
                    "member" -> "عضو تیم"
                    else -> sessionManager.orgRole.orEmpty().ifBlank { "عضو تیم" }
                }
            )
        }
        loadHrData()
    }

    private fun loadHrData() {
        viewModelScope.launch {
            setState { copy(isLoading = true, errorMessage = null) }

            val balancesResult = growthRepo.getLeaveBalances()
            val payslipsResult = growthRepo.getPayslips()
            val assetsResult = growthRepo.getAssets()
            val leavesResult = growthRepo.getMyLeaves()
            val goalsResult = growthRepo.getGoals()

            var leaveDays = 0
            if (balancesResult is NetworkResult.Success && balancesResult.data.isNotEmpty()) {
                val annual = balancesResult.data.firstOrNull { it.key == "annual" } ?: balancesResult.data.firstOrNull()
                leaveDays = annual?.balance?.toInt() ?: 0
            }

            val livePayslips = (payslipsResult as? NetworkResult.Success)?.data.orEmpty()
            val liveAssets = (assetsResult as? NetworkResult.Success)?.data.orEmpty()

            val liveLeaves = when (leavesResult) {
                is NetworkResult.Success -> leavesResult.data.map { it.toDomainModel() }
                else -> emptyList()
            }

            val liveGoals = when (goalsResult) {
                is NetworkResult.Success -> goalsResult.data.map { it.toDomainModel() }
                else -> emptyList()
            }

            val netMonthly = livePayslips.firstOrNull()?.netAmount ?: "—"

            val errorMsg = when {
                payslipsResult is NetworkResult.Error -> payslipsResult.message
                assetsResult is NetworkResult.Error -> assetsResult.message
                balancesResult is NetworkResult.Error -> balancesResult.message
                leavesResult is NetworkResult.Error && leavesResult.code != 404 -> leavesResult.message
                goalsResult is NetworkResult.Error && goalsResult.code != 404 -> goalsResult.message
                else -> null
            }

            setState {
                copy(
                    remainingLeaveDays = leaveDays,
                    monthlySalaryNet = netMonthly,
                    rewardPoints = 0,
                    payslips = livePayslips,
                    assets = liveAssets,
                    leaves = liveLeaves,
                    goals = liveGoals,
                    rewardPools = emptyList(),
                    isLoading = false,
                    errorMessage = errorMsg
                )
            }
        }
    }

    override fun processIntent(intent: OrgGrowthIntent) {
        when (intent) {
            is OrgGrowthIntent.SelectSubTab -> setState { copy(selectedSubTab = intent.tab) }
            OrgGrowthIntent.OpenPayslipsArchive -> setState { copy(isPayslipsArchiveOpen = true) }
            OrgGrowthIntent.ClosePayslipsArchive -> setState { copy(isPayslipsArchiveOpen = false) }
            OrgGrowthIntent.OpenPerformanceReview -> setState { copy(isPerformanceReviewOpen = true) }
            OrgGrowthIntent.ClosePerformanceReview -> setState { copy(isPerformanceReviewOpen = false) }
            OrgGrowthIntent.OpenMyAssets -> setState { copy(isMyAssetsOpen = true) }
            OrgGrowthIntent.CloseMyAssets -> setState { copy(isMyAssetsOpen = false) }
            OrgGrowthIntent.OpenRewardsWallet -> setState { copy(isRewardsWalletOpen = true) }
            OrgGrowthIntent.CloseRewardsWallet -> setState { copy(isRewardsWalletOpen = false) }
            OrgGrowthIntent.OpenLeaveRequest -> setState { copy(isLeaveRequestOpen = true) }
            OrgGrowthIntent.CloseLeaveRequest -> setState { copy(isLeaveRequestOpen = false) }
            OrgGrowthIntent.NewLeaveClicked -> sendEffect(OrgGrowthEffect.OpenNewLeaveRequest)
            OrgGrowthIntent.DownloadPayslipClicked -> sendEffect(OrgGrowthEffect.DownloadPayslip)
            OrgGrowthIntent.Refresh -> loadHrData()
        }
    }
}
