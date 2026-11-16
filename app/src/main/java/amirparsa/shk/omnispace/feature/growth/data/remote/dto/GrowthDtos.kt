package amirparsa.shk.omnispace.feature.growth.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LeaveBalanceDto(
    @SerializedName("typeId")
    val typeId: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("paid")
    val paid: Boolean = true,
    @SerializedName("balance")
    val balance: Double = 0.0,
    @SerializedName("takenThisYear")
    val takenThisYear: Double = 0.0,
    @SerializedName("givenThisYear")
    val givenThisYear: Double = 0.0
)

data class LeaveRequestDto(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("typeId")
    val typeId: String? = null,
    @SerializedName("typeName")
    val typeName: String? = null,
    @SerializedName("leaveType")
    val leaveType: String? = null,
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("startDate")
    val startDate: String? = null,
    @SerializedName("endDate")
    val endDate: String? = null,
    @SerializedName("days")
    val days: Int? = null,
    @SerializedName("daysCount")
    val daysCount: Int? = null,
    @SerializedName("substituteColleagueName")
    val substituteColleagueName: String? = null,
    @SerializedName("standIn")
    val standIn: String? = null,
    @SerializedName("substituteId")
    val substituteId: String? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("note")
    val note: String? = null,
    @SerializedName("reason")
    val reason: String? = null,
    @SerializedName("createdAt")
    val createdAt: String? = null
)

data class SubmitLeaveRequestPayload(
    @SerializedName("typeId")
    val typeId: String? = null,
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("startDate")
    val startDate: String,
    @SerializedName("endDate")
    val endDate: String,
    @SerializedName("reason")
    val reason: String? = null,
    @SerializedName("note")
    val note: String? = null,
    @SerializedName("substituteId")
    val substituteId: String? = null,
    @SerializedName("standIn")
    val standIn: String? = null
)

data class GoalDto(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("title")
    val title: String = "",
    @SerializedName("progress")
    val progress: Double = 0.0,
    @SerializedName("target")
    val target: Double = 100.0,
    @SerializedName("ownerName")
    val ownerName: String? = null,
    @SerializedName("weight")
    val weight: Double = 1.0,
    @SerializedName("category")
    val category: String? = null
)

data class RequestTypeDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String? = null
)

data class PayslipDto(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("month")
    val month: String? = null,
    @SerializedName("year")
    val year: String? = null,
    @SerializedName("monthName")
    val monthName: String? = null,
    @SerializedName("netAmount")
    val netAmount: Long? = null,
    @SerializedName("netAmountFormatted")
    val netAmountFormatted: String? = null,
    @SerializedName("baseSalary")
    val baseSalary: String? = null,
    @SerializedName("housingAllowance")
    val housingAllowance: String? = null,
    @SerializedName("overtimeAmount")
    val overtimeAmount: String? = null,
    @SerializedName("insuranceDeduction")
    val insuranceDeduction: String? = null,
    @SerializedName("taxDeduction")
    val taxDeduction: String? = null,
    @SerializedName("isPaid")
    val isPaid: Boolean = true,
    @SerializedName("issueDate")
    val issueDate: String? = null,
    @SerializedName("status")
    val status: String? = null
)

data class PayrollMineDto(
    @SerializedName("decree")
    val decree: Any? = null,
    @SerializedName("decrees")
    val decrees: List<Any> = emptyList(),
    @SerializedName("payslips")
    val payslips: List<PayslipDto> = emptyList()
)

data class AssetDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("category")
    val category: String? = null,
    @SerializedName("tag")
    val tag: String? = null,
    @SerializedName("assetTag")
    val assetTag: String? = null,
    @SerializedName("serial")
    val serial: String? = null,
    @SerializedName("serialNumber")
    val serialNumber: String? = null,
    @SerializedName("assignedAt")
    val assignedAt: String? = null,
    @SerializedName("handoverDate")
    val handoverDate: String? = null,
    @SerializedName("warrantyStatus")
    val warrantyStatus: String? = null
)

data class SubmitRequestPayload(
    @SerializedName("typeId")
    val typeId: String,
    @SerializedName("fields")
    val fields: Map<String, Any> = emptyMap()
)
