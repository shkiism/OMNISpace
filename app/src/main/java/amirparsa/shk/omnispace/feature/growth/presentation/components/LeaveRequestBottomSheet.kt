package amirparsa.shk.omnispace.feature.growth.presentation.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import amirparsa.shk.omnispace.core.utils.DateUtils
import amirparsa.shk.omnispace.core.designsystem.OmniSpaceTheme
import amirparsa.shk.omnispace.core.designsystem.OmniTheme
import amirparsa.shk.omnispace.core.designsystem.OmniThemeMode
import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.growth.data.repository.GrowthRepositoryImpl
import amirparsa.shk.omnispace.feature.growth.domain.repository.GrowthRepository
import kotlinx.coroutines.launch

enum class LeaveType(val titleFa: String, val isDeductedFromBalance: Boolean) {
    PAID("استحقاقی (کاری)", true),
    SICK("استعلاجی", false),
    HOURLY("ساعتی", true),
    UNPAID("بدون حقوق", false)
}

data class StandInColleague(
    val id: String,
    val name: String,
    val role: String,
    val initials: String,
    val avatarBg: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveRequestBottomSheet(
    remainingDays: Int = 12,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    colleagues: List<StandInColleague> = emptyList(),
    growthRepository: GrowthRepository? = null,
    onDismissRequest: () -> Unit = {},
    onSubmitSuccess: (LeaveType, String, String, StandInColleague, String) -> Unit = { _, _, _, _, _ -> }
) {
    val context = LocalContext.current
    val repository: GrowthRepository = growthRepository ?: remember(context) { GrowthRepositoryImpl(context.applicationContext) }
    val coroutineScope = rememberCoroutineScope()

    val currentJalali = remember { amirparsa.shk.omnispace.core.utils.DateUtils.getCurrentJalaliDate().formatFa() }
    var selectedType by remember { mutableStateOf(LeaveType.PAID) }
    var startDate by remember { mutableStateOf(currentJalali) }
    var endDate by remember { mutableStateOf(currentJalali) }
    var descriptionText by remember { mutableStateOf("") }
    var customStandInName by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var editingDateType by remember { mutableStateOf<String?>(null) } // "start" or "end"
    var tempDateText by remember { mutableStateOf("") }

    val colleaguesList = colleagues
    var selectedColleague by remember(colleaguesList) { mutableStateOf(colleaguesList.firstOrNull()) }

    if (editingDateType != null) {
        AlertDialog(
            onDismissRequest = { editingDateType = null },
            containerColor = Color(0xFF13161F),
            title = {
                Text(
                    text = if (editingDateType == "start") "ویرایش تاریخ شروع" else "ویرایش تاریخ پایان",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                OutlinedTextField(
                    value = tempDateText,
                    onValueChange = { tempDateText = it },
                    placeholder = { Text("مثال: ${DateUtils.getCurrentJalaliDate().formatFa()}", color = Color(0xFF64748B)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF7C3AED),
                        unfocusedBorderColor = Color(0xFF262C3C)
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (tempDateText.isNotBlank()) {
                        if (editingDateType == "start") startDate = tempDateText.trim()
                        else if (editingDateType == "end") endDate = tempDateText.trim()
                    }
                    editingDateType = null
                }) {
                    Text("تایید", color = Color(0xFFA78BFA), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingDateType = null }) {
                    Text("انصراف", color = Color(0xFF94A3B8))
                }
            }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color(0xFF13161F),
        contentColor = Color.White,
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .navigationBarsPadding()
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // HEADER ROW
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ثبت درخواست مرخصی جدید",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Text(
                                text = "مانده مرخصی استحقاقی: $remainingDays روز کاری",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF10B981)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E2433))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // 1. LEAVE TYPE SELECTION (CHIPS)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "نوع مرخصی:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFCBD5E1)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LeaveType.entries.forEach { type ->
                            val isSelected = type == selectedType
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Color(0xFF7C3AED) else Color(0xFF1A1F2C))
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0xFF7C3AED) else Color(0xFF2B3347),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedType = type }
                                    .padding(horizontal = 14.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = type.titleFa,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }

                // 2. DATE RANGE SELECTION
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "بازه زمانی مرخصی:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFCBD5E1)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Start Date Tile
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF161A26))
                                .border(1.dp, Color(0xFF262C3C), RoundedCornerShape(12.dp))
                                .clickable {
                                    tempDateText = startDate
                                    editingDateType = "start"
                                }
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "از تاریخ (جهت ویرایش کلیک کنید):",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = Color(0xFF9D7BF6),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = startDate,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // End Date Tile
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF161A26))
                                .border(1.dp, Color(0xFF262C3C), RoundedCornerShape(12.dp))
                                .clickable {
                                    tempDateText = endDate
                                    editingDateType = "end"
                                }
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "تا تاریخ (جهت ویرایش کلیک کنید):",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = Color(0xFF06B6D4),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = endDate,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    // Calculation badge
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF7C3AED).copy(alpha = 0.12f))
                            .border(1.dp, Color(0xFF7C3AED).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "مدت زمان مرخصی: ۳ روز کاری (۲۴ ساعت) • کسر از سهمیه استحقاقی",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFC4B5FD)
                        )
                    }
                }

                // 3. STAND-IN COLLEAGUE (تعیین همکار جانشین)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "همکار جانشین (Handover Person):",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFCBD5E1)
                        )
                        Text(
                            text = "الزامی",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF59E0B)
                        )
                    }

                    if (colleaguesList.isEmpty()) {
                        OutlinedTextField(
                            value = customStandInName,
                            onValueChange = { customStandInName = it },
                            placeholder = {
                                Text(
                                    text = "نام و نام خانوادگی همکار جانشین...",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF161A24),
                                unfocusedContainerColor = Color(0xFF161A24),
                                focusedBorderColor = Color(0xFF7C3AED),
                                unfocusedBorderColor = Color(0xFF262C3C),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else {
                        colleaguesList.forEach { colleague ->
                            val isSelected = colleague.id == selectedColleague?.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Color(0xFF1E1D32) else Color(0xFF161A24))
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0xFF7C3AED) else Color(0xFF262C3C),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedColleague = colleague }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(colleague.avatarBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = colleague.initials,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = colleague.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = colleague.role,
                                            fontSize = 11.sp,
                                            color = Color(0xFF94A3B8)
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF10B981).copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Color(0xFF10B981),
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                text = "جانشین منتخب",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF10B981)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. DESCRIPTION & NOTES
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "توضیحات و علت مرخصی (اختیاری):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFCBD5E1)
                    )
                    OutlinedTextField(
                        value = descriptionText,
                        onValueChange = { descriptionText = it },
                        placeholder = {
                            Text(
                                text = "علت یا نکات مهم جهت هماهنگی کارها با جانشین...",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(88.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF161A24),
                            unfocusedContainerColor = Color(0xFF161A24),
                            focusedBorderColor = Color(0xFF7C3AED),
                            unfocusedBorderColor = Color(0xFF262C3C),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                if (errorMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF2D1515))
                            .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            fontSize = 11.sp,
                            color = Color(0xFFFCA5A5)
                        )
                    }
                }

                // 5. SUBMIT ACTION BUTTON
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSubmitting) Color(0xFF5B21B6) else Color(0xFF7C3AED))
                            .clickable(enabled = !isSubmitting) {
                                val sDate = startDate.trim()
                                val eDate = endDate.trim()
                                val desc = descriptionText.trim()

                                if (sDate.isBlank()) {
                                    val err = "لطفاً تاریخ شروع مرخصی را مشخص کنید."
                                    errorMessage = err
                                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                    return@clickable
                                }
                                if (eDate.isBlank()) {
                                    val err = "لطفاً تاریخ پایان مرخصی را مشخص کنید."
                                    errorMessage = err
                                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                    return@clickable
                                }
                                val finalColleague = selectedColleague ?: if (customStandInName.isNotBlank()) {
                                    StandInColleague(
                                        id = "custom",
                                        name = customStandInName.trim(),
                                        role = "همکار جانشین",
                                        initials = customStandInName.take(2),
                                        avatarBg = Color(0xFF6D28D9)
                                    )
                                } else null

                                if (finalColleague == null || finalColleague.name.isBlank()) {
                                    val err = "لطفاً همکار جانشین را مشخص کنید."
                                    errorMessage = err
                                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                    return@clickable
                                }

                                errorMessage = null
                                isSubmitting = true
                                coroutineScope.launch {
                                    val result = repository.submitLeaveRequest(
                                        type = selectedType.titleFa,
                                        startDate = sDate,
                                        endDate = eDate,
                                        reason = desc,
                                        substituteId = finalColleague.id
                                    )
                                    isSubmitting = false
                                    when (result) {
                                        is NetworkResult.Success -> {
                                            Toast.makeText(
                                                context,
                                                "درخواست مرخصی با موفقیت در سیستم ثبت شد.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            onSubmitSuccess(
                                                selectedType,
                                                sDate,
                                                eDate,
                                                finalColleague,
                                                desc
                                            )
                                            onDismissRequest()
                                        }
                                        is NetworkResult.Error -> {
                                            val msg = result.message.ifBlank { "خطا در ثبت درخواست مرخصی" }
                                            errorMessage = msg
                                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                        }
                                        is NetworkResult.Loading -> {}
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSubmitting) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "در حال ارسال...",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        } else {
                            Text(
                                text = "ارسال درخواست جهت تایید مدیر فنی",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF1E2433))
                            .clickable(enabled = !isSubmitting) { onDismissRequest() }
                            .padding(horizontal = 18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "انصراف",
                            fontSize = 13.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }
    }
}

