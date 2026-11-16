package amirparsa.shk.omnispace.feature.qa.presentation

import amirparsa.shk.omnispace.core.base.mvi.UiEffect
import amirparsa.shk.omnispace.core.base.mvi.UiIntent
import amirparsa.shk.omnispace.core.base.mvi.UiState
import amirparsa.shk.omnispace.feature.qa.domain.model.TestStatus
import amirparsa.shk.omnispace.feature.qa.domain.model.ZephyrTestCase
import androidx.compose.ui.graphics.Color

val TestStatus.color: Color get() = Color(this.colorHex)
val TestStatus.bgColor: Color get() = Color(this.colorHex).copy(alpha = 0.15f)

/**
 * Filter categories for QA test cases.
 * Belonging to Presentation Layer (NOT Domain).
 */
enum class QaFilter(val titleFa: String) {
    ALL("همه"),
    FAIL("شکست‌خورده"),
    UNEXECUTED("نیازمند اجرا"),
    PASS("پاس‌شده")
}

/**
 * Immutable UI State for Zephyr QA screen.
 */
data class ZephyrQaUiState(
    val cycleName: String = "اسپرینت ۲۴ - آزمون‌های کیفی",
    val selectedFilter: QaFilter = QaFilter.ALL,
    val testCases: List<ZephyrTestCase> = emptyList(),
    val showUndoSnackbar: Boolean = false,
    val lastUpdatedCase: Pair<ZephyrTestCase, TestStatus>? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) : UiState {
    val totalCount: Int get() = testCases.size
    val passCount: Int get() = testCases.count { it.status == TestStatus.PASS }
    val failCount: Int get() = testCases.count { it.status == TestStatus.FAIL }
    val unexecutedCount: Int get() = testCases.count { it.status == TestStatus.UNEXECUTED }
    val blockedCount: Int get() = testCases.count { it.status == TestStatus.BLOCKED }
    val passPercentage: Int get() = if (totalCount > 0) (passCount * 100 / totalCount) else 0

    val filteredCases: List<ZephyrTestCase>
        get() = when (selectedFilter) {
            QaFilter.ALL -> testCases
            QaFilter.FAIL -> testCases.filter { it.status == TestStatus.FAIL }
            QaFilter.UNEXECUTED -> testCases.filter { it.status == TestStatus.UNEXECUTED || it.status == TestStatus.BLOCKED }
            QaFilter.PASS -> testCases.filter { it.status == TestStatus.PASS }
        }
}

/**
 * User Intents for Zephyr QA.
 */
sealed interface ZephyrQaIntent : UiIntent {
    data class SelectFilter(val filter: QaFilter) : ZephyrQaIntent
    data class UpdateTestStatus(val testCase: ZephyrTestCase, val newStatus: TestStatus) : ZephyrQaIntent
    data object UndoStatusUpdate : ZephyrQaIntent
    data object DismissUndoSnackbar : ZephyrQaIntent
    data object BackClicked : ZephyrQaIntent
    data class CreateBugClicked(val testCase: ZephyrTestCase) : ZephyrQaIntent
    data object RefreshClicked : ZephyrQaIntent
}

/**
 * Side Effects for Zephyr QA.
 */
sealed interface ZephyrQaEffect : UiEffect {
    data object NavigateBack : ZephyrQaEffect
    data class OpenCreateBug(val testCase: ZephyrTestCase) : ZephyrQaEffect
}

