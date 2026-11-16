package amirparsa.shk.omnispace.feature.qa.presentation

import androidx.lifecycle.viewModelScope
import amirparsa.shk.omnispace.core.base.mvi.MviViewModel
import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.qa.domain.model.TestStatus
import amirparsa.shk.omnispace.feature.qa.domain.model.ZephyrTestCase
import amirparsa.shk.omnispace.feature.work.domain.repository.WorkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * MVI ViewModel managing QA test cases, filters, status executions, and undo actions.
 */
@HiltViewModel
class ZephyrQaViewModel @Inject constructor(
    private val workRepo: WorkRepository
) : MviViewModel<ZephyrQaUiState, ZephyrQaIntent, ZephyrQaEffect>(
    initialState = ZephyrQaUiState(
        testCases = emptyList(),
        isLoading = false
    )
) {

    private var snackbarDismissJob: Job? = null

    init {
        loadTestCases()
    }

    private fun loadTestCases() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            val result = workRepo.getTests()
            if (result is NetworkResult.Success) {
                val mapped = result.data.map { dto ->
                    val status = when (dto.status?.lowercase()) {
                        "pass", "passed", "success" -> TestStatus.PASS
                        "fail", "failed" -> TestStatus.FAIL
                        "blocked" -> TestStatus.BLOCKED
                        else -> TestStatus.UNEXECUTED
                    }
                    ZephyrTestCase(
                        id = dto.id,
                        title = dto.title,
                        module = dto.module ?: "عمومی",
                        status = status,
                        stepsSummary = dto.stepsSummary ?: "",
                        executedBy = dto.executedBy,
                        executionTime = dto.executionTime,
                        errorMessage = dto.errorMessage,
                        bugIssueKey = dto.bugIssueKey
                    )
                }
                setState { copy(testCases = mapped, isLoading = false, error = null) }
            } else {
                val err = (result as? NetworkResult.Error)?.message ?: "خطا در دریافت تست‌های کیفی"
                setState { copy(isLoading = false, error = err) }
            }
        }
    }

    override fun processIntent(intent: ZephyrQaIntent) {
        when (intent) {
            is ZephyrQaIntent.SelectFilter -> setState { copy(selectedFilter = intent.filter) }
            is ZephyrQaIntent.UpdateTestStatus -> updateStatus(intent.testCase, intent.newStatus)
            ZephyrQaIntent.UndoStatusUpdate -> undoLastUpdate()
            ZephyrQaIntent.DismissUndoSnackbar -> setState { copy(showUndoSnackbar = false) }
            ZephyrQaIntent.BackClicked -> sendEffect(ZephyrQaEffect.NavigateBack)
            is ZephyrQaIntent.CreateBugClicked -> sendEffect(ZephyrQaEffect.OpenCreateBug(intent.testCase))
            ZephyrQaIntent.RefreshClicked -> loadTestCases()
        }
    }

    private fun updateStatus(testCase: ZephyrTestCase, newStatus: TestStatus) {
        val currentCases = uiState.value.testCases
        val index = currentCases.indexOfFirst { it.id == testCase.id }
        if (index != -1) {
            val oldStatus = currentCases[index].status
            val updatedCases = currentCases.toMutableList()
            updatedCases[index] = currentCases[index].copy(
                status = newStatus,
                executionTime = "الان",
                executedBy = "شما"
            )

            setState {
                copy(
                    testCases = updatedCases,
                    lastUpdatedCase = Pair(currentCases[index], oldStatus),
                    showUndoSnackbar = true
                )
            }

            snackbarDismissJob?.cancel()
            snackbarDismissJob = viewModelScope.launch {
                delay(3000)
                setState { copy(showUndoSnackbar = false) }
            }
        }
    }

    private fun undoLastUpdate() {
        val lastUpdate = uiState.value.lastUpdatedCase ?: return
        val (oldCase, prevStatus) = lastUpdate
        val currentCases = uiState.value.testCases.toMutableList()
        val index = currentCases.indexOfFirst { it.id == oldCase.id }
        if (index != -1) {
            currentCases[index] = oldCase.copy(status = prevStatus)
            setState {
                copy(
                    testCases = currentCases,
                    showUndoSnackbar = false,
                    lastUpdatedCase = null
                )
            }
        }
    }
}

