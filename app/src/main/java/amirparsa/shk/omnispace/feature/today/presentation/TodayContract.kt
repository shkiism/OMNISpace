package amirparsa.shk.omnispace.feature.today.presentation

import amirparsa.shk.omnispace.core.base.mvi.UiEffect
import amirparsa.shk.omnispace.core.base.mvi.UiIntent
import amirparsa.shk.omnispace.core.base.mvi.UiState
import amirparsa.shk.omnispace.core.designsystem.component.WorkType
import amirparsa.shk.omnispace.feature.today.domain.model.HabitItem
import amirparsa.shk.omnispace.feature.today.domain.model.NoteModel
import amirparsa.shk.omnispace.feature.today.domain.model.TodayTaskItem
import amirparsa.shk.omnispace.feature.today.domain.model.UpcomingMeetingModel

/**
 * Immutable UI State for Today screen.
 */
data class TodayUiState(
    val isCheckedIn: Boolean = false,
    val selectedWorkType: WorkType = WorkType.ON_SITE,
    val checkInTime: String? = null,
    val elapsedTime: String? = null,
    val completionPercentage: Int = 0,
    val habits: List<HabitItem> = emptyList(),
    val tasks: List<TodayTaskItem> = emptyList(),
    val notes: List<NoteModel> = emptyList(),
    val upcomingMeeting: UpcomingMeetingModel? = null,
    val quickNoteText: String = "",
    val isHabitCreationOpen: Boolean = false,
    val isNotesArchiveOpen: Boolean = false,
    val isMeetingAgendaOpen: Boolean = false,
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val syncStatusText: String = "همگام‌سازی شده",
    val lastSyncTime: String = "هم‌اکنون"
) : UiState {
    val completedHabitsCount: Int
        get() = habits.count { it.isDone }

    val completedTasksCount: Int
        get() = tasks.count { it.isDone }
}

/**
 * User Intents for Today screen.
 */
sealed interface TodayIntent : UiIntent {
    data object CheckIn : TodayIntent
    data object CheckOut : TodayIntent
    data class ChangeWorkType(val workType: WorkType) : TodayIntent
    data object TakeBreak : TodayIntent

    data class ToggleHabit(val id: String) : TodayIntent
    data class AddHabit(val title: String) : TodayIntent

    data class StartFocus(val minutes: Int) : TodayIntent
    data class StopFocus(val loggedMinutes: Int) : TodayIntent

    data class ToggleTask(val id: String) : TodayIntent
    data class TaskClicked(val projectKey: String) : TodayIntent

    data class UpdateQuickNote(val text: String) : TodayIntent
    data object SaveQuickNote : TodayIntent

    data object OpenHabitCreation : TodayIntent
    data object CloseHabitCreation : TodayIntent

    data object OpenNotesArchive : TodayIntent
    data object CloseNotesArchive : TodayIntent

    data object LoadNotes : TodayIntent
    data class SaveNote(val id: String?, val title: String, val content: String) : TodayIntent
    data class TogglePinNote(val id: String) : TodayIntent
    data class DeleteNote(val id: String) : TodayIntent

    data object NavigateToWorkspaceClicked : TodayIntent
    data object JoinMeetingClicked : TodayIntent
    data object OpenMeetingAgendaClicked : TodayIntent
    data object CloseMeetingAgenda : TodayIntent
    data class AddMeetingToCalendarClicked(val meeting: UpcomingMeetingModel) : TodayIntent
    data object OpenSystemNotificationShadeClicked : TodayIntent

    data object TriggerSync : TodayIntent
    data object RefreshToday : TodayIntent
}

/**
 * Single-shot UI Effects for Today screen.
 */
sealed interface TodayEffect : UiEffect {
    data object NavigateToWorkspace : TodayEffect
    data class OpenTask(val issueKey: String) : TodayEffect
    data object JoinMeeting : TodayEffect
    data class LaunchSystemCalendar(val meeting: UpcomingMeetingModel) : TodayEffect
    data object OpenSystemNotificationShade : TodayEffect
    data class ShowToast(val message: String) : TodayEffect
}
