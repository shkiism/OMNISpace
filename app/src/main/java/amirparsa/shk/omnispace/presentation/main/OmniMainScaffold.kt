package amirparsa.shk.omnispace.presentation.main

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import amirparsa.shk.omnispace.core.designsystem.OmniTheme
import amirparsa.shk.omnispace.core.designsystem.component.OmniBottomBar
import amirparsa.shk.omnispace.core.designsystem.component.OmniFab
import amirparsa.shk.omnispace.core.designsystem.component.OmniNavigationTab
import amirparsa.shk.omnispace.core.designsystem.component.OmniTopBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.Color
import amirparsa.shk.omnispace.core.designsystem.component.OmniAvatar
import amirparsa.shk.omnispace.feature.auth.presentation.LoginScreen
import amirparsa.shk.omnispace.feature.chat.presentation.ChatRoomScreen
import amirparsa.shk.omnispace.feature.connect.presentation.ConnectScreen
import amirparsa.shk.omnispace.feature.growth.presentation.components.LeaveRequestBottomSheet
import amirparsa.shk.omnispace.feature.growth.presentation.OrgGrowthScreen
import amirparsa.shk.omnispace.feature.issue.presentation.components.CreateIssueBottomSheet
import amirparsa.shk.omnispace.feature.issue.presentation.IssueDetailScreen
import amirparsa.shk.omnispace.feature.meeting.presentation.VideoCallScreen
import amirparsa.shk.omnispace.feature.notification.presentation.shade.AndroidSystemNotificationShadeScreen
import amirparsa.shk.omnispace.feature.notification.presentation.inbox.NotificationInboxScreen
import amirparsa.shk.omnispace.feature.people.presentation.ColleagueProfileScreen
import amirparsa.shk.omnispace.feature.qa.presentation.ZephyrQaScreen
import amirparsa.shk.omnispace.feature.search.presentation.GlobalSearchScreen
import amirparsa.shk.omnispace.feature.today.presentation.TodayScreen
import amirparsa.shk.omnispace.feature.wiki.presentation.article.WikiArticleScreen
import amirparsa.shk.omnispace.feature.wiki.presentation.tree.WikiTreeNavigationScreen
import amirparsa.shk.omnispace.feature.work.presentation.kanban.InteractiveKanbanScreen
import amirparsa.shk.omnispace.feature.work.presentation.work.WorkScreen
import amirparsa.shk.omnispace.presentation.main.model.ChatNavArgs

