package amirparsa.shk.omnispace.feature.issue.presentation.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import amirparsa.shk.omnispace.core.designsystem.DarkBorderSubtle
import amirparsa.shk.omnispace.core.designsystem.DarkCard
import amirparsa.shk.omnispace.core.designsystem.DarkElevated
import amirparsa.shk.omnispace.core.designsystem.DarkPrimaryViolet
import amirparsa.shk.omnispace.core.designsystem.DarkStatusDanger
import amirparsa.shk.omnispace.core.designsystem.DarkStatusInfo
import amirparsa.shk.omnispace.core.designsystem.DarkStatusSuccess
import amirparsa.shk.omnispace.core.designsystem.DarkStatusWarning
import amirparsa.shk.omnispace.core.designsystem.DarkTextMuted
import amirparsa.shk.omnispace.core.designsystem.DarkTextSecondary
import amirparsa.shk.omnispace.core.utils.DateUtils
import amirparsa.shk.omnispace.feature.connect.data.remote.dto.PersonDto
import amirparsa.shk.omnispace.feature.work.data.remote.dto.ProjectDto

/**
 * CreateIssueBottomSheet
 *
 * فرم ایجاد تسک، باگ و استوری جدید در پروژه‌ها (Create Issue BottomSheet)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateIssueBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    projects: List<ProjectDto> = emptyList(),
    assignees: List<PersonDto> = emptyList(),
    onIssueCreated: (title: String, project: String, type: String, priority: String, assignee: String) -> Unit = { _, _, _, _, _ -> },
    onIssueCreatedFull: (title: String, projectId: String, projectName: String, type: String, priority: String, assigneeId: String?, description: String?) -> Unit = { _, _, _, _, _, _, _ -> }
) {
    val projectsList = projects
    var selectedProject by remember(projectsList) { mutableStateOf(projectsList.firstOrNull()) }

    val assigneesList = assignees
    var selectedAssignee by remember(assigneesList) { mutableStateOf(assigneesList.firstOrNull()) }

    val issueTypes = listOf(
        "Story" to DarkStatusInfo,
        "Task" to DarkPrimaryViolet,
        "Bug" to DarkStatusDanger,
        "QA" to Color(0xFFA855F7)
    )
    var selectedType by remember { mutableStateOf(issueTypes[0].first) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val priorities = listOf(
        "بحرانی (Blocker)" to DarkStatusDanger,
        "بالا (High)" to DarkStatusWarning,
        "عادی (Normal)" to DarkStatusSuccess,
        "پایین (Low)" to Color(0xFF64748B)
    )
    var selectedPriority by remember { mutableStateOf(priorities[2].first) }

    var dueAt by remember { mutableStateOf(DateUtils.getCurrentJalaliDate().formatFa()) }

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
                            text = "ایجاد تسک یا باگ جدید",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "ثبت در اسپرینت جاری و بورد کانبان",
                            fontSize = 11.sp,
                            color = DarkTextSecondary
                        )
                    }
                    IconButton(onClick = onDismissRequest) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "بستن", tint = DarkTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Project Selector
                Text(text = "پروژه هدف *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkTextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                if (projectsList.isEmpty()) {
                    Text(
                        text = "پروژه‌ای برای انتخاب یافت نشد یا در حال بارگذاری است...",
                        fontSize = 11.sp,
                        color = DarkTextMuted,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        projectsList.forEach { proj ->
                            val isSelected = proj.id == selectedProject?.id
                            Surface(
                                modifier = Modifier.clickable { selectedProject = proj },
                                color = if (isSelected) DarkPrimaryViolet.copy(alpha = 0.2f) else DarkCard,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) DarkPrimaryViolet else DarkBorderSubtle
                                )
                            ) {
                                Text(
                                    text = proj.name,
                                    color = if (isSelected) DarkPrimaryViolet else DarkTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Issue Type
                Text(text = "نوع کار (Issue Type) *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkTextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    issueTypes.forEach { (type, color) ->
                        val isSelected = type == selectedType
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedType = type },
                            color = if (isSelected) color.copy(alpha = 0.2f) else DarkCard,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) color else DarkBorderSubtle
                            )
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = type,
                                    color = if (isSelected) color else DarkTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title field
                Text(text = "عنوان تسک *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkTextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("مثال: اتصال به سرویس LiveKit در پس‌زمینه...", fontSize = 13.sp, color = DarkTextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkCard,
                        unfocusedContainerColor = DarkCard,
                        focusedBorderColor = DarkPrimaryViolet,
                        unfocusedBorderColor = DarkBorderSubtle,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description field
                Text(text = "توضیحات و معیارهای پذیرش (اختیاری)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkTextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("جزییات، لاگ‌ها، گام‌های بازتولید یا تسک‌های فرعی...", fontSize = 13.sp, color = DarkTextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
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

                Spacer(modifier = Modifier.height(14.dp))

                // Priority & Assignee Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "اولویت", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkTextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        priorities.forEach { (prio, color) ->
                            val isSelected = prio == selectedPriority
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .clickable { selectedPriority = prio },
                                color = if (isSelected) color.copy(alpha = 0.18f) else DarkCard,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) color else DarkBorderSubtle)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
                                    Text(text = prio, color = if (isSelected) Color.White else DarkTextMuted, fontSize = 10.sp)
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "فرد تخصیص‌یافته", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkTextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        assigneesList.forEach { person ->
                            val isSelected = person.id == selectedAssignee?.id
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .clickable { selectedAssignee = person },
                                color = if (isSelected) DarkPrimaryViolet.copy(alpha = 0.18f) else DarkCard,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) DarkPrimaryViolet else DarkBorderSubtle)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = if (isSelected) DarkPrimaryViolet else DarkTextMuted, modifier = Modifier.size(12.dp))
                                    Text(text = person.displayName, color = if (isSelected) Color.White else DarkTextMuted, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // CTA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val currentProject = selectedProject
                    Button(
                        onClick = {
                            if (title.isNotBlank() && currentProject != null) {
                                onIssueCreated(
                                    title,
                                    currentProject.name,
                                    selectedType,
                                    selectedPriority,
                                    selectedAssignee?.displayName ?: ""
                                )
                                onIssueCreatedFull(
                                    title,
                                    currentProject.id,
                                    currentProject.name,
                                    selectedType,
                                    selectedPriority,
                                    selectedAssignee?.id,
                                    description.takeIf { it.isNotBlank() }
                                )
                            }
                        },
                        enabled = title.isNotBlank() && selectedProject != null,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkPrimaryViolet),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "ایجاد تسک سازمانی", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 13.sp)
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

