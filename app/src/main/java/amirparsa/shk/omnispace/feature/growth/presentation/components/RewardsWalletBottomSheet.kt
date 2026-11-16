package amirparsa.shk.omnispace.feature.growth.presentation.components

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import amirparsa.shk.omnispace.core.designsystem.DarkBorderSubtle
import amirparsa.shk.omnispace.core.designsystem.DarkCard
import amirparsa.shk.omnispace.core.designsystem.DarkElevated
import amirparsa.shk.omnispace.core.designsystem.DarkPrimaryViolet
import amirparsa.shk.omnispace.core.designsystem.DarkStatusInfo
import amirparsa.shk.omnispace.core.designsystem.DarkStatusSuccess
import amirparsa.shk.omnispace.core.designsystem.DarkStatusWarning
import amirparsa.shk.omnispace.core.designsystem.DarkTextMuted
import amirparsa.shk.omnispace.core.designsystem.DarkTextSecondary

data class PayoutPoolItem(
    val title: String,
    val amount: String,
    val signatures: String, // e.g. "۲ از ۳ امضا"
    val status: String,
    val statusColor: Color,
    val dueDate: String
)

/**
 * RewardsWalletBottomSheet
 *
 * کیف پول و استخرهای پاداش سازمانی (Rewards Wallet & Payouts Sheet)
 * منطبق بر متد وب /me/payouts شامل موجودی امتیاز، استخرها و امضاهای تأییدکنندگان
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsWalletBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    pools: List<PayoutPoolItem> = emptyList()
) {
    val context = LocalContext.current

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            containerColor = DarkElevated,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 4.dp)
                        .size(width = 38.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(DarkTextMuted.copy(alpha = 0.4f))
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "کیف پول و پاداش‌های سازمانی",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "استخرهای دوره و سوابق تسویه (/me/payouts)",
                            fontSize = 11.sp,
                            color = DarkTextSecondary
                        )
                    }
                    IconButton(onClick = onDismissRequest) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "بستن", tint = DarkTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hero Wallet Balance Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkPrimaryViolet.copy(alpha = 0.5f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF2E1B5B),
                                        Color(0xFF1E1738),
                                        Color(0xFF13161C)
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = DarkPrimaryViolet,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "موجودی پاداش کسب‌شده شما",
                                        fontSize = 12.sp,
                                        color = DarkTextSecondary
                                    )
                                }

                                Surface(
                                    color = DarkPrimaryViolet.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = DarkStatusWarning, modifier = Modifier.size(13.dp))
                                        Text(text = "رتبه ۳ تیم", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "۲,۴۵۰ امتیاز پاداش",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "ارزش معادل ریالی: ۲۴,۵۰۰,۰۰۰ تومان",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkStatusSuccess
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // KPI Qualification Progress
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "شاخص کیفیت و KPI تسویه:", fontSize = 11.sp, color = DarkTextMuted)
                                Text(text = "۹۲٪ (آستانه لازم: ۸۰٪)", fontSize = 11.sp, color = DarkStatusSuccess, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { 0.92f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = DarkStatusSuccess,
                                trackColor = DarkElevated
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section: Active Pools
                Text(
                    text = "استخرهای دوره و سوابق تسویه (Payout Pools):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (pools.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "هیچ استخر پاداشی در حال حاضر فعال نیست.",
                                fontSize = 12.sp,
                                color = DarkTextSecondary
                            )
                        }
                    } else {
                        pools.forEach { pool ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkCard),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = pool.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = pool.amount,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DarkPrimaryViolet
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Surface(
                                        color = DarkElevated,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = pool.signatures, fontSize = 11.sp, color = DarkTextSecondary)
                                            Surface(
                                                color = pool.statusColor.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = pool.status,
                                                    color = pool.statusColor,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(text = pool.dueDate, fontSize = 10.sp, color = DarkTextMuted)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        Toast.makeText(context, "درخواست تسویه زودهنگام پاداش برای بررسی واحد مالی ثبت شد.", Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkPrimaryViolet),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Payments, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "درخواست تسویه مانده پاداش با فیش حقوقی", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

