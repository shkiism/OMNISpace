package amirparsa.shk.omnispace.feature.growth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import amirparsa.shk.omnispace.core.designsystem.OmniSpaceTheme
import amirparsa.shk.omnispace.core.designsystem.OmniTheme
import amirparsa.shk.omnispace.core.designsystem.OmniThemeMode
import amirparsa.shk.omnispace.core.designsystem.component.OmniShimmerCard
import amirparsa.shk.omnispace.core.designsystem.component.OmniShimmerList
import amirparsa.shk.omnispace.core.utils.DateUtils
import amirparsa.shk.omnispace.feature.growth.domain.model.LeaveRecord
import amirparsa.shk.omnispace.feature.growth.domain.model.OkrGoal
import amirparsa.shk.omnispace.feature.growth.presentation.components.LeaveRequestBottomSheet
import amirparsa.shk.omnispace.feature.growth.presentation.components.MyAssetsBottomSheet
import amirparsa.shk.omnispace.feature.growth.presentation.components.PayslipsArchiveBottomSheet
import amirparsa.shk.omnispace.feature.growth.presentation.components.PerformanceReviewBottomSheet
import amirparsa.shk.omnispace.feature.growth.presentation.components.RewardsWalletBottomSheet

/**
 * Stateful entry composable for Organization & Growth screen following MVI architecture.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrgGrowthScreen(
    viewModel: OrgGrowthViewModel = viewModel(),
    onNewLeaveClick: (() -> Unit)? = null,
    onDownloadPayslipClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                OrgGrowthEffect.OpenNewLeaveRequest -> {
                    if (onNewLeaveClick != null) {
                        onNewLeaveClick()
                    } else {
                        viewModel.onIntent(OrgGrowthIntent.OpenLeaveRequest)
                    }
                }
                OrgGrowthEffect.DownloadPayslip -> onDownloadPayslipClick()
                is OrgGrowthEffect.ShowToast -> { /* Handle Toast */ }
            }
        }
    }

    OrgGrowthContent(
        state = state,
        onIntent = viewModel::onIntent,
        onNewLeaveClick = onNewLeaveClick ?: { viewModel.onIntent(OrgGrowthIntent.OpenLeaveRequest) },
        onDownloadPayslipClick = onDownloadPayslipClick,
        modifier = modifier
    )
}

