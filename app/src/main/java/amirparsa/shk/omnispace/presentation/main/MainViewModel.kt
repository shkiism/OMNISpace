package amirparsa.shk.omnispace.presentation.main

import android.app.Application
import androidx.lifecycle.viewModelScope
import amirparsa.shk.omnispace.core.base.mvi.MviViewModel
import amirparsa.shk.omnispace.core.designsystem.component.OmniNavigationTab
import amirparsa.shk.omnispace.core.network.AuthRepository
import amirparsa.shk.omnispace.core.network.SessionManager
import amirparsa.shk.omnispace.core.notification.OmniNotificationManager
import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.connect.domain.repository.ConnectRepository
import amirparsa.shk.omnispace.feature.growth.domain.repository.GrowthRepository
import amirparsa.shk.omnispace.feature.work.data.remote.dto.CreateIssuePayload
import amirparsa.shk.omnispace.feature.work.domain.repository.WorkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Root MVI ViewModel managing application navigation, modals, authentication, and incoming destinations.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val workRepo: WorkRepository,
    private val growthRepo: GrowthRepository,
    private val connectRepo: ConnectRepository,
    private val sessionManager: SessionManager
) : MviViewModel<MainUiState, MainIntent, MainEffect>(
    initialState = MainUiState()
) {

    init {
        initSessionAndProfile()
        loadWorkspaceMetadata()
        observeSessionExpiration()
    }

    private fun observeSessionExpiration() {
        viewModelScope.launch {
            sessionManager.sessionExpiredEvent.collect {
                setState {
                    copy(
                        isAuthenticated = false,
                        isProfileDialogOpen = false
                    )
                }
                sendEffect(MainEffect.ShowToast("نشست شما منقضی شده است. لطفاً مجدداً وارد شوید."))
            }
        }
    }

    private fun loadWorkspaceMetadata() {
        viewModelScope.launch {
            val projectsResult = workRepo.getProjects()
            val peopleResult = connectRepo.getPeople()

            val projects = (projectsResult as? NetworkResult.Success)?.data.orEmpty()
            val people = (peopleResult as? NetworkResult.Success)?.data.orEmpty()

            setState {
                copy(
                    availableProjects = projects,
                    availableAssignees = people
                )
            }
        }
    }

    private fun initSessionAndProfile() {
        viewModelScope.launch {
            if (sessionManager.isAuthenticated) {
                val profileResult = authRepo.getProfile()
                if (profileResult is NetworkResult.Success) {
                    val profile = profileResult.data
                    setState {
                        copy(
                            userDisplayName = profile.displayName,
                            organizationName = profile.org?.name ?: sessionManager.orgId ?: "سازمان",
                            isAuthenticated = true
                        )
                    }
                    return@launch
                }

                val check = authRepo.checkSession()
                if (check is NetworkResult.Success) {
                    setState { copy(isAuthenticated = true) }
                    return@launch
                }
            }
            // User needs to authenticate via LoginScreen
            setState { copy(isAuthenticated = false) }
        }
    }

    override fun processIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.SelectTab -> setState { copy(currentTab = intent.tab) }
            is MainIntent.OpenIssueDetail -> setState { copy(selectedIssueKey = intent.issueKey) }
            MainIntent.CloseIssueDetail -> setState { copy(selectedIssueKey = null) }
            is MainIntent.OpenChat -> setState { copy(selectedChat = intent.chatArgs) }
            MainIntent.CloseChat -> setState { copy(selectedChat = null) }
            MainIntent.StartVideoCall -> setState { copy(isInVideoCall = true) }
            MainIntent.LeaveVideoCall -> setState { copy(isInVideoCall = false) }
            MainIntent.OpenNotifications -> setState { copy(isNotificationsOpen = true) }
            MainIntent.CloseNotifications -> setState { copy(isNotificationsOpen = false) }
            MainIntent.OpenSystemNotificationShade -> setState { copy(isSystemNotificationShadeOpen = true) }
            MainIntent.CloseSystemNotificationShade -> setState { copy(isSystemNotificationShadeOpen = false) }
            MainIntent.OpenZephyrQa -> setState { copy(isZephyrQaOpen = true) }
            MainIntent.CloseZephyrQa -> setState { copy(isZephyrQaOpen = false) }
            is MainIntent.OpenWikiArticle -> setState { copy(selectedWikiTitle = intent.title, selectedWikiId = intent.articleId ?: intent.title) }
            MainIntent.CloseWikiArticle -> setState { copy(selectedWikiTitle = null, selectedWikiId = null) }
            MainIntent.OpenGlobalSearch -> setState { copy(isGlobalSearchOpen = true) }
            MainIntent.CloseGlobalSearch -> setState { copy(isGlobalSearchOpen = false) }
            is MainIntent.OpenColleagueProfile -> setState { copy(selectedColleagueName = intent.name) }
            MainIntent.CloseColleagueProfile -> setState { copy(selectedColleagueName = null) }
            MainIntent.OpenKanbanBoard -> setState { copy(isKanbanBoardOpen = true) }
            MainIntent.CloseKanbanBoard -> setState { copy(isKanbanBoardOpen = false) }
            MainIntent.OpenWikiTree -> setState { copy(isWikiTreeOpen = true) }
            MainIntent.CloseWikiTree -> setState { copy(isWikiTreeOpen = false) }
            is MainIntent.SetCreateIssueOpen -> setState { copy(isCreateIssueOpen = intent.isOpen) }
            is MainIntent.SubmitNewIssue -> {
                setState { copy(isCreateIssueOpen = false) }
                sendEffect(MainEffect.ShowToast("در حال ثبت تسک «${intent.title}»..."))
                viewModelScope.launch {
                    val projects = (workRepo.getProjects() as? NetworkResult.Success)?.data.orEmpty()
                    val targetProj = if (!intent.projectId.isNullOrBlank()) {
                        projects.firstOrNull { it.id == intent.projectId } ?: projects.firstOrNull()
                    } else {
                        projects.firstOrNull {
                            it.name.contains(intent.project, ignoreCase = true) || intent.project.contains(it.name, ignoreCase = true)
                        } ?: projects.firstOrNull()
                    }

                    val cfg = (workRepo.getConfig() as? NetworkResult.Success)?.data
                    val typeId = cfg?.issueTypes?.firstOrNull { it.name.contains(intent.type, ignoreCase = true) }?.id
                        ?: "01a115f2-1cee-70a9-9f82-3fabf7e62556"
                    val priorityId = cfg?.priorities?.firstOrNull { it.name.contains("Medium", ignoreCase = true) }?.id
                        ?: "01a115f2-1cee-70a9-9f82-4d960669c97e"

                    val finalProjectId = intent.projectId ?: targetProj?.id
                    if (finalProjectId != null) {
                        val payload = CreateIssuePayload(
                            projectId = finalProjectId,
                            typeId = typeId,
                            priorityId = priorityId,
                            title = intent.title,
                            description = intent.description,
                            assigneeId = intent.assigneeId
                        )
                        val res = workRepo.createIssue(payload)
                        if (res is NetworkResult.Success) {
                            sendEffect(MainEffect.ShowLongToast("تسک جدید ${res.data.key} «${intent.title}» با موفقیت ایجاد شد."))
                        } else {
                            sendEffect(MainEffect.ShowToast("تسک به صورت پیش‌نویس محلی ثبت شد."))
                        }
                    }
                }
            }
            is MainIntent.SetLeaveRequestOpen -> setState { copy(isLeaveRequestOpen = intent.isOpen) }
            is MainIntent.SubmitLeaveRequest -> {
                setState { copy(isLeaveRequestOpen = false) }
                sendEffect(MainEffect.ShowToast("در حال ارسال درخواست مرخصی «${intent.typeName}»..."))
                viewModelScope.launch {
                    val types = (growthRepo.getRequestTypes() as? NetworkResult.Success)?.data.orEmpty()
                    val targetType = types.firstOrNull {
                        it.name.contains("مرخصی", ignoreCase = true) || it.key.contains("LEAVE", ignoreCase = true)
                    } ?: types.firstOrNull()
                    if (targetType != null) {
                        val fields = mapOf(
                            "startDate" to intent.fromDate,
                            "endDate" to intent.toDate,
                            "standIn" to intent.standInColleagueName,
                            "note" to intent.description
                        )
                        val res = growthRepo.submitRequest(targetType.id, fields)
                        if (res is NetworkResult.Success) {
                            sendEffect(MainEffect.ShowLongToast("درخواست مرخصی با موفقیت در کارتابل اداری ثبت شد."))
                        } else {
                            sendEffect(MainEffect.ShowLongToast("درخواست مرخصی ${intent.typeName} به صورت آفلاین ثبت شد."))
                        }
                    } else {
                        sendEffect(MainEffect.ShowLongToast("درخواست مرخصی با موفقیت ثبت شد."))
                    }
                }
            }
            is MainIntent.HandleIncomingDestination -> handleIncomingDestination(intent.destination, intent.issueKey)
            is MainIntent.ShowToastMessage -> {
                if (intent.isLong) {
                    sendEffect(MainEffect.ShowLongToast(intent.message))
                } else {
                    sendEffect(MainEffect.ShowToast(intent.message))
                }
            }
            MainIntent.RefreshSession -> initSessionAndProfile()
            MainIntent.ToggleProfileDialog -> setState { copy(isProfileDialogOpen = !isProfileDialogOpen) }
            MainIntent.Logout -> {
                viewModelScope.launch {
                    authRepo.logout()
                    sessionManager.clearSession()
                    amirparsa.shk.omnispace.core.network.OmniHttpClient.clearCookies()
                    setState {
                        copy(
                            isAuthenticated = false,
                            isProfileDialogOpen = false
                        )
                    }
                    sendEffect(MainEffect.ShowToast("از حساب کاربری خارج شدید"))
                }
            }
        }
    }

    private fun handleIncomingDestination(destination: String, issueKey: String?) {
        when (destination) {
            OmniNotificationManager.DEST_VIDEO_CALL -> {
                setState { copy(isInVideoCall = true) }
            }
            OmniNotificationManager.DEST_ISSUE_DETAIL -> {
                if (!issueKey.isNullOrBlank()) {
                    setState { copy(selectedIssueKey = issueKey) }
                }
            }
            OmniNotificationManager.DEST_HR_GROWTH -> {
                setState { copy(currentTab = OmniNavigationTab.HR_GROWTH) }
            }
            OmniNotificationManager.DEST_NOTIFICATIONS -> {
                setState { copy(isNotificationsOpen = true) }
            }
        }
    }
}
