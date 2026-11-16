package amirparsa.shk.omnispace.feature.growth.data.repository

import android.content.Context
import amirparsa.shk.omnispace.core.network.OmniHttpClient
import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.growth.data.remote.dto.AssetDto
import amirparsa.shk.omnispace.feature.growth.data.remote.dto.GoalDto
import amirparsa.shk.omnispace.feature.growth.data.remote.dto.LeaveBalanceDto
import amirparsa.shk.omnispace.feature.growth.data.remote.dto.LeaveRequestDto
import amirparsa.shk.omnispace.feature.growth.data.remote.dto.PayrollMineDto
import amirparsa.shk.omnispace.feature.growth.data.remote.dto.PayslipDto
import amirparsa.shk.omnispace.feature.growth.data.remote.dto.RequestTypeDto
import amirparsa.shk.omnispace.feature.growth.data.remote.dto.SubmitLeaveRequestPayload
import amirparsa.shk.omnispace.feature.growth.data.remote.dto.SubmitRequestPayload
import amirparsa.shk.omnispace.feature.growth.data.remote.mapper.toCompanyAssetItem
import amirparsa.shk.omnispace.feature.growth.data.remote.mapper.toPayslipItem
import amirparsa.shk.omnispace.feature.growth.domain.repository.GrowthRepository
import amirparsa.shk.omnispace.feature.growth.presentation.components.CompanyAssetItem
import amirparsa.shk.omnispace.feature.growth.presentation.components.PayslipItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GrowthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : GrowthRepository {

    private val client = OmniHttpClient.getClient(context)
    private val gson = Gson()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    override suspend fun getLeaveBalances(): NetworkResult<List<LeaveBalanceDto>> =
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("${OmniHttpClient.WORK_BASE}/leave/balances")
                    .build()
                val response = client.newCall(request).execute()
                val json = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    val listType = object : TypeToken<List<LeaveBalanceDto>>() {}.type
                    val data: List<LeaveBalanceDto> = gson.fromJson(json, listType) ?: emptyList()
                    NetworkResult.Success(data)
                } else {
                    NetworkResult.Error(response.code, "خطا در دریافت مانده مرخصی‌ها")
                }
            } catch (e: Exception) {
                NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
            }
        }

    override suspend fun getLeaveRequests(): NetworkResult<List<LeaveRequestDto>> = getMyLeaves()

    override suspend fun getMyLeaves(): NetworkResult<List<LeaveRequestDto>> =
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("${OmniHttpClient.WORK_BASE}/leave/mine")
                    .build()
                val response = client.newCall(request).execute()
                val json = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    val listType = object : TypeToken<List<LeaveRequestDto>>() {}.type
                    val data: List<LeaveRequestDto> = gson.fromJson(json, listType) ?: emptyList()
                    NetworkResult.Success(data)
                } else if (response.code == 404) {
                    val fallbackRequest = Request.Builder()
                        .url("${OmniHttpClient.WORK_BASE}/leave/requests")
                        .build()
                    val fallbackResponse = client.newCall(fallbackRequest).execute()
                    val fallbackJson = fallbackResponse.body?.string().orEmpty()
                    if (fallbackResponse.isSuccessful) {
                        val listType = object : TypeToken<List<LeaveRequestDto>>() {}.type
                        val data: List<LeaveRequestDto> = gson.fromJson(fallbackJson, listType) ?: emptyList()
                        NetworkResult.Success(data)
                    } else {
                        NetworkResult.Error(fallbackResponse.code, "خطا در دریافت درخواست‌های مرخصی")
                    }
                } else {
                    NetworkResult.Error(response.code, "خطا در دریافت درخواست‌های مرخصی")
                }
            } catch (e: Exception) {
                NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
            }
        }

    override suspend fun getGoals(): NetworkResult<List<GoalDto>> =
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("${OmniHttpClient.WORK_BASE}/goals")
                    .build()
                val response = client.newCall(request).execute()
                val json = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    val listType = object : TypeToken<List<GoalDto>>() {}.type
                    val data: List<GoalDto> = gson.fromJson(json, listType) ?: emptyList()
                    NetworkResult.Success(data)
                } else if (response.code == 404) {
                    val fallbackRequest = Request.Builder()
                        .url("${OmniHttpClient.WORK_BASE}/growth/goals")
                        .build()
                    val fallbackResponse = client.newCall(fallbackRequest).execute()
                    val fallbackJson = fallbackResponse.body?.string().orEmpty()
                    if (fallbackResponse.isSuccessful) {
                        val listType = object : TypeToken<List<GoalDto>>() {}.type
                        val data: List<GoalDto> = gson.fromJson(fallbackJson, listType) ?: emptyList()
                        NetworkResult.Success(data)
                    } else {
                        NetworkResult.Error(fallbackResponse.code, "خطا در دریافت اهداف سازمانی و فردی")
                    }
                } else {
                    NetworkResult.Error(response.code, "خطا در دریافت اهداف OKR")
                }
            } catch (e: Exception) {
                NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
            }
        }

    override suspend fun submitLeaveRequest(
        type: String,
        startDate: String,
        endDate: String,
        reason: String,
        substituteId: String?
    ): NetworkResult<Boolean> = submitLeaveRequest(
        SubmitLeaveRequestPayload(
            typeId = type,
            type = type,
            startDate = startDate,
            endDate = endDate,
            reason = reason,
            note = reason,
            substituteId = substituteId,
            standIn = substituteId
        )
    )

    override suspend fun submitLeaveRequest(payload: SubmitLeaveRequestPayload): NetworkResult<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("${OmniHttpClient.WORK_BASE}/leave/requests")
                    .post(gson.toJson(payload).toRequestBody(jsonMediaType))
                    .build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    NetworkResult.Success(true)
                } else {
                    val json = response.body?.string().orEmpty()
                    NetworkResult.Error(response.code, "خطا در ثبت درخواست مرخصی: $json")
                }
            } catch (e: Exception) {
                NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
            }
        }

    override suspend fun getRequestTypes(): NetworkResult<List<RequestTypeDto>> =
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("${OmniHttpClient.WORK_BASE}/requests/types")
                    .build()
                val response = client.newCall(request).execute()
                val json = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    val listType = object : TypeToken<List<RequestTypeDto>>() {}.type
                    val data: List<RequestTypeDto> = gson.fromJson(json, listType) ?: emptyList()
                    NetworkResult.Success(data)
                } else {
                    NetworkResult.Error(response.code, "خطا در دریافت انواع درخواست‌های اداری")
                }
            } catch (e: Exception) {
                NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
            }
        }

    override suspend fun getMyPayroll(): NetworkResult<PayrollMineDto> =
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("${OmniHttpClient.WORK_BASE}/payroll/me")
                    .build()
                val response = client.newCall(request).execute()
                val json = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    val data = gson.fromJson(json, PayrollMineDto::class.java)
                    NetworkResult.Success(data)
                } else if (response.code == 404) {
                    val fallbackRequest = Request.Builder()
                        .url("${OmniHttpClient.WORK_BASE}/payroll/mine")
                        .build()
                    val fallbackResponse = client.newCall(fallbackRequest).execute()
                    val fallbackJson = fallbackResponse.body?.string().orEmpty()
                    if (fallbackResponse.isSuccessful) {
                        val data = gson.fromJson(fallbackJson, PayrollMineDto::class.java)
                        NetworkResult.Success(data)
                    } else {
                        NetworkResult.Error(fallbackResponse.code, "خطا در دریافت احکام و فیش حقوقی")
                    }
                } else {
                    NetworkResult.Error(response.code, "خطا در دریافت احکام و فیش حقوقی")
                }
            } catch (e: Exception) {
                NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
            }
        }

    override suspend fun getMyAssets(): NetworkResult<List<AssetDto>> =
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("${OmniHttpClient.WORK_BASE}/assets")
                    .build()
                val response = client.newCall(request).execute()
                val json = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    val listType = object : TypeToken<List<AssetDto>>() {}.type
                    val data: List<AssetDto> = gson.fromJson(json, listType) ?: emptyList()
                    NetworkResult.Success(data)
                } else {
                    NetworkResult.Error(response.code, "خطا در استعلام تجهیزات سازمانی")
                }
            } catch (e: Exception) {
                NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
            }
        }

    override suspend fun getPayslips(): NetworkResult<List<PayslipItem>> =
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("${OmniHttpClient.WORK_BASE}/payroll/slips")
                    .build()
                val response = client.newCall(request).execute()
                val json = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    val listType = object : TypeToken<List<PayslipDto>>() {}.type
                    val list: List<PayslipDto>? = runCatching { gson.fromJson<List<PayslipDto>>(json, listType) }.getOrNull()
                    if (list != null) {
                        return@withContext NetworkResult.Success(list.map { it.toPayslipItem() })
                    }
                    val payrollDto: PayrollMineDto? = runCatching { gson.fromJson(json, PayrollMineDto::class.java) }.getOrNull()
                    if (payrollDto != null) {
                        return@withContext NetworkResult.Success(payrollDto.payslips.map { it.toPayslipItem() })
                    }
                    NetworkResult.Success(emptyList())
                } else if (response.code == 404) {
                    // Fallback to /payroll/me or /payroll/mine
                    when (val mineResult = getMyPayroll()) {
                        is NetworkResult.Success -> {
                            NetworkResult.Success(mineResult.data.payslips.map { it.toPayslipItem() })
                        }
                        is NetworkResult.Error -> {
                            NetworkResult.Error(mineResult.code, mineResult.message)
                        }
                        is NetworkResult.Loading -> NetworkResult.Loading
                    }
                } else {
                    NetworkResult.Error(response.code, "خطا در دریافت فیش‌های حقوقی")
                }
            } catch (e: Exception) {
                NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
            }
        }

    override suspend fun getAssets(): NetworkResult<List<CompanyAssetItem>> =
        withContext(Dispatchers.IO) {
            when (val assetsResult = getMyAssets()) {
                is NetworkResult.Success -> {
                    val items = assetsResult.data.map { it.toCompanyAssetItem() }
                    NetworkResult.Success(items)
                }
                is NetworkResult.Error -> {
                    NetworkResult.Error(assetsResult.code, assetsResult.message)
                }
                is NetworkResult.Loading -> NetworkResult.Loading
            }
        }

    override suspend fun submitRequest(typeId: String, fields: Map<String, Any>): NetworkResult<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val payload = SubmitRequestPayload(typeId, fields)
                val request = Request.Builder()
                    .url("${OmniHttpClient.WORK_BASE}/requests")
                    .post(gson.toJson(payload).toRequestBody(jsonMediaType))
                    .build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    NetworkResult.Success(Unit)
                } else {
                    val json = response.body?.string().orEmpty()
                    NetworkResult.Error(response.code, "خطا در ارسال درخواست اداری: $json")
                }
            } catch (e: Exception) {
                NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
            }
        }
}
