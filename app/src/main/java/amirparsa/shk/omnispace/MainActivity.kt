package amirparsa.shk.omnispace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import amirparsa.shk.omnispace.core.designsystem.OmniSpaceTheme
import amirparsa.shk.omnispace.core.designsystem.OmniTheme
import amirparsa.shk.omnispace.core.designsystem.component.IssuePriority
import amirparsa.shk.omnispace.core.designsystem.component.IssueStatus
import amirparsa.shk.omnispace.core.designsystem.component.OmniAvatar
import amirparsa.shk.omnispace.core.designsystem.component.OmniCard
import amirparsa.shk.omnispace.core.designsystem.component.OmniDestructiveButton
import amirparsa.shk.omnispace.core.designsystem.component.OmniPasswordField
import amirparsa.shk.omnispace.core.designsystem.component.OmniPrimaryButton
import amirparsa.shk.omnispace.core.designsystem.component.OmniSearchBar
import amirparsa.shk.omnispace.core.designsystem.component.OmniSecondaryButton
import amirparsa.shk.omnispace.core.designsystem.component.OmniTextField
import amirparsa.shk.omnispace.core.designsystem.component.PriorityChip
import amirparsa.shk.omnispace.core.designsystem.component.StatusChip
import amirparsa.shk.omnispace.core.utils.DateUtils
import amirparsa.shk.omnispace.core.utils.NumberUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OmniSpaceTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = OmniTheme.colors.canvas
                    ) { innerPadding ->
                        DesignSystemShowcaseScreen(
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DesignSystemShowcaseScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    var emailValue by remember { mutableStateOf("amirparsa.shk@gmail.com") }
    var passwordValue by remember { mutableStateOf("AmirP@rsa2000") }
    var searchQuery by remember { mutableStateOf("") }
    var isButtonLoading by remember { mutableStateOf(false) }

    val todayJalali = remember { DateUtils.getCurrentJalaliDate() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // --- Header Section ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OmniAvatar(name = "Amir Parsa", size = 44.dp)
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "امیرپارسا",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = OmniTheme.colors.textPrimary
                        )
                        Box(
                            modifier = Modifier
                                .background(
                                    OmniTheme.colors.primaryViolet.copy(alpha = 0.15f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Admin",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OmniTheme.colors.primaryViolet
                            )
                        }
                    }
                    Text(
                        text = todayJalali.formatFa(),
                        fontSize = 12.sp,
                        color = OmniTheme.colors.textMuted
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(OmniTheme.colors.elevated, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = OmniTheme.colors.textSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // --- System Status Banner ---
        OmniCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "پایه‌های دیزاین سیستم OmniSpace",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = OmniTheme.colors.textPrimary
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(OmniTheme.colors.statusSuccess, CircleShape)
                        )
                        Text(
                            text = "Phase 0 Active",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OmniTheme.colors.statusSuccess
                        )
                    }
                }
                Text(
                    text = "تمامی کامپوننت‌های پایه و توکن‌های Obsidian Violet پیاده‌سازی شدند و آماده استفاده در صفحات هستند.",
                    fontSize = 13.sp,
                    color = OmniTheme.colors.textSecondary,
                    lineHeight = 18.sp
                )
            }
        }

        // --- SECTION 1: Status & Priority Chips ---
        SectionHeader(number = "۰۱", title = "وضعیت‌ها و اولویت‌ها (Status & Priority Chips)")
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "چرخه حیات کارها:",
                fontSize = 12.sp,
                color = OmniTheme.colors.textMuted
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusChip(status = IssueStatus.BACKLOG)
                StatusChip(status = IssueStatus.TODO)
                StatusChip(status = IssueStatus.IN_PROGRESS)
                StatusChip(status = IssueStatus.QA)
                StatusChip(status = IssueStatus.DONE)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "درجه‌های اولویت:",
                fontSize = 12.sp,
                color = OmniTheme.colors.textMuted
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PriorityChip(priority = IssuePriority.HIGH)
                PriorityChip(priority = IssuePriority.MEDIUM)
                PriorityChip(priority = IssuePriority.LOW)
            }
        }

        HorizontalDivider(color = OmniTheme.colors.borderSubtle)

        // --- SECTION 2: Buttons Matrix ---
        SectionHeader(number = "۰۲", title = "دکمه‌ها (Buttons Matrix - h: 48dp)")
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OmniPrimaryButton(
                text = "دکمه اصلی (Primary Action)",
                isLoading = isButtonLoading,
                onClick = {
                    scope.launch {
                        isButtonLoading = true
                        delay(1500)
                        isButtonLoading = false
                    }
                }
            )

            OmniSecondaryButton(
                text = "دکمه ثانویه (Secondary Action)",
                onClick = {}
            )

            OmniDestructiveButton(
                text = "حذف یا اقدام خطرناک (Destructive Action)",
                onClick = {}
            )
        }

        HorizontalDivider(color = OmniTheme.colors.borderSubtle)

        // --- SECTION 3: Input Controls ---
        SectionHeader(number = "۰۳", title = "ورودی‌ها و فرم‌ها (Input Controls)")
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            OmniTextField(
                value = emailValue,
                onValueChange = { emailValue = it },
                label = "شناسه شرکتی / ایمیل سازمانی",
                badgeText = "تأیید شده",
                leadingIcon = Icons.Default.Email
            )

            OmniPasswordField(
                value = passwordValue,
                onValueChange = { passwordValue = it },
                label = "رمز عبور امن",
                leadingIcon = Icons.Default.Lock,
                forgotPasswordLabel = "فراموشی رمز؟",
                onForgotPasswordClick = {}
            )

            OmniSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )
        }

        // --- Footer Spec Note ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "OmniSpace Android • Jetpack Compose 2.0 • Obsidian Violet",
                fontSize = 11.sp,
                color = OmniTheme.colors.textMuted
            )
        }
    }
}

@Composable
private fun SectionHeader(number: String, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = number,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = OmniTheme.colors.primaryViolet
        )
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = OmniTheme.colors.textSecondary
        )
    }
}