/**
 * Pure Stateless Composable rendering Organization & Growth screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrgGrowthContent(
    state: OrgGrowthUiState,
    onIntent: (OrgGrowthIntent) -> Unit,
    onNewLeaveClick: () -> Unit = {},
    onDownloadPayslipClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val payslipSheetState = rememberModalBottomSheetState()
    val perfSheetState = rememberModalBottomSheetState()
    val assetsSheetState = rememberModalBottomSheetState()
    val rewardsSheetState = rememberModalBottomSheetState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OmniTheme.colors.canvas)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (state.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.5.dp)),
                color = Color(0xFF7C3AED),
                trackColor = Color(0xFF1E2432)
            )
        }

        if (state.errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF2D1515))
                    .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.errorMessage,
                        fontSize = 11.sp,
                        color = Color(0xFFFCA5A5)
                    )
                    Text(
                        text = "تلاش مجدد",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA78BFA),
                        modifier = Modifier.clickable { onIntent(OrgGrowthIntent.Refresh) }
                    )
                }
            }
        }

        // 1. EMPLOYEE PROFILE HERO CARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF13161F))
                .border(1.dp, Color(0xFF222838), RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val initials = state.currentUserName.trim().split(" ")
                            .filter { it.isNotBlank() }
                            .mapNotNull { it.firstOrNull()?.toString() }
                            .take(2)
                            .joinToString("")
                            .ifBlank { "کاربر" }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF2E1E5B))
                                .border(1.dp, Color(0xFF7C3AED).copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE2E8F0)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = state.currentUserName.ifBlank { "کاربر سازمانی" },
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF231F3D))
                                        .border(1.dp, Color(0xFF7C3AED).copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = state.userRole.ifBlank { "عضو تیم" },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFC4B5FD)
                                    )
                                }
                            }
                            Text(
                                text = state.organizationName.ifBlank { "سازمان" },
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = state.organizationName.ifBlank { "سازمان" },
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }

                    if (state.employeeCode.isNotBlank()) {
                        Text(
                            text = "کد: ${state.employeeCode}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFF1E2432), thickness = 1.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F121A))
                            .border(1.dp, Color(0xFF1D2230), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkHistory,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                        Column {
                            Text(text = "سابقه همکاری", fontSize = 10.sp, color = Color(0xFF64748B))
                            Text(text = "۲ سال و ۴ ماه", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE2E8F0))
                        }
                    }

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F121A))
                            .border(1.dp, Color(0xFF1D2230), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                        Column {
                            Text(text = "واحد سازمانی", fontSize = 10.sp, color = Color(0xFF64748B))
                            Text(text = "مهندسی محصول", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE2E8F0))
                        }
                    }
                }
            }
        }

        // 2. SUB-TAB SWITCHER CHIPS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HrSubTab.entries.forEach { tab ->
                val isSelected = tab == state.selectedSubTab
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) Color(0xFF7C3AED) else Color(0xFF161A24))
                        .border(
                            1.dp,
                            if (isSelected) Color(0xFF7C3AED) else Color(0xFF262C3C),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { onIntent(OrgGrowthIntent.SelectSubTab(tab)) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = tab.titleFa,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else Color(0xFF94A3B8)
                    )
                }
            }
        }

        // 3. CARD 1: PAYROLL & SALARY
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF131620))
                .border(1.dp, Color(0xFF242938), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                val latestPayslip = state.payslips.firstOrNull()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF2E1E5B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                contentDescription = null,
                                tint = Color(0xFFA78BFA),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = if (latestPayslip != null) "فیش حقوقی ماه ${latestPayslip.monthName}" else "فیش حقوقی ماه جاری",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (latestPayslip != null) "اطلاعات رسمی حقوق و دستمزد" else "سامانه مالی پرسنلی",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    if (latestPayslip != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (latestPayslip.isPaid) Color(0xFF063726) else Color(0xFF2E1E5B))
                                .border(
                                    1.dp,
                                    if (latestPayslip.isPaid) Color(0xFF10B981).copy(alpha = 0.5f) else Color(0xFF7C3AED).copy(alpha = 0.5f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (latestPayslip.isPaid) "• تسویه شده" else "• در حال پردازش",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (latestPayslip.isPaid) Color(0xFF34D399) else Color(0xFFC4B5FD)
                            )
                        }
                    }
                }

                if (latestPayslip != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0C0E14))
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "مبلغ خالص پرداختی ماه جاری",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = latestPayslip.netAmount,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF171B26))
                                .padding(vertical = 10.dp, horizontal = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(text = "حقوق پایه", fontSize = 10.sp, color = Color(0xFF94A3B8))
                            Text(text = latestPayslip.baseSalary, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFCBD5E1))
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF171B26))
                                .padding(vertical = 10.dp, horizontal = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(text = "اضافه‌کاری", fontSize = 10.sp, color = Color(0xFF94A3B8))
                            Text(text = latestPayslip.overtimeAmount, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFCBD5E1))
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF171B26))
                                .padding(vertical = 10.dp, horizontal = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(text = "مزایا و بن", fontSize = 10.sp, color = Color(0xFF94A3B8))
                            Text(text = latestPayslip.housingAllowance, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFCBD5E1))
                        }
                    }
                } else if (state.isLoading) {
                    OmniShimmerCard(height = 130.dp)
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0C0E14))
                            .padding(vertical = 24.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "فیش حقوقی ثبت نشده است",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "هنوز فیش حقوقی صادرشده‌ای برای شما در سامانه ثبت نشده است.",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFE2D9FC))
                        .clickable {
                            onIntent(OrgGrowthIntent.OpenPayslipsArchive)
                            if (latestPayslip != null) {
                                onDownloadPayslipClick()
                            }
                        },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = Color(0xFF1E1035),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "مشاهده ریز فیش و آرشیو ماهانه (PDF)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1035)
                    )
                }
            }
        }

        // 4. CARD 2: LEAVE BALANCE & REQUESTS
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF131620))
                .border(1.dp, Color(0xFF242938), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF232838)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = Color(0xFFCBD5E1),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "مانده مرخصی سالانه",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "سال کاری ${DateUtils.getCurrentJalaliDate().year}",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1F2432))
                            .border(1.dp, Color(0xFF333B4E), RoundedCornerShape(8.dp))
                            .clickable { onIntent(OrgGrowthIntent.NewLeaveClicked) }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "ثبت مرخصی",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }

                val remaining = state.remainingLeaveDays
                val totalDays = 26
                val usedDays = (totalDays - remaining).coerceAtLeast(0)
                val leaveProgress = (remaining / totalDays.toFloat()).coerceIn(0f, 1f)
                val leavePercent = (leaveProgress * 100).toInt()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0C0E14))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$remaining روز مانده",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "از کل $totalDays روز کاری سال",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF222838))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(leaveProgress)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0xFFC4B5FD))
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$usedDays روز استفاده شده",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = "$leavePercent٪ مانده فعال",
                                fontSize = 10.sp,
                                color = Color(0xFFA78BFA)
                            )
                        }
                    }
                }

                // 4.2 LEAVE REQUESTS HISTORY & STATUS
                Text(
                    text = "تاریخچه و وضعیت مرخصی‌های ثبت‌شده:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFCBD5E1),
                    modifier = Modifier.padding(top = 4.dp)
                )

                if (state.leaves.isEmpty()) {
                    if (state.isLoading) {
                        OmniShimmerList(count = 2)
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF171B26))
                                .padding(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "هنوز درخواست مرخصی ثبت نشده است.",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.leaves.forEach { leave ->
                            val isApproved = leave.status.equals("Approved", ignoreCase = true) ||
                                    leave.status.contains("تایید", ignoreCase = true)
                            val isRejected = leave.status.equals("Rejected", ignoreCase = true) ||
                                    leave.status.contains("رد", ignoreCase = true)
                            val isPending = !isApproved && !isRejected

                            val badgeBg = when {
                                isApproved -> Color(0xFF063726)
                                isRejected -> Color(0xFF2D1515)
                                else -> Color(0xFF2E240D)
                            }
                            val badgeTextColor = when {
                                isApproved -> Color(0xFF34D399)
                                isRejected -> Color(0xFFFCA5A5)
                                else -> Color(0xFFFBBF24)
                            }
                            val badgeBorderColor = when {
                                isApproved -> Color(0xFF10B981).copy(alpha = 0.4f)
                                isRejected -> Color(0xFFEF4444).copy(alpha = 0.4f)
                                else -> Color(0xFFF59E0B).copy(alpha = 0.4f)
                            }
                            val statusLabel = when {
                                isApproved -> "✓ تایید شده"
                                isRejected -> "✕ رد شده"
                                else -> "⏳ در انتظار بررسی"
                            }
                            val dotColor = when {
                                isApproved -> Color(0xFF10B981)
                                isRejected -> Color(0xFFEF4444)
                                else -> Color(0xFFF59E0B)
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF171B26))
                                    .border(1.dp, Color(0xFF222838), RoundedCornerShape(10.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(dotColor)
                                        )
                                        Text(
                                            text = leave.leaveType,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFE2E8F0)
                                        )
                                    }
                                    Text(
                                        text = "بازه: ${leave.startDate} تا ${leave.endDate} • ${leave.daysCount} روز کاری",
                                        fontSize = 10.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                    Text(
                                        text = "جانشین: ${leave.substituteColleagueName}",
                                        fontSize = 10.sp,
                                        color = Color(0xFF64748B)
                                    )
                                    if (!leave.reason.isNullOrBlank()) {
                                        Text(
                                            text = "توضیحات: ${leave.reason}",
                                            fontSize = 10.sp,
                                            color = Color(0xFF7C8BA1)
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(badgeBg)
                                        .border(1.dp, badgeBorderColor, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = statusLabel,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = badgeTextColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. CARD 3: OKR & PERFORMANCE
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF131620))
                .border(1.dp, Color(0xFF242938), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "عملکرد و اهداف (OKR)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "ارزیابی دوره‌ای عملکرد سازمانی",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF222838))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "دوره جاری",
                            fontSize = 10.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }

                val avgProgress = if (state.goals.isNotEmpty()) {
                    state.goals.map { it.progressPercent }.average().toInt()
                } else 0
                val avgFraction = (avgProgress / 100f).coerceIn(0f, 1f)
                val performanceScore = if (state.goals.isNotEmpty()) {
                    "${String.format(java.util.Locale.US, "%.1f", (avgProgress / 20.0).coerceIn(0.0, 5.0))} از ۵.۰"
                } else "—"
                val (badgeLabel, badgeBgColor, badgeTextColor) = when {
                    state.goals.isEmpty() -> Triple("در انتظار ثبت", Color(0xFF222838), Color(0xFF94A3B8))
                    avgProgress >= 80 -> Triple("Top Performer", Color(0xFF063726), Color(0xFF34D399))
                    avgProgress >= 50 -> Triple("On Track", Color(0xFF1E2838), Color(0xFF60A5FA))
                    else -> Triple("Needs Focus", Color(0xFF3A2018), Color(0xFFF87171))
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0C0E14))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF2E1E5B)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MilitaryTech,
                                        contentDescription = null,
                                        tint = Color(0xFFA78BFA),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(text = "شاخص ارزیابی عملکرد", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                    Text(text = performanceScore, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(badgeBgColor)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = badgeLabel,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeTextColor
                                )
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "میانگین تحقق اهداف کلیدی (Key Results)", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                Text(text = "$avgProgress٪ پیشرفت", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA78BFA))
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0xFF222838))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(avgFraction)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(Color(0xFF7C3AED), Color(0xFFA78BFA))
                                            )
                                        )
                                )
                            }
                        }
                    }
                }

                // 5.1 OKR GOALS: ORGANIZATION & PERSONAL GOALS WITH PROGRESS INDICATORS
                val orgGoals = state.goals.filter { it.isOrganizationGoal }
                val personalGoals = state.goals.filter { !it.isOrganizationGoal }

                if (orgGoals.isEmpty() && personalGoals.isEmpty()) {
                    if (state.isLoading) {
                        OmniShimmerList(count = 2)
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF171B26))
                                .padding(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "اهداف و OKR سازمانی یا فردی ثبت نشده است.",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }

                if (orgGoals.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "اهداف کلیدی سازمانی (Company OKRs)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFCBD5E1)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF0F2922))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${orgGoals.size} هدف سازمانی",
                                fontSize = 10.sp,
                                color = Color(0xFF2DD4BF)
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        orgGoals.forEach { goal ->
                            GoalProgressItem(goal = goal, isOrg = true)
                        }
                    }
                }

                if (personalGoals.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "اهداف عملکرد فردی (Personal OKRs)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFCBD5E1)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF231F3D))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${personalGoals.size} هدف فردی",
                                fontSize = 10.sp,
                                color = Color(0xFFC4B5FD)
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        personalGoals.forEach { goal ->
                            GoalProgressItem(goal = goal, isOrg = false)
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onIntent(OrgGrowthIntent.OpenPerformanceReview) }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "مشاهده کارنامه رشد و بازخورد ۳۶۰ درجه",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFA78BFA)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = Color(0xFFA78BFA),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // 6. CARD 4: REWARDS & ASSETS
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF131620))
                .border(1.dp, Color(0xFF242938), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "باشگاه پاداش و دارایی‌ها",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "امتیازات باشگاه همکاران",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = null,
                        tint = Color(0xFFA78BFA),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0C0E14))
                            .clickable { onIntent(OrgGrowthIntent.OpenRewardsWallet) }
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "کیف پول پاداش سازمانی", fontSize = 10.sp, color = Color(0xFF94A3B8))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "${state.rewardPoints}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = if (state.rewardPoints > 0) "امتیاز فعال در دوره جاری" else "بدون امتیاز در دوره جاری",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (state.rewardPoints > 0) Color(0xFF34D399) else Color(0xFF94A3B8)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0C0E14))
                            .clickable { onIntent(OrgGrowthIntent.OpenRewardsWallet) }
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "استخرهای تسویه پاداش", fontSize = 10.sp, color = Color(0xFF94A3B8))
                        Text(text = "پیگیری تسویه دوره‌ای (/me/payouts)", fontSize = 10.sp, color = Color(0xFFCBD5E1), lineHeight = 14.sp)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF7C3AED)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "مشاهده استخرها ↗",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF171B26))
                        .clickable { onIntent(OrgGrowthIntent.OpenMyAssets) }
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LaptopMac,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "تجهیزات سازمانی تحویل‌شده",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE2E8F0)
                                )
                            }

                            if (state.assets.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF063726))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${state.assets.size} قلم فعال",
                                        fontSize = 9.sp,
                                        color = Color(0xFF34D399)
                                    )
                                }
                            }
                        }

                        if (state.assets.isNotEmpty()) {
                            Text(
                                text = state.assets.joinToString(" • ") { it.title },
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8),
                                lineHeight = 16.sp,
                                maxLines = 2
                            )
                        } else if (state.isLoading) {
                            OmniShimmerCard(height = 24.dp)
                        } else {
                            Text(
                                text = "هیچ تجهیزات سازمانی به نام شما ثبت نشده است.",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(72.dp))
    }

    if (state.isPayslipsArchiveOpen) {
        PayslipsArchiveBottomSheet(
            onDismissRequest = { onIntent(OrgGrowthIntent.ClosePayslipsArchive) },
            sheetState = payslipSheetState,
            payslips = state.payslips
        )
    }

    if (state.isPerformanceReviewOpen) {
        PerformanceReviewBottomSheet(
            targetColleagueName = state.currentUserName.ifBlank { "همکار" },
            cycleName = "ارزیابی دوره‌ای عملکرد",
            onDismissRequest = { onIntent(OrgGrowthIntent.ClosePerformanceReview) },
            sheetState = perfSheetState
        )
    }

    if (state.isMyAssetsOpen) {
        MyAssetsBottomSheet(
            onDismissRequest = { onIntent(OrgGrowthIntent.CloseMyAssets) },
            sheetState = assetsSheetState,
            assets = state.assets
        )
    }

    if (state.isRewardsWalletOpen) {
        RewardsWalletBottomSheet(
            onDismissRequest = { onIntent(OrgGrowthIntent.CloseRewardsWallet) },
            sheetState = rewardsSheetState,
            pools = state.rewardPools
        )
    }

    if (state.isLeaveRequestOpen) {
        val leaveSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        LeaveRequestBottomSheet(
            remainingDays = state.remainingLeaveDays,
            sheetState = leaveSheetState,
            onDismissRequest = { onIntent(OrgGrowthIntent.CloseLeaveRequest) },
            onSubmitSuccess = { _, _, _, _, _ ->
                onIntent(OrgGrowthIntent.Refresh)
            }
        )
    }
}

@Composable
private fun GoalProgressItem(
    goal: OkrGoal,
    isOrg: Boolean
) {
    val progressFraction = (goal.progressPercent / 100f).coerceIn(0f, 1f)
    val gradientColors = if (isOrg) {
        listOf(Color(0xFF0D9488), Color(0xFF14B8A6))
    } else {
        listOf(Color(0xFF7C3AED), Color(0xFFA78BFA))
    }
    val badgeBg = if (isOrg) Color(0xFF0F2922) else Color(0xFF231F3D)
    val badgeTextColor = if (isOrg) Color(0xFF2DD4BF) else Color(0xFFC4B5FD)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF171B26))
            .border(1.dp, Color(0xFF222838), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = goal.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${goal.progressPercent}٪",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeTextColor
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF222838))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressFraction)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Brush.horizontalGradient(gradientColors))
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "مسئول: ${goal.ownerName}",
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                )
                Text(
                    text = if (isOrg) "وزن: ${goal.weight}" else "ضریب ارزیابی: ${goal.weight}",
                    fontSize = 10.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Preview(name = "Org Growth Screen Dark Mode", showBackground = true, backgroundColor = 0xFF0B0C0E)
@Composable
private fun OrgGrowthScreenPreview() {
    OmniSpaceTheme(themeMode = OmniThemeMode.DARK) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            OrgGrowthContent(
                state = OrgGrowthUiState(),
                onIntent = {}
            )
        }
    }
}
