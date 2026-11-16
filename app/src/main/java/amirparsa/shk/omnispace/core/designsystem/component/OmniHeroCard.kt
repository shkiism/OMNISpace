package amirparsa.shk.omnispace.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import amirparsa.shk.omnispace.core.designsystem.OmniSpaceTheme
import amirparsa.shk.omnispace.core.designsystem.OmniTheme
import amirparsa.shk.omnispace.core.designsystem.OmniThemeMode

enum class WorkType {
    ON_SITE,
    REMOTE
}

@Composable
fun OmniHeroCard(
    isCheckedIn: Boolean,
    checkInTimeStr: String? = null,
    elapsedTimeStr: String? = null,
    locationName: String = "شعبه مرکزی",
    completionPercentage: Int = 0,
    selectedWorkType: WorkType = WorkType.ON_SITE,
    onWorkTypeChange: (WorkType) -> Unit = {},
    onCheckInClick: () -> Unit = {},
    onCheckOutClick: () -> Unit = {},
    onBreakClick: () -> Unit = {},
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (isLoading) {
        OmniShimmerCard(modifier = modifier, height = 180.dp)
        return
    }

    val safeCheckIn = checkInTimeStr ?: "--:--"
    val safeElapsed = elapsedTimeStr ?: "ثبت نشده"

    val statusColor by animateColorAsState(
        targetValue = if (isCheckedIn) Color(0xFF10B981) else Color(0xFF94A3B8),
        label = "statusColor"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(OmniTheme.colors.card)
            .border(1.dp, OmniTheme.colors.borderSubtle, RoundedCornerShape(18.dp))
    ) {
        // Top Hairline Gradient Accent (Violet Glow)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            OmniTheme.colors.primaryViolet,
                            OmniTheme.colors.primaryContainer,
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Header Row: Status badge on one side, Elapsed Work Time chip on the other
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Live Status Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isCheckedIn) Color(0xFF063726) else Color(0xFF261C1E))
                        .border(
                            1.dp,
                            if (isCheckedIn) Color(0xFF10B981).copy(alpha = 0.5f) else Color(0xFFEF4444).copy(alpha = 0.3f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (isCheckedIn) Color(0xFF10B981) else Color(0xFFEF4444))
                    )
                    Text(
                        text = if (isCheckedIn) "در محل کار ($locationName)" else "خارج از شرکت (عدم ثبت تردد)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isCheckedIn) Color(0xFF34D399) else Color(0xFFFCA5A5)
                    )
                }

                // Elapsed Work Time Pill (if checked in)
                if (isCheckedIn) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF1B202D))
                            .border(1.dp, Color(0xFF2A3144), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = safeElapsed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }
            }

            // 2. Center Content: Checked In vs Checked Out
            if (isCheckedIn) {
                // ==================== CHECKED IN STATE ====================
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "زمان ورود ثبت‌شده",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Normal
                    )

                    // Large Centered Clock Digits
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = safeCheckIn,
                            fontSize = 46.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE2E8F0),
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "صبح",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFA78BFA),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Subtitle: Official system record + completion percentage
                    val annotatedSubtitle = buildAnnotatedString {
                        append("ثبت ورود رسمی سیستم مرکزی • ")
                        withStyle(style = SpanStyle(color = Color(0xFFA78BFA), fontWeight = FontWeight.Bold)) {
                            append("تکمیل $completionPercentage٪")
                        }
                        append(" از ساعت موظف")
                    }
                    Text(
                        text = annotatedSubtitle,
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Linear Progress Bar (72%)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF222838))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(completionPercentage / 100f)
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFF7C3AED),
                                            Color(0xFFA78BFA)
                                        )
                                    )
                                )
                        )
                    }
                }

                // 3. Action Buttons for Checked In State: ثبت خروج & استراحت
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Clock Out Button (Dark Maroon/Red container)
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF3B151C))
                            .border(1.dp, Color(0xFFE11D48).copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .clickable { onCheckOutClick() },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            tint = Color(0xFFFDA4AF),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ثبت خروج",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFDA4AF)
                        )
                    }

                    // Break Button (Dark Slate container)
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1F2432))
                            .border(1.dp, Color(0xFF323B4E), RoundedCornerShape(12.dp))
                            .clickable { onBreakClick() },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Coffee,
                            contentDescription = null,
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "استراحت",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }
            } else {
                // ==================== CHECKED OUT STATE ====================
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "شروع شیفت کاری امروز",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE2E8F0)
                        )
                        Text(
                            text = "نحوه فعالیت خود را انتخاب کرده و ورود را ثبت نمایید:",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    // Presence Type Toggle (حضوری / دورکاری)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF171B26))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // On-site option
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedWorkType == WorkType.ON_SITE) Color(0xFF2E1E5B) else Color.Transparent)
                                .border(
                                    1.dp,
                                    if (selectedWorkType == WorkType.ON_SITE) Color(0xFF7C3AED).copy(alpha = 0.6f) else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onWorkTypeChange(WorkType.ON_SITE) },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Apartment,
                                contentDescription = null,
                                tint = if (selectedWorkType == WorkType.ON_SITE) Color(0xFFC4B5FD) else Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "حضوری در شرکت",
                                fontSize = 12.sp,
                                fontWeight = if (selectedWorkType == WorkType.ON_SITE) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedWorkType == WorkType.ON_SITE) Color(0xFFC4B5FD) else Color(0xFF94A3B8)
                            )
                        }

                        // Remote option
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedWorkType == WorkType.REMOTE) Color(0xFF2E1E5B) else Color.Transparent)
                                .border(
                                    1.dp,
                                    if (selectedWorkType == WorkType.REMOTE) Color(0xFF7C3AED).copy(alpha = 0.6f) else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onWorkTypeChange(WorkType.REMOTE) },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = if (selectedWorkType == WorkType.REMOTE) Color(0xFFC4B5FD) else Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "دورکاری (ریموت)",
                                fontSize = 12.sp,
                                fontWeight = if (selectedWorkType == WorkType.REMOTE) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedWorkType == WorkType.REMOTE) Color(0xFFC4B5FD) else Color(0xFF94A3B8)
                            )
                        }
                    }

                    // Large Full-Width Check-in Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF7C3AED),
                                        Color(0xFF6D28D9)
                                    )
                                )
                            )
                            .clickable { onCheckInClick() },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Login,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ثبت ورود به کار ⏰",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Checked In Mode")
@Composable
private fun OmniHeroCardCheckedInPreview() {
    OmniSpaceTheme(themeMode = OmniThemeMode.DARK) {
        androidx.compose.runtime.CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
            OmniHeroCard(isCheckedIn = true)
        }
    }
}

@Preview(name = "Checked Out Mode")
@Composable
private fun OmniHeroCardCheckedOutPreview() {
    OmniSpaceTheme(themeMode = OmniThemeMode.DARK) {
        androidx.compose.runtime.CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
            OmniHeroCard(isCheckedIn = false)
        }
    }
}
