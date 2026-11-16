package amirparsa.shk.omnispace.feature.growth.data.remote.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Tv
import amirparsa.shk.omnispace.feature.growth.data.remote.dto.AssetDto
import amirparsa.shk.omnispace.feature.growth.data.remote.dto.GoalDto
import amirparsa.shk.omnispace.feature.growth.data.remote.dto.LeaveBalanceDto
import amirparsa.shk.omnispace.feature.growth.data.remote.dto.LeaveRequestDto
import amirparsa.shk.omnispace.feature.growth.data.remote.dto.PayslipDto
import amirparsa.shk.omnispace.feature.growth.domain.model.LeaveRecord
import amirparsa.shk.omnispace.feature.growth.domain.model.OkrGoal
import amirparsa.shk.omnispace.feature.growth.presentation.components.CompanyAssetItem
import amirparsa.shk.omnispace.feature.growth.presentation.components.PayslipItem

fun LeaveBalanceDto.toDomainModel(): LeaveRecord {
    return LeaveRecord(
        id = typeId,
        leaveType = name,
        startDate = "-",
        endDate = "-",
        daysCount = takenThisYear.toInt(),
        substituteColleagueName = "-",
        status = if (paid) "با حقوق" else "بدون حقوق"
    )
}

fun LeaveRequestDto.toDomainModel(): LeaveRecord {
    val rawStatus = status.orEmpty()
    val normalizedStatus = when {
        rawStatus.contains("approve", ignoreCase = true) || rawStatus.contains("تایید", ignoreCase = true) -> "Approved"
        rawStatus.contains("reject", ignoreCase = true) || rawStatus.contains("رد", ignoreCase = true) -> "Rejected"
        rawStatus.contains("pend", ignoreCase = true) || rawStatus.contains("انتظار", ignoreCase = true) -> "Pending"
        else -> rawStatus.ifBlank { "Pending" }
    }

    return LeaveRecord(
        id = id.orEmpty(),
        leaveType = typeName ?: leaveType ?: type ?: "مرخصی استحقاقی",
        startDate = startDate ?: "-",
        endDate = endDate ?: "-",
        daysCount = daysCount ?: days ?: 1,
        substituteColleagueName = substituteColleagueName ?: standIn ?: "-",
        status = normalizedStatus,
        reason = reason ?: note
    )
}

fun GoalDto.toDomainModel(): OkrGoal {
    val progressPct = if (target > 0) ((progress / target) * 100).coerceIn(0.0, 100.0).toInt() else 0
    val isOrg = category?.equals("ORGANIZATION", ignoreCase = true) == true ||
            category?.contains("سازمان", ignoreCase = true) == true ||
            ownerName.isNullOrBlank() ||
            ownerName.contains("سازمان", ignoreCase = true) ||
            ownerName.contains("تیم", ignoreCase = true)

    return OkrGoal(
        id = id,
        title = title,
        progress = progress,
        target = target,
        progressPercent = progressPct,
        ownerName = ownerName ?: if (isOrg) "" else "",
        weight = weight,
        isOrganizationGoal = isOrg,
        category = category
    )
}

fun PayslipDto.toPayslipItem(): PayslipItem {
    val monthTitle = monthName ?: if (month != null && year != null) "$month $year" else "ماه جاری"
    val net = netAmountFormatted ?: (netAmount?.let { "$it تومان" } ?: "۰ تومان")
    return PayslipItem(
        monthName = monthTitle,
        netAmount = net,
        isPaid = isPaid,
        baseSalary = baseSalary ?: "—",
        housingAllowance = housingAllowance ?: "—",
        overtimeAmount = overtimeAmount ?: "—",
        insuranceDeduction = insuranceDeduction ?: "—",
        taxDeduction = taxDeduction ?: "—",
        issueDate = issueDate ?: "—"
    )
}

fun AssetDto.toCompanyAssetItem(): CompanyAssetItem {
    val displayTitle = title ?: name ?: "دارایی سازمانی"
    val displayCategory = category ?: "تجهیزات کاری"
    val displayTag = assetTag ?: tag ?: "AST-${id.takeLast(4)}"
    val displaySerial = serialNumber ?: serial ?: "SN-${id.take(8)}"
    val displayHandover = handoverDate ?: assignedAt ?: "—"
    val displayWarranty = warrantyStatus ?: "فعال"

    val icon = when {
        displayCategory.contains("کامپیوتر") || displayCategory.contains("لپ‌تاپ") || displayTitle.contains("MacBook", ignoreCase = true) -> Icons.Default.Computer
        displayCategory.contains("نمایشگر") || displayCategory.contains("مانیتور") || displayTitle.contains("Dell", ignoreCase = true) -> Icons.Default.Tv
        displayCategory.contains("امنیت") || displayCategory.contains("توکن") || displayTitle.contains("YubiKey", ignoreCase = true) -> Icons.Default.Key
        else -> Icons.Default.PhoneAndroid
    }

    return CompanyAssetItem(
        id = id,
        title = displayTitle,
        category = displayCategory,
        assetTag = displayTag,
        serialNumber = displaySerial,
        handoverDate = displayHandover,
        warrantyStatus = displayWarranty,
        icon = icon
    )
}
