package amirparsa.shk.omnispace.feature.work.presentation.work

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import amirparsa.shk.omnispace.core.base.mvi.MviViewModel
import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.work.domain.model.KanbanColumnOverviewItem
import amirparsa.shk.omnispace.feature.work.domain.model.ReportsQaOverviewItem
import amirparsa.shk.omnispace.feature.work.domain.model.WikiSpaceOverviewItem
import amirparsa.shk.omnispace.feature.work.domain.model.WorkspaceIssue
import amirparsa.shk.omnispace.feature.work.domain.repository.WorkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkViewModel @Inject constructor(
    private val workRepo: WorkRepository
) : MviViewModel<WorkUiState, WorkIntent, WorkEffect>(
    initialState = WorkUiState()
) {
    private var activeProjectId: String? = null

    init {
        loadProjectsAndIssues()
    }

    private fun loadProjectsAndIssues() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            val projectsResult = workRepo.getProjects()
            if (projectsResult is NetworkResult.Success && projectsResult.data.isNotEmpty()) {
                val project = projectsResult.data.first()
                activeProjectId = project.id

                // Load workflow for status mappings
                val workflowResult = workRepo.getProjectWorkflow(project.id)
                val statusCategoryMap = mutableMapOf<String, String>()
                val statusNameMap = mutableMapOf<String, String>()
                val statuses = if (workflowResult is NetworkResult.Success) {
                    workflowResult.data.statuses.forEach { status ->
                        statusCategoryMap[status.id] = status.category
                        statusNameMap[status.id] = status.name
                    }
                    workflowResult.data.statuses
                } else emptyList()

                // Load live issues
                val issuesResult = workRepo.getIssues(project.id)
                if (issuesResult is NetworkResult.Success) {
                    val inProgress = mutableListOf<WorkspaceIssue>()
                    val todo = mutableListOf<WorkspaceIssue>()

                    issuesResult.data.forEach { issue ->
                        val category = statusCategoryMap[issue.statusId.orEmpty()] ?: "todo"
                        val mapped = WorkspaceIssue(
                            id = issue.id,
                            key = issue.key,
                            title = issue.title,
                            priorityName = when {
                                issue.priorityId?.contains("blocker") == true -> "بحرانی"
                                issue.priorityId?.contains("high") == true -> "فوری"
                                else -> "متوسط"
                            },
                            priorityColor = when {
                                issue.priorityId?.contains("blocker") == true -> 0xFFEF4444L
                                issue.priorityId?.contains("high") == true -> 0xFFF59E0BL
                                else -> 0xFF38BDF8L
                            },
                            assigneeInitials = if (!issue.assigneeId.isNullOrBlank()) "هم" else null,
                            isDone = category == "done",
                            subtasksCount = null,
                            commentsCount = null,
                            dueText = issue.dueAt?.take(10) ?: "اسپرینت جاری"
                        )

                        if (category == "in_progress") {
                            inProgress.add(mapped)
                        } else {
                            todo.add(mapped)
                        }
                    }

                    // Dynamically compute kanban overview columns from real data
                    val computedKanbanOverview = if (statuses.isNotEmpty()) {
                        statuses.map { status ->
                            val itemsForStatus = issuesResult.data
                                .filter { it.statusId == status.id }
                                .map { "${it.key}: ${it.title}" }
                            KanbanColumnOverviewItem(
                                columnTitle = status.name,
                                items = itemsForStatus
                            )
                        }
                    } else {
                        listOf(
                            KanbanColumnOverviewItem(
                                columnTitle = "در حال انجام",
                                items = inProgress.map { "${it.key}: ${it.title}" }
                            ),
                            KanbanColumnOverviewItem(
                                columnTitle = "برای انجام",
                                items = todo.map { "${it.key}: ${it.title}" }
                            )
                        )
                    }

                    // Dynamically compute QA / Reports metrics from real data
                    val totalIssues = issuesResult.data.size
                    val doneIssues = issuesResult.data.count { statusCategoryMap[it.statusId.orEmpty()] == "done" }
                    val passRateStr = if (totalIssues > 0) "${(doneIssues * 100) / totalIssues}٪" else "۰٪"
                    val computedReportsQa = ReportsQaOverviewItem(
                        velocitySp = "$totalIssues تسک",
                        velocityDiff = "پروژه ${project.name}",
                        passRate = passRateStr,
                        passRateDiff = "$doneIssues از $totalIssues انجام‌شده"
                    )

                    val allCount = inProgress.size + todo.size
                    val blockerCount = inProgress.count { it.priorityName == "بحرانی" || it.priorityName == "فوری" } +
                        todo.count { it.priorityName == "بحرانی" || it.priorityName == "فوری" }
                    val doneCount = todo.count { it.isDone } + inProgress.count { it.isDone }
                    val dynamicFilterOptions = listOf(
                        "همه ($allCount)",
                        "فوری و اولویت بالا ($blockerCount)",
                        "سررسید این هفته ($allCount)",
                        "تکمیل شده ($doneCount)"
                    )

                    setState {
                        copy(
                            activeProjectName = project.name,
                            inProgressTasks = inProgress,
                            todoTasks = todo,
                            kanbanOverview = computedKanbanOverview,
                            reportsQa = computedReportsQa,
                            filterOptions = dynamicFilterOptions,
                            isLoading = false,
                            error = null
                        )
                    }
                    return@launch
                } else {
                    val err = (issuesResult as? NetworkResult.Error)?.message ?: "خطا در دریافت تسک‌ها"
                    setState { copy(isLoading = false, error = err) }
                    return@launch
                }
            } else {
                val err = (projectsResult as? NetworkResult.Error)?.message ?: "پروژه‌ای یافت نشد"
                setState { copy(isLoading = false, error = err) }
            }
        }
    }

    override fun processIntent(intent: WorkIntent) {
        when (intent) {
            is WorkIntent.SelectSubTab -> {
                setState { copy(selectedSubTab = intent.tab) }
            }
            is WorkIntent.SelectFilter -> {
                setState { copy(selectedFilterIndex = intent.filterIndex) }
            }
            is WorkIntent.ToggleInProgressTask -> {
                setState {
                    copy(
                        inProgressTasks = inProgressTasks.map {
                            if (it.id == intent.id) it.copy(isDone = !it.isDone) else it
                        }
                    )
                }
            }
            is WorkIntent.ToggleTodoTask -> {
                setState {
                    copy(
                        todoTasks = todoTasks.map {
                            if (it.id == intent.id) it.copy(isDone = !it.isDone) else it
                        }
                    )
                }
            }
            is WorkIntent.TaskClicked -> {
                sendEffect(WorkEffect.OpenTask(intent.issue))
            }
            WorkIntent.NewTaskClicked -> {
                sendEffect(WorkEffect.OpenNewTask)
            }
            WorkIntent.OpenZephyrQaClicked -> {
                sendEffect(WorkEffect.OpenZephyrQa)
            }
            is WorkIntent.WikiClicked -> {
                sendEffect(WorkEffect.OpenWiki(intent.title))
            }
            WorkIntent.OpenInteractiveKanbanClicked -> {
                sendEffect(WorkEffect.OpenInteractiveKanban)
            }
            WorkIntent.OpenWikiTreeClicked -> {
                sendEffect(WorkEffect.OpenWikiTree)
            }
        }
    }
}