/**
 * Stateful Main Application Scaffold managing tabs, overlays, and modal sheets using MVI.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OmniMainScaffold(
    incomingDestination: String? = null,
    incomingIssueKey: String? = null,
    onClearIncoming: () -> Unit = {},
    viewModel: MainViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(incomingDestination, incomingIssueKey) {
        if (incomingDestination != null) {
            viewModel.onIntent(MainIntent.HandleIncomingDestination(incomingDestination, incomingIssueKey))
            onClearIncoming()
        }
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MainEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is MainEffect.ShowLongToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    OmniMainScaffoldContent(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier
    )
}

/**
 * Stateless UI content composable for Main Scaffold.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OmniMainScaffoldContent(
    state: MainUiState,
    onIntent: (MainIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val createIssueSheetState = rememberModalBottomSheetState()

    when {
        !state.isAuthenticated -> {
            LoginScreen(
                onLoginSuccess = { onIntent(MainIntent.RefreshSession) }
            )
        }

        state.isInVideoCall -> {
            BackHandler { onIntent(MainIntent.LeaveVideoCall) }
            VideoCallScreen(
                roomTitle = "جلسه ویدیویی",
                onLeaveCall = { onIntent(MainIntent.LeaveVideoCall) },
                onOpenChat = {
                    onIntent(
                        MainIntent.OpenChat(
                            ChatNavArgs(
                                title = "#جلسه-ویدیویی",
                                subtitle = "گفتگوی متنی جلسه",
                                linkedIssueKey = null,
                                linkedIssueTitle = null
                            )
                        )
                    )
                }
            )
        }

        state.isZephyrQaOpen -> {
            BackHandler { onIntent(MainIntent.CloseZephyrQa) }
            ZephyrQaScreen(
                onBackClick = { onIntent(MainIntent.CloseZephyrQa) },
                onCreateBugClick = { tc ->
                    onIntent(MainIntent.OpenIssueDetail(tc.bugIssueKey ?: "MD-156"))
                }
            )
        }

        state.selectedWikiTitle != null -> {
            BackHandler { onIntent(MainIntent.CloseWikiArticle) }
            WikiArticleScreen(
                articleId = state.selectedWikiId ?: state.selectedWikiTitle.orEmpty(),
                onBackClick = { onIntent(MainIntent.CloseWikiArticle) }
            )
        }

        state.isSystemNotificationShadeOpen -> {
            BackHandler { onIntent(MainIntent.CloseSystemNotificationShade) }
            AndroidSystemNotificationShadeScreen(
                onBackClick = { onIntent(MainIntent.CloseSystemNotificationShade) },
                onJoinVideoCall = {
                    onIntent(MainIntent.CloseSystemNotificationShade)
                    onIntent(MainIntent.StartVideoCall)
                },
                onOpenIssue = { key ->
                    onIntent(MainIntent.CloseSystemNotificationShade)
                    onIntent(MainIntent.OpenIssueDetail(key))
                },
                onOpenHr = {
                    onIntent(MainIntent.CloseSystemNotificationShade)
                    onIntent(MainIntent.SelectTab(OmniNavigationTab.HR_GROWTH))
                }
            )
        }

        state.isNotificationsOpen -> {
            BackHandler { onIntent(MainIntent.CloseNotifications) }
            NotificationInboxScreen(
                onBackClick = { onIntent(MainIntent.CloseNotifications) },
                onOpenIssue = { key ->
                    onIntent(MainIntent.CloseNotifications)
                    onIntent(MainIntent.OpenIssueDetail(key))
                },
                onJoinMeeting = {
                    onIntent(MainIntent.CloseNotifications)
                    onIntent(MainIntent.StartVideoCall)
                },
                onViewPayslip = {
                    onIntent(MainIntent.CloseNotifications)
                    onIntent(MainIntent.SelectTab(OmniNavigationTab.HR_GROWTH))
                },
                onOpenZephyrQa = {
                    onIntent(MainIntent.CloseNotifications)
                    onIntent(MainIntent.OpenZephyrQa)
                },
                onOpenSystemNotificationShade = {
                    onIntent(MainIntent.CloseNotifications)
                    onIntent(MainIntent.OpenSystemNotificationShade)
                }
            )
        }

        state.selectedChat != null -> {
            val chat = state.selectedChat
            BackHandler { onIntent(MainIntent.CloseChat) }
            ChatRoomScreen(
                roomId = chat.linkedIssueKey ?: "chat",
                roomTitle = chat.title,
                roomSubtitle = chat.subtitle,
                linkedIssueKey = chat.linkedIssueKey,
                linkedIssueTitle = chat.linkedIssueTitle,
                onBackClick = { onIntent(MainIntent.CloseChat) },
                onStartVideoCall = { onIntent(MainIntent.StartVideoCall) },
                onStartVoiceCall = { onIntent(MainIntent.StartVideoCall) },
                onOpenLinkedIssue = { key -> onIntent(MainIntent.OpenIssueDetail(key)) }
            )
        }

        state.selectedIssueKey != null -> {
            BackHandler { onIntent(MainIntent.CloseIssueDetail) }
            IssueDetailScreen(
                issueKey = state.selectedIssueKey,
                onBackClick = { onIntent(MainIntent.CloseIssueDetail) }
            )
        }

        state.isGlobalSearchOpen -> {
            BackHandler { onIntent(MainIntent.CloseGlobalSearch) }
            GlobalSearchScreen(
                onBackClick = { onIntent(MainIntent.CloseGlobalSearch) },
                onOpenIssue = { key ->
                    onIntent(MainIntent.CloseGlobalSearch)
                    onIntent(MainIntent.OpenIssueDetail(key))
                },
                onOpenChat = { _ ->
                    onIntent(MainIntent.CloseGlobalSearch)
                    onIntent(
                        MainIntent.OpenChat(
                            ChatNavArgs(
                                title = "#عمومی",
                                subtitle = "چت سازمانی",
                                linkedIssueKey = null,
                                linkedIssueTitle = null
                            )
                        )
                    )
                },
                onOpenWiki = { title ->
                    onIntent(MainIntent.CloseGlobalSearch)
                    onIntent(MainIntent.OpenWikiArticle(title))
                },
                onOpenProfile = { name ->
                    onIntent(MainIntent.CloseGlobalSearch)
                    onIntent(MainIntent.OpenColleagueProfile(name))
                }
            )
        }

        state.selectedColleagueName != null -> {
            val colleague = state.selectedColleagueName
            BackHandler { onIntent(MainIntent.CloseColleagueProfile) }
            ColleagueProfileScreen(
                colleagueName = colleague,
                onBackClick = { onIntent(MainIntent.CloseColleagueProfile) },
                onStartChat = { name ->
                    onIntent(MainIntent.CloseColleagueProfile)
                    onIntent(
                        MainIntent.OpenChat(
                            ChatNavArgs(
                                title = name,
                                subtitle = "پیام مستقیم",
                                linkedIssueKey = null,
                                linkedIssueTitle = null
                            )
                        )
                    )
                },
                onStartVideoCall = { _ ->
                    onIntent(MainIntent.CloseColleagueProfile)
                    onIntent(MainIntent.StartVideoCall)
                },
                onOpenTask = { key ->
                    onIntent(MainIntent.CloseColleagueProfile)
                    onIntent(MainIntent.OpenIssueDetail(key))
                }
            )
        }

        state.isKanbanBoardOpen -> {
            BackHandler { onIntent(MainIntent.CloseKanbanBoard) }
            InteractiveKanbanScreen(
                projectName = "تخته اسکرام و کانبان",
                onBackClick = { onIntent(MainIntent.CloseKanbanBoard) },
                onTaskClick = { key ->
                    onIntent(MainIntent.OpenIssueDetail(key))
                },
                onNewTaskClick = {
                    onIntent(MainIntent.SetCreateIssueOpen(true))
                }
            )
        }

        state.isWikiTreeOpen -> {
            BackHandler { onIntent(MainIntent.CloseWikiTree) }
            WikiTreeNavigationScreen(
                onBackClick = { onIntent(MainIntent.CloseWikiTree) },
                onDocumentClick = { docId, title ->
                    onIntent(MainIntent.OpenWikiArticle(title = title, articleId = docId))
                }
            )
        }

        else -> {
            Scaffold(
                modifier = modifier.fillMaxSize(),
                containerColor = OmniTheme.colors.canvas,
                topBar = {
                    OmniTopBar(
                        title = when (state.currentTab) {
                            OmniNavigationTab.TODAY -> "صبح بخیر، ${state.userDisplayName}"
                            OmniNavigationTab.WORKSPACE -> "کارها و فضاها"
                            OmniNavigationTab.CONNECT -> "ارتباطات و جلسات"
                            OmniNavigationTab.HR_GROWTH -> "سازمان و من"
                        },
                        subtitle = when (state.currentTab) {
                            OmniNavigationTab.TODAY -> "${state.todayJalali} · ${state.organizationName}"
                            OmniNavigationTab.WORKSPACE -> "پروژه‌ها، اسپرینت‌ها و اسناد"
                            OmniNavigationTab.CONNECT -> "چت، اتاق‌های LiveKit و همکاران"
                            OmniNavigationTab.HR_GROWTH -> "خدمات پرسنلی، حقوق و رشد"
                        },
                        userDisplayName = state.userDisplayName,
                        unreadCount = state.unreadCount,
                        onSearchClick = { onIntent(MainIntent.OpenGlobalSearch) },
                        onInboxClick = { onIntent(MainIntent.OpenNotifications) },
                        onProfileClick = { onIntent(MainIntent.ToggleProfileDialog) }
                    )
                },
                bottomBar = {
                    OmniBottomBar(
                        currentTab = state.currentTab,
                        onTabSelected = { onIntent(MainIntent.SelectTab(it)) },
                        usePersian = true
                    )
                },
                floatingActionButton = {
                    OmniFab(
                        onNewTaskClick = { onIntent(MainIntent.SetCreateIssueOpen(true)) },
                        onNewLeaveClick = { onIntent(MainIntent.SetLeaveRequestOpen(true)) },
                        onNewMeetingClick = { onIntent(MainIntent.StartVideoCall) },
                        onNewNoteClick = { /* Note Sheet */ }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (state.currentTab) {
                        OmniNavigationTab.TODAY -> {
                            TodayScreen(
                                onNavigateToWorkspace = { onIntent(MainIntent.SelectTab(OmniNavigationTab.WORKSPACE)) },
                                onTaskClick = { issueKey -> onIntent(MainIntent.OpenIssueDetail(issueKey)) },
                                onJoinMeetingClick = { onIntent(MainIntent.StartVideoCall) },
                                onOpenSystemNotificationShade = { onIntent(MainIntent.OpenSystemNotificationShade) }
                            )
                        }

                        OmniNavigationTab.WORKSPACE -> {
                            WorkScreen(
                                onTaskClick = { issue -> onIntent(MainIntent.OpenIssueDetail(issue.key)) },
                                onNewTaskClick = { onIntent(MainIntent.SetCreateIssueOpen(true)) },
                                onOpenZephyrQa = { onIntent(MainIntent.OpenZephyrQa) },
                                onWikiClick = { title ->
                                    val mappedId = when {
                                        title.contains("معماری") -> "doc-arch-1"
                                        title.contains("استقرار") || title.contains("DevOps") -> "doc-arch-2"
                                        title.contains("طراحی") || title.contains("دیزاین") -> "doc-ui-1"
                                        else -> "doc-qa-1"
                                    }
                                    onIntent(MainIntent.OpenWikiArticle(title = title, articleId = mappedId))
                                },
                                onOpenInteractiveKanban = { onIntent(MainIntent.OpenKanbanBoard) },
                                onOpenWikiTree = { onIntent(MainIntent.OpenWikiTree) }
                            )
                        }

                        OmniNavigationTab.CONNECT -> {
                            ConnectScreen(
                                onJoinLiveKitClick = { onIntent(MainIntent.StartVideoCall) },
                                onNewInstantMeetingClick = { onIntent(MainIntent.StartVideoCall) },
                                onChatClick = { chat ->
                                    onIntent(
                                        MainIntent.OpenChat(
                                            ChatNavArgs(
                                                title = chat.title,
                                                subtitle = chat.subtitle,
                                                linkedIssueKey = null,
                                                linkedIssueTitle = null
                                            )
                                        )
                                    )
                                },
                                onColleagueClick = { name ->
                                    onIntent(MainIntent.OpenColleagueProfile(name))
                                }
                            )
                        }

                        OmniNavigationTab.HR_GROWTH -> {
                            OrgGrowthScreen(
                                onNewLeaveClick = { onIntent(MainIntent.SetLeaveRequestOpen(true)) },
                                onDownloadPayslipClick = {
                                    onIntent(MainIntent.ShowToastMessage("فیش حقوقی آبان ماه دانلود شد."))
                                }
                            )
                        }
                    }

                    // MODAL BOTTOM SHEET FOR LEAVE REQUEST
                    if (state.isLeaveRequestOpen) {
                        val colleaguesForLeave = remember(state.availableAssignees) {
                            state.availableAssignees.map { person ->
                                amirparsa.shk.omnispace.feature.growth.presentation.components.StandInColleague(
                                    id = person.id,
                                    name = person.displayName,
                                    role = person.orgRole ?: "عضو تیم",
                                    initials = if (person.displayName.length >= 2) person.displayName.take(2) else "هم",
                                    avatarBg = androidx.compose.ui.graphics.Color(0xFF6D28D9)
                                )
                            }
                        }
                        LeaveRequestBottomSheet(
                            remainingDays = 12,
                            colleagues = colleaguesForLeave,
                            onDismissRequest = { onIntent(MainIntent.SetLeaveRequestOpen(false)) },
                            onSubmitSuccess = { type, from, to, colleague, description ->
                                onIntent(
                                    MainIntent.SubmitLeaveRequest(
                                        typeName = type.titleFa,
                                        fromDate = from,
                                        toDate = to,
                                        standInColleagueName = colleague.name,
                                        description = description
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    // GLOBAL MODAL BOTTOM SHEET FOR ISSUE CREATION
    if (state.isCreateIssueOpen) {
        CreateIssueBottomSheet(
            onDismissRequest = { onIntent(MainIntent.SetCreateIssueOpen(false)) },
            sheetState = createIssueSheetState,
            projects = state.availableProjects,
            assignees = state.availableAssignees,
            onIssueCreated = { title, project, type, priority, assignee ->
                onIntent(MainIntent.SubmitNewIssue(title, project, type, priority, assignee))
            },
            onIssueCreatedFull = { title, projectId, projectName, type, priority, assigneeId, description ->
                onIntent(
                    MainIntent.SubmitNewIssue(
                        title = title,
                        project = projectName,
                        type = type,
                        priority = priority,
                        assignee = assigneeId ?: "",
                        projectId = projectId,
                        assigneeId = assigneeId,
                        description = description
                    )
                )
            }
        )
    }

    // USER PROFILE & LOGOUT DIALOG
    if (state.isProfileDialogOpen) {
        AlertDialog(
            onDismissRequest = { onIntent(MainIntent.ToggleProfileDialog) },
            containerColor = Color(0xFF13161C),
            titleContentColor = Color.White,
            textContentColor = Color(0xFF94A3B8),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OmniAvatar(name = state.userDisplayName, size = 42.dp, isOnline = true)
                    Column {
                        Text(
                            text = state.userDisplayName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = state.organizationName,
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Business,
                            contentDescription = null,
                            tint = Color(0xFF9D7BF6),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "سازمان: ${state.organizationName}",
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccountCircle,
                            contentDescription = null,
                            tint = Color(0xFF9D7BF6),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "وضعیت نشست: احراز شده و فعال",
                            fontSize = 13.sp,
                            color = Color(0xFF10B981)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { onIntent(MainIntent.Logout) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Logout,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text("خروج از حساب", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { onIntent(MainIntent.ToggleProfileDialog) }) {
                    Text("بستن", color = Color(0xFF94A3B8))
                }
            }
        )
    }
}

@Composable
fun PlaceholderScreen(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OmniTheme.colors.canvas),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = OmniTheme.colors.textPrimary
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = OmniTheme.colors.textMuted,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

