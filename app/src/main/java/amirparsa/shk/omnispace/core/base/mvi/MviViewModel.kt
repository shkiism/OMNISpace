package amirparsa.shk.omnispace.core.base.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base ViewModel for MVI (Model-View-Intent) architecture.
 *
 * @param State The immutable state class representing the current UI state.
 * @param Intent The actions or events produced by the UI.
 * @param Effect The single-time side effects (navigation, alerts, toasts).
 */
abstract class MviViewModel<State : UiState, Intent : UiIntent, Effect : UiEffect>(
    initialState: State
) : ViewModel() {

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect: Flow<Effect> = _effect.receiveAsFlow()

    val currentState: State
        get() = _uiState.value

    /**
     * Entry point for UI to send intents to the ViewModel.
     */
    fun onIntent(intent: Intent) {
        processIntent(intent)
    }

    /**
     * Subclasses implement this method to handle specific user intents.
     */
    protected abstract fun processIntent(intent: Intent)

    /**
     * Updates the UI state atomically using a reducer function.
     */
    protected fun setState(reducer: State.() -> State) {
        _uiState.update { it.reducer() }
    }

    /**
     * Emits a one-off side effect to be consumed by the UI.
     */
    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}

