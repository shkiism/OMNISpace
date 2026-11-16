package amirparsa.shk.omnispace.feature.people.presentation

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import amirparsa.shk.omnispace.core.designsystem.DarkBorderSubtle
import amirparsa.shk.omnispace.core.designsystem.DarkCard
import amirparsa.shk.omnispace.core.designsystem.DarkElevated
import amirparsa.shk.omnispace.core.designsystem.DarkPrimaryViolet
import amirparsa.shk.omnispace.core.designsystem.DarkStatusInfo
import amirparsa.shk.omnispace.core.designsystem.DarkStatusSuccess
import amirparsa.shk.omnispace.core.designsystem.DarkTextMuted
import amirparsa.shk.omnispace.core.designsystem.DarkTextSecondary
import amirparsa.shk.omnispace.core.designsystem.OmniTheme

import amirparsa.shk.omnispace.core.designsystem.component.OmniShimmerCard
import amirparsa.shk.omnispace.core.designsystem.component.omniShimmer
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Stateful entry composable for Colleague Profile screen following MVI architecture.
 */
@Composable
fun ColleagueProfileScreen(
    colleagueName: String = "",
    jobTitle: String = "",
    department: String = "",
    employeeCode: String = "",
    isOnline: Boolean = false,
    email: String = "",
    onBackClick: () -> Unit = {},
    onStartChat: (colleagueName: String) -> Unit = {},
    onStartVideoCall: (colleagueName: String) -> Unit = {},
    onOpenTask: (issueKey: String) -> Unit = {},
    viewModel: ColleagueProfileViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(colleagueName, jobTitle, department, employeeCode, isOnline, email) {
        viewModel.initData(
            colleagueName = colleagueName,
            jobTitle = jobTitle,
            department = department,
            employeeCode = employeeCode,
            isOnline = isOnline,
            email = email
        )
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ColleagueProfileEffect.NavigateBack -> onBackClick()
                is ColleagueProfileEffect.StartChat -> onStartChat(effect.colleagueName)
                is ColleagueProfileEffect.StartVideoCall -> onStartVideoCall(effect.colleagueName)
                is ColleagueProfileEffect.OpenTask -> onOpenTask(effect.issueKey)
                is ColleagueProfileEffect.SendEmail -> {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:${effect.email}")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        Toast.makeText(context, "آدرس ایمیل: ${effect.email}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    ColleagueProfileContent(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier
    )
}

/**
 * Stateless UI content composable for Colleague Profile.
 */
@Composable
fun ColleagueProfileContent(
    state: ColleagueProfileUiState,
    onIntent: (ColleagueProfileIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(OmniTheme.colors.canvas)
        ) {
            // Header Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCard)
                    .border(0.5.dp, DarkBorderSubtle)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { onIntent(ColleagueProfileIntent.BackClicked) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "بازگشت",
                        tint = Color.White
                    )
                }

                Text(
                    text = "پروفایل سازمانی همکار",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.width(40.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (state.isLoading) {
                    OmniShimmerCard(height = 220.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    OmniShimmerCard(height = 140.dp)
                } else {
                // Profile Avatar Card
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(86.dp)
                            .clip(CircleShape)
                            .background(DarkPrimaryViolet.copy(alpha = 0.2f))
                            .border(2.dp, DarkPrimaryViolet, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.colleagueName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString(""),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkPrimaryViolet
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(if (state.isOnline) DarkStatusSuccess else Color(0xFF64748B))
                            .border(2.dp, DarkElevated, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = state.colleagueName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = state.jobTitle, fontSize = 13.sp, color = DarkTextSecondary)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "${state.department} • ${state.employeeCode}", fontSize = 11.sp, color = DarkTextMuted)

                Spacer(modifier = Modifier.height(18.dp))

                // Immediate Action Buttons (Chat, VideoCall, Email)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onIntent(ColleagueProfileIntent.StartChatClicked) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkPrimaryViolet),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ChatBubbleOutline, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "ارسال پیام", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onIntent(ColleagueProfileIntent.StartVideoCallClicked) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkStatusSuccess),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Videocam, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "تماس ویدیویی", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Surface(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onIntent(ColleagueProfileIntent.SendEmailClicked) },
                        color = DarkElevated,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Email, contentDescription = "ایمیل", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Segmented Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkCard, RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onIntent(ColleagueProfileIntent.SelectTab(0)) },
                        color = if (state.selectedTab == 0) DarkPrimaryViolet else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "مهارت‌ها و مشخصات",
                                color = if (state.selectedTab == 0) Color.Black else DarkTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onIntent(ColleagueProfileIntent.SelectTab(1)) },
                        color = if (state.selectedTab == 1) DarkPrimaryViolet else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "کارهای مشترک (${state.sharedTasks.size})",
                                color = if (state.selectedTab == 1) Color.Black else DarkTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tab Content
                if (state.selectedTab == 0) {
                    // Skills Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = "مهارت‌های تخصصی تاییدشده", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(10.dp))
                            if (state.skills.isEmpty()) {
                                Text(
                                    text = "مهارتی ثبت نشده است",
                                    fontSize = 12.sp,
                                    color = DarkTextMuted
                                )
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    state.skills.take(3).forEach { skill ->
                                        Surface(
                                            color = DarkElevated,
                                            shape = RoundedCornerShape(8.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle)
                                        ) {
                                            Text(text = skill, color = DarkPrimaryViolet, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                        }
                                    }
                                }
                                if (state.skills.size > 3) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        state.skills.drop(3).forEach { skill ->
                                            Surface(
                                                color = DarkElevated,
                                                shape = RoundedCornerShape(8.dp),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle)
                                            ) {
                                                Text(text = skill, color = DarkPrimaryViolet, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(text = "اطلاعات سازمانی", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "• دپارتمان: ${state.department.ifBlank { "سازمان" }}", fontSize = 12.sp, color = DarkTextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "• کد پرسنلی: ${state.employeeCode.ifBlank { "ثبت نشده" }}", fontSize = 12.sp, color = DarkTextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "• وضعیت حضور: ${if (state.isOnline) "آنلاین در سامانه" else "آفلاین"}", fontSize = 12.sp, color = DarkTextSecondary)
                        }
                    }
                } else {
                    // Shared Tasks List
                    if (state.sharedTasks.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkCard),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "هیچ تسک یا کار مشترکی با این همکار ثبت نشده است",
                                    color = DarkTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            state.sharedTasks.forEach { task ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onIntent(ColleagueProfileIntent.OpenTaskClicked(task.key)) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Surface(color = DarkPrimaryViolet.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                                                Text(text = task.key, color = DarkPrimaryViolet, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = task.title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                        Surface(
                                            color = if (task.status == "تکمیل شده") DarkStatusSuccess.copy(alpha = 0.15f) else DarkStatusInfo.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = task.status,
                                                color = if (task.status == "تکمیل شده") DarkStatusSuccess else DarkStatusInfo,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                }
            }
        }
    }
}

