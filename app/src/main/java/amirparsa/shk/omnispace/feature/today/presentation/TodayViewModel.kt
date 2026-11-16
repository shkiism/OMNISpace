package amirparsa.shk.omnispace.feature.today.presentation

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import amirparsa.shk.omnispace.core.base.mvi.MviViewModel
import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.today.domain.model.HabitItem
import amirparsa.shk.omnispace.feature.today.domain.model.TodayTaskItem
import amirparsa.shk.omnispace.feature.today.domain.repository.TodayRepository
import amirparsa.shk.omnispace.feature.work.domain.repository.WorkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodayViewModel @Inject constructor(
    private val todayRepo: TodayRepository,
    private val workRepo: WorkRepository
) : MviViewModel<TodayUiState, TodayIntent, TodayEffect>(
    initialState = TodayUiState(
        isLoading = true,
        habits = emptyList(),
        tasks = emptyList()
    )
) {

    init {
        loadDataFromNetwork()
        triggerSync()
    }

    private fun triggerSync() {
        viewModelScope.launch {
            setState { copy(isSyncing = true, syncStatusText = "در حال همگام‌سازی...") }
            val syncResult = todayRepo.syncPrivateZone()
            if (syncResult is NetworkResult.Success) {
                setState {
                    copy(
                        isSyncing = false,
                        syncStatusText = "همگام‌سازی شده",
                        lastSyncTime = "هم‌اکنون"
                    )
                }
                // Refresh notes from local Room cache after pull
                val notesRes = todayRepo.getNotes()
                if (notesRes is NetworkResult.Success) {
                    setState { copy(notes = notesRes.data) }
                }
            } else {
                setState {
                    copy(
                        isSyncing = false,
                        syncStatusText = "آفلاین (صف محلی)"
                    )
                }
            }
        }
    }

    private fun loadDataFromNetwork() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            // 0. Load upcoming meeting from DataSource
            val meeting = todayRepo.getUpcomingMeeting()
            setState { copy(upcomingMeeting = meeting) }

            // 1. Load habits
            val habitsResult = todayRepo.getHabits()
            if (habitsResult is NetworkResult.Success) {
                setState { copy(habits = habitsResult.data) }
            }

            // 2. Load notes from /private/v1/notes
            val notesResult = todayRepo.getNotes()
            if (notesResult is NetworkResult.Success) {
                setState { copy(notes = notesResult.data) }
            }

            // 3. Load today's active tasks/issues from live backend
            val projectsResult = workRepo.getProjects()
            if (projectsResult is NetworkResult.Success && projectsResult.data.isNotEmpty()) {
                val firstProject = projectsResult.data.first()
                val issuesResult = workRepo.getIssues(firstProject.id)
                if (issuesResult is NetworkResult.Success) {
                    val mappedTasks = issuesResult.data.take(6).map { issue ->
                        TodayTaskItem(
                            id = issue.id,
                            title = issue.title,
                            projectKey = issue.key,
                            subtitle = issue.dueAt?.take(10) ?: "اسپرینت جاری",
                            isDone = false,
                            priorityColor = when {
                                issue.priorityId?.contains("blocker") == true -> 0xFFEF4444L
                                issue.priorityId?.contains("high") == true -> 0xFFF59E0BL
                                else -> 0xFF10B981L
                            },
                            avatarInitials = if (!issue.assigneeId.isNullOrBlank()) "هم" else "ام"
                        )
                    }
                    setState { copy(tasks = mappedTasks) }
                }
            }
            setState { copy(isLoading = false) }
        }
    }

    override fun processIntent(intent: TodayIntent) {
        when (intent) {
            TodayIntent.CheckIn -> {
                val currentTime = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
                setState { copy(isCheckedIn = true, checkInTime = currentTime, elapsedTime = "شروع به کار") }
                sendEffect(TodayEffect.ShowToast("ورود با موفقیت ثبت شد"))
            }
            TodayIntent.CheckOut -> {
                setState { copy(isCheckedIn = false, elapsedTime = "پایان شیفت") }
                sendEffect(TodayEffect.ShowToast("خروج با موفقیت ثبت شد"))
            }
            is TodayIntent.ChangeWorkType -> {
                setState { copy(selectedWorkType = intent.workType) }
            }
            TodayIntent.TakeBreak -> {
                sendEffect(TodayEffect.ShowToast("استراحت آغاز شد"))
            }
            is TodayIntent.ToggleHabit -> {
                val targetHabit = currentState.habits.find { it.id == intent.id }
                val newDone = !(targetHabit?.isDone ?: false)
                // Optimistic UI update
                setState {
                    copy(
                        habits = habits.map {
                            if (it.id == intent.id) it.copy(isDone = newDone) else it
                        }
                    )
                }
                viewModelScope.launch {
                    val result = todayRepo.toggleHabit(intent.id, newDone)
                    if (result !is NetworkResult.Success) {
                        // Revert if failed
                        setState {
                            copy(
                                habits = habits.map {
                                    if (it.id == intent.id) it.copy(isDone = !newDone) else it
                                }
                            )
                        }
                        sendEffect(TodayEffect.ShowToast("خطا در همگام‌سازی وضعیت عادت"))
                    }
                }
            }
            is TodayIntent.AddHabit -> {
                setState { copy(isHabitCreationOpen = false) }
                sendEffect(TodayEffect.ShowToast("در حال ایجاد عادت «${intent.title}»..."))
                viewModelScope.launch {
                    val result = todayRepo.createHabit(intent.title)
                    if (result is NetworkResult.Success) {
                        sendEffect(TodayEffect.ShowToast("عادت «${intent.title}» با موفقیت ثبت شد"))
                        loadDataFromNetwork()
                    } else {
                        // Local fallback
                        val newHabit = HabitItem(
                            id = (currentState.habits.size + 1).toString(),
                            title = intent.title,
                            isDone = false
                        )
                        setState { copy(habits = habits + newHabit) }
                    }
                }
            }
            is TodayIntent.StartFocus -> {
                viewModelScope.launch {
                    todayRepo.startFocus(intent.minutes)
                }
            }
            is TodayIntent.StopFocus -> {
                viewModelScope.launch {
                    val result = todayRepo.stopFocus()
                    if (result is NetworkResult.Success) {
                        sendEffect(TodayEffect.ShowToast("دقایق فوکوس (${intent.loggedMinutes} دقیقه) در سرور ثبت شد"))
                    } else {
                        sendEffect(TodayEffect.ShowToast("تایمر فوکوس متوقف شد (${intent.loggedMinutes} دقیقه)"))
                    }
                }
            }
            is TodayIntent.ToggleTask -> {
                val currentTask = currentState.tasks.find { it.id == intent.id }
                val newDone = !(currentTask?.isDone ?: false)
                setState {
                    copy(
                        tasks = tasks.map {
                            if (it.id == intent.id) it.copy(isDone = newDone) else it
                        }
                    )
                }
                viewModelScope.launch {
                    todayRepo.toggleTask(intent.id, newDone)
                }
            }
            is TodayIntent.TaskClicked -> {
                sendEffect(TodayEffect.OpenTask(intent.projectKey))
            }
            is TodayIntent.UpdateQuickNote -> {
                setState { copy(quickNoteText = intent.text) }
            }
            TodayIntent.SaveQuickNote -> {
                if (currentState.quickNoteText.isNotBlank()) {
                    sendEffect(TodayEffect.ShowToast("یادداشت با موفقیت ذخیره شد"))
                    setState { copy(quickNoteText = "") }
                }
            }
            TodayIntent.OpenHabitCreation -> {
                setState { copy(isHabitCreationOpen = true) }
            }
            TodayIntent.CloseHabitCreation -> {
                setState { copy(isHabitCreationOpen = false) }
            }
            TodayIntent.OpenNotesArchive -> {
                setState { copy(isNotesArchiveOpen = true) }
                viewModelScope.launch {
                    val notesRes = todayRepo.getNotes()
                    if (notesRes is NetworkResult.Success) {
                        setState { copy(notes = notesRes.data) }
                    }
                }
            }
            TodayIntent.CloseNotesArchive -> {
                setState { copy(isNotesArchiveOpen = false) }
            }
            TodayIntent.LoadNotes -> {
                viewModelScope.launch {
                    val notesRes = todayRepo.getNotes()
                    if (notesRes is NetworkResult.Success) {
                        setState { copy(notes = notesRes.data) }
                    }
                }
            }
            is TodayIntent.SaveNote -> {
                viewModelScope.launch {
                    if (intent.id.isNullOrBlank()) {
                        // Create
                        val result = todayRepo.createNote(intent.title, intent.content)
                        if (result is NetworkResult.Success) {
                            setState { copy(notes = listOf(result.data) + notes.filter { it.id != result.data.id }) }
                            sendEffect(TodayEffect.ShowToast("یادداشت با موفقیت ذخیره شد"))
                        }
                    } else {
                        // Update
                        val currentNote = currentState.notes.find { it.id == intent.id }
                        val isPinned = currentNote?.isPinned ?: false
                        todayRepo.updateNote(intent.id, intent.title, intent.content, isPinned)
                        setState {
                            copy(
                                notes = notes.map {
                                    if (it.id == intent.id) it.copy(title = intent.title, content = intent.content, updatedAt = "هم‌اکنون") else it
                                }
                            )
                        }
                        sendEffect(TodayEffect.ShowToast("یادداشت بروزرسانی شد"))
                    }
                }
            }
            is TodayIntent.TogglePinNote -> {
                val current = currentState.notes.find { it.id == intent.id }
                if (current != null) {
                    val newPinned = !current.isPinned
                    setState {
                        copy(
                            notes = notes.map {
                                if (it.id == intent.id) it.copy(isPinned = newPinned) else it
                            }
                        )
                    }
                    viewModelScope.launch {
                        todayRepo.togglePinNote(intent.id, newPinned)
                    }
                }
            }
            is TodayIntent.DeleteNote -> {
                setState {
                    copy(notes = notes.filter { it.id != intent.id })
                }
                viewModelScope.launch {
                    todayRepo.deleteNote(intent.id)
                    sendEffect(TodayEffect.ShowToast("یادداشت حذف شد"))
                }
            }
            TodayIntent.NavigateToWorkspaceClicked -> {
                sendEffect(TodayEffect.NavigateToWorkspace)
            }
            TodayIntent.JoinMeetingClicked -> {
                sendEffect(TodayEffect.JoinMeeting)
            }
            TodayIntent.OpenMeetingAgendaClicked -> {
                setState { copy(isMeetingAgendaOpen = true) }
            }
            TodayIntent.CloseMeetingAgenda -> {
                setState { copy(isMeetingAgendaOpen = false) }
            }
            is TodayIntent.AddMeetingToCalendarClicked -> {
                sendEffect(TodayEffect.LaunchSystemCalendar(intent.meeting))
                sendEffect(TodayEffect.ShowToast("در حال انتقال به تقویم سیستم..."))
            }
            TodayIntent.OpenSystemNotificationShadeClicked -> {
                sendEffect(TodayEffect.OpenSystemNotificationShade)
            }
            TodayIntent.TriggerSync -> {
                triggerSync()
            }
            TodayIntent.RefreshToday -> {
                loadDataFromNetwork()
                triggerSync()
            }
        }
    }
}
