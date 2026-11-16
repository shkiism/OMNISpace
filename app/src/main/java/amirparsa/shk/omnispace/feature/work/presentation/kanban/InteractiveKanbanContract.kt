package amirparsa.shk.omnispace.feature.work.presentation.kanban

import amirparsa.shk.omnispace.core.base.mvi.UiEffect
import amirparsa.shk.omnispace.core.base.mvi.UiIntent
import amirparsa.shk.omnispace.core.base.mvi.UiState
import amirparsa.shk.omnispace.feature.work.domain.model.KanbanCardItem
import amirparsa.shk.omnispace.feature.work.domain.model.KanbanColumnStatus
import androidx.compose.ui.graphics.Color

val KanbanColumnStatus.color: Color get() = Color(this.colorHex)

/**
 * Immutable UI State for Interactive Kanban board.
 */
data class InteractiveKanbanUiState(
    val projectName: String = "بُرد کانبان",
    val cards: List<KanbanCardItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) : UiState

/**
 * User Intents for Interactive Kanban board.
 */
sealed interface InteractiveKanbanIntent : UiIntent {
    data object BackClicked : InteractiveKanbanIntent
    data object NewTaskClicked : InteractiveKanbanIntent
    data object Refresh : InteractiveKanbanIntent
    data class TaskClicked(val key: String) : InteractiveKanbanIntent
    data class MoveCard(val cardId: String, val newStatus: KanbanColumnStatus) : InteractiveKanbanIntent
    data class TransitionCard(val cardId: String, val transitionId: String) : InteractiveKanbanIntent
}

/**
 * Side effects for Interactive Kanban board.
 */
sealed interface InteractiveKanbanEffect : UiEffect {
    data object NavigateBack : InteractiveKanbanEffect
    data object OpenNewTask : InteractiveKanbanEffect
    data class OpenTask(val key: String) : InteractiveKanbanEffect
    data class ShowToast(val message: String) : InteractiveKanbanEffect
}

