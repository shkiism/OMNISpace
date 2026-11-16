package amirparsa.shk.omnispace.feature.auth.presentation

import amirparsa.shk.omnispace.core.base.mvi.UiEffect
import amirparsa.shk.omnispace.core.base.mvi.UiIntent
import amirparsa.shk.omnispace.core.base.mvi.UiState

/**
 * Immutable UI State for Login screen matching Stitch RTL design.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showTokenDialog: Boolean = false,
    val tokenInput: String = "",
    val selectedLanguage: String = "fa",
    val showHelpDialog: Boolean = false
) : UiState

/**
 * User Intents for Login screen.
 */
sealed interface LoginIntent : UiIntent {
    data class EmailChanged(val email: String) : LoginIntent
    data class PasswordChanged(val password: String) : LoginIntent
    data object TogglePasswordVisibility : LoginIntent
    data object SubmitLogin : LoginIntent
    data object OpenTokenDialog : LoginIntent
    data object CloseTokenDialog : LoginIntent
    data class TokenInputChanged(val token: String) : LoginIntent
    data object SubmitToken : LoginIntent
    data class SelectLanguage(val lang: String) : LoginIntent
    data object DismissError : LoginIntent
    data object OpenHelpDialog : LoginIntent
    data object CloseHelpDialog : LoginIntent
    data object ForgotPasswordClicked : LoginIntent
}

/**
 * One-shot Side Effects for Login screen.
 */
sealed interface LoginEffect : UiEffect {
    data object LoginSuccess : LoginEffect
    data class ShowToast(val message: String) : LoginEffect
}

