package amirparsa.shk.omnispace.feature.work.presentation.work

import amirparsa.shk.omnispace.core.base.mvi.UiEffect
import amirparsa.shk.omnispace.core.base.mvi.UiIntent
import amirparsa.shk.omnispace.core.base.mvi.UiState
import amirparsa.shk.omnispace.feature.work.domain.model.KanbanColumnOverviewItem
import amirparsa.shk.omnispace.feature.work.domain.model.ReportsQaOverviewItem
import amirparsa.shk.omnispace.feature.work.domain.model.WikiSpaceOverviewItem
import amirparsa.shk.omnispace.feature.work.domain.model.WorkspaceIssue

/**
 * UI sub-tabs for Workspace navigation.
 * Belonging to Presentation Layer (NOT Domain).
 */
enum class WorkSubTab(val titleFa: String, val count: Int? = null) {
    MY_TASKS("کارهای من"),
    PROJECTS_BOARD("پروژه‌ها و بُرد"),
    DOCS_WIKI("اسناد و ویکی"),
    REPORTS_QA("گزارش‌ها و QA")
}

/**
 * Immutable UI State for Workspace screen.
 */
data class WorkUiState(
    val selectedSubTab: WorkSubTab = WorkSubTab.MY_TASKS,
    val selectedFilterIndex: Int = 0,
    val filterOptions: List<String> = listOf(
        "همه",
        "فوری و اولویت بالا",
        "سررسید این هفته",
        "تکمیل شده"
    ),
    val activeProjectName: String = "",
    val inProgressTasks: List<WorkspaceIssue> = emptyList(),
    val todoTasks: List<WorkspaceIssue> = emptyList(),
    val kanbanOverview: List<KanbanColumnOverviewItem> = emptyList(),
    val wikiSpaces: List<WikiSpaceOverviewItem> = emptyList(),
    val reportsQa: ReportsQaOverviewItem = ReportsQaOverviewItem(),
    val isLoading: Boolean = false,
    val error: String? = null
) : UiState

/**
 * User Intents for Workspace screen.
 */
sealed interface WorkIntent : UiIntent {
    data class SelectSubTab(val tab: WorkSubTab) : WorkIntent
    data class SelectFilter(val filterIndex: Int) : WorkIntent
    data class ToggleInProgressTask(val id: String) : WorkIntent
    data class ToggleTodoTask(val id: String) : WorkIntent
    data class TaskClicked(val issue: WorkspaceIssue) : WorkIntent

    data object NewTaskClicked : WorkIntent
    data object OpenZephyrQaClicked : WorkIntent
    data class WikiClicked(val title: String) : WorkIntent
    data object OpenInteractiveKanbanClicked : WorkIntent
    data object OpenWikiTreeClicked : WorkIntent
}

/**
 * Side effects for Workspace screen.
 */
sealed interface WorkEffect : UiEffect {
    data class OpenTask(val issue: WorkspaceIssue) : WorkEffect
    data object OpenNewTask : WorkEffect
    data object OpenZephyrQa : WorkEffect
    data class OpenWiki(val title: String) : WorkEffect
    data object OpenInteractiveKanban : WorkEffect
    data object OpenWikiTree : WorkEffect
    data class ShowToast(val message: String) : WorkEffect
}

