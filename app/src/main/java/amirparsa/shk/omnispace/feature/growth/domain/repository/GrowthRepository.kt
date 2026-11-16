package amirparsa.shk.omnispace.feature.growth.domain.repository

import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.growth.data.remote.dto.AssetDto
import amirparsa.shk.omnispace.feature.growth.data.remote.dto.GoalDto
import amirparsa.shk.omnispace.feature.growth.data.remote.dto.LeaveBalanceDto
import amirparsa.shk.omnispace.feature.growth.data.remote.dto.LeaveRequestDto
import amirparsa.shk.omnispace.feature.growth.data.remote.dto.PayrollMineDto
import amirparsa.shk.omnispace.feature.growth.data.remote.dto.RequestTypeDto
import amirparsa.shk.omnispace.feature.growth.data.remote.dto.SubmitLeaveRequestPayload
import amirparsa.shk.omnispace.feature.growth.presentation.components.CompanyAssetItem
import amirparsa.shk.omnispace.feature.growth.presentation.components.PayslipItem

interface GrowthRepository {
    suspend fun getLeaveBalances(): NetworkResult<List<LeaveBalanceDto>>
    suspend fun getLeaveRequests(): NetworkResult<List<LeaveRequestDto>>
    suspend fun getMyLeaves(): NetworkResult<List<LeaveRequestDto>>
    suspend fun getGoals(): NetworkResult<List<GoalDto>>
    suspend fun submitLeaveRequest(
        type: String,
        startDate: String,
        endDate: String,
        reason: String,
        substituteId: String? = null
    ): NetworkResult<Boolean>
    suspend fun submitLeaveRequest(payload: SubmitLeaveRequestPayload): NetworkResult<Boolean>
    suspend fun getRequestTypes(): NetworkResult<List<RequestTypeDto>>
    suspend fun getMyPayroll(): NetworkResult<PayrollMineDto>
    suspend fun getMyAssets(): NetworkResult<List<AssetDto>>
    suspend fun getPayslips(): NetworkResult<List<PayslipItem>>
    suspend fun getAssets(): NetworkResult<List<CompanyAssetItem>>
    suspend fun submitRequest(typeId: String, fields: Map<String, Any>): NetworkResult<Unit>
}
