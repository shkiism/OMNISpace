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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import amirparsa.shk.omnispace.core.designsystem.DarkBorderSubtle
import amirparsa.shk.omnispace.core.designsystem.DarkCard
import amirparsa.shk.omnispace.core.designsystem.DarkElevated
import amirparsa.shk.omnispace.core.designsystem.DarkPrimaryViolet
import amirparsa.shk.omnispace.core.designsystem.DarkStatusSuccess
import amirparsa.shk.omnispace.core.designsystem.DarkStatusWarning
import amirparsa.shk.omnispace.core.designsystem.DarkTextMuted
import amirparsa.shk.omnispace.core.designsystem.DarkTextSecondary

/**
 * PerformanceReviewBottomSheet
 *
 * فرم ارزیابی ۳۶۰ درجه و اهداف دوره‌ای (360 Performance Review Form)
 * منطبق بر لاجیک وب‌سایت (/performance/peer-requests) با امتیازدهی ستاره‌ای، بازخورد کیفی و OKRها
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceReviewBottomSheet(
    targetColleagueName: String = "همکار",
    cycleName: String = "ارزیابی دوره‌ای عملکرد",
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    onSubmitReview: (scores: Map<String, Int>, strengths: String, improvements: String) -> Unit = { _, _, _ -> }
) {
    val context = LocalContext.current

    var scoreQuality by remember { mutableIntStateOf(5) }
    var scoreTeamwork by remember { mutableIntStateOf(5) }
    var scoreOwnership by remember { mutableIntStateOf(4) }
    var scoreProblemSolving by remember { mutableIntStateOf(4) }

    var strengthsText by remember { mutableStateOf("") }
    var improvementsText by remember { mutableStateOf("") }

    var okrProgress by remember { mutableFloatStateOf(0f) }

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
                            text = "ارزیابی ۳۶۰ درجه همتایان (Peer Review)",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "$cycleName • همکار: $targetColleagueName",
                            fontSize = 11.sp,
                            color = DarkTextSecondary
                        )
                    }
                    IconButton(onClick = onDismissRequest) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "بستن", tint = DarkTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Quantitative 1-5 Star Ratings
                Text(
                    text = "امتیازدهی به شاخص‌های کلیدی (۱ تا ۵ ستاره):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StarRatingRow(
                            label = "کیفیت خروجی و دقت فنی:",
                            score = scoreQuality,
                            onScoreChange = { scoreQuality = it }
                        )
                        StarRatingRow(
                            label = "همکاری تیمی و مهارت‌های ارتباطی:",
                            score = scoreTeamwork,
                            onScoreChange = { scoreTeamwork = it }
                        )
                        StarRatingRow(
                            label = "مسئولیت‌پذیری و تعهد به زمان‌بندی:",
                            score = scoreOwnership,
                            onScoreChange = { scoreOwnership = it }
                        )
                        StarRatingRow(
                            label = "خلاقیت و توانایی حل مسئله:",
                            score = scoreProblemSolving,
                            onScoreChange = { scoreProblemSolving = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: OKRs Progress
                Text(
                    text = "ارزیابی پیشرفت اهداف فصل (Key Results - ${(okrProgress * 100).toInt()}٪):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "هدف: تحویل ماژول LiveKit و دیزاین سیستم سوپراپ با موفقیت ۹۵٪ تست‌ها",
                            fontSize = 12.sp,
                            color = DarkTextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Slider(
                            value = okrProgress,
                            onValueChange = { okrProgress = it },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = DarkPrimaryViolet,
                                activeTrackColor = DarkPrimaryViolet,
                                inactiveTrackColor = DarkElevated
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Strengths
                Text(
                    text = "نقاط قوت بارز همکار:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = strengthsText,
                    onValueChange = { strengthsText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkCard,
                        unfocusedContainerColor = DarkCard,
                        focusedBorderColor = DarkPrimaryViolet,
                        unfocusedBorderColor = DarkBorderSubtle,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Section 4: Areas for improvement
                Text(
                    text = "زمینه‌های قابل بهبود و توصیه‌های رشد:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = improvementsText,
                    onValueChange = { improvementsText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkCard,
                        unfocusedContainerColor = DarkCard,
                        focusedBorderColor = DarkPrimaryViolet,
                        unfocusedBorderColor = DarkBorderSubtle,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // CTA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val scores = mapOf(
                                "quality" to scoreQuality,
                                "teamwork" to scoreTeamwork,
                                "ownership" to scoreOwnership,
                                "problem_solving" to scoreProblemSolving
                            )
                            onSubmitReview(scores, strengthsText, improvementsText)
                            Toast.makeText(context, "ارزیابی ۳۶۰ درجه با موفقیت ثبت و ارسال شد.", Toast.LENGTH_LONG).show()
                            onDismissRequest()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkPrimaryViolet),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "ثبت نهایی ارزیابی همتا", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = onDismissRequest,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle)
                    ) {
                        Text(text = "انصراف", color = DarkTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun StarRatingRow(
    label: String,
    score: Int,
    onScoreChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f))

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            (1..5).forEach { star ->
                val isFilled = star <= score
                Icon(
                    imageVector = if (isFilled) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = null,
                    tint = if (isFilled) DarkStatusWarning else DarkTextMuted,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onScoreChange(star) }
                )
            }
        }
    }
}

