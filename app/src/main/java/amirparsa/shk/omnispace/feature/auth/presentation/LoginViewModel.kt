package amirparsa.shk.omnispace.feature.auth.presentation

import androidx.lifecycle.viewModelScope
import amirparsa.shk.omnispace.core.base.mvi.MviViewModel
import amirparsa.shk.omnispace.core.network.AuthRepository
import amirparsa.shk.omnispace.core.network.SessionManager
import amirparsa.shk.omnispace.core.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * MVI ViewModel managing authentication, credentials, token login, and session persistence.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val sessionManager: SessionManager
) : MviViewModel<LoginUiState, LoginIntent, LoginEffect>(
    initialState = LoginUiState()
) {

    override fun processIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EmailChanged -> setState { copy(email = intent.email, errorMessage = null) }
            is LoginIntent.PasswordChanged -> setState { copy(password = intent.password, errorMessage = null) }
            LoginIntent.TogglePasswordVisibility -> setState { copy(isPasswordVisible = !isPasswordVisible) }
            LoginIntent.DismissError -> setState { copy(errorMessage = null) }
            LoginIntent.OpenHelpDialog -> setState { copy(showHelpDialog = true) }
            LoginIntent.CloseHelpDialog -> setState { copy(showHelpDialog = false) }
            LoginIntent.OpenTokenDialog -> setState { copy(showTokenDialog = true, tokenInput = "") }
            LoginIntent.CloseTokenDialog -> setState { copy(showTokenDialog = false) }
            is LoginIntent.TokenInputChanged -> setState { copy(tokenInput = intent.token) }
            is LoginIntent.SelectLanguage -> setState { copy(selectedLanguage = intent.lang) }

            LoginIntent.ForgotPasswordClicked -> {
                sendEffect(LoginEffect.ShowToast("جهت بازیابی رمز عبور، با راهبر سامانه تماس بگیرید"))
            }

            LoginIntent.SubmitLogin -> {
                val email = currentState.email.trim()
                val password = currentState.password.trim()

                if (email.isBlank()) {
                    setState { copy(errorMessage = "لطفاً ایمیل سازمانی را وارد نمایید") }
                    return
                }
                if (password.isBlank()) {
                    setState { copy(errorMessage = "لطفاً رمز عبور را وارد نمایید") }
                    return
                }

                setState { copy(isLoading = true, errorMessage = null) }

                viewModelScope.launch {
                    val result = authRepo.login(email, password)
                    when (result) {
                        is NetworkResult.Success -> {
                            setState { copy(isLoading = false) }
                            sendEffect(LoginEffect.LoginSuccess)
                        }
                        is NetworkResult.Error -> {
                            setState {
                                copy(
                                    isLoading = false,
                                    errorMessage = result.message.ifBlank { "اطلاعات ورود نادرست است." }
                                )
                            }
                        }
                        is NetworkResult.Loading -> Unit
                    }
                }
            }

            LoginIntent.SubmitToken -> {
                val token = currentState.tokenInput.trim()
                if (token.isBlank()) {
                    sendEffect(LoginEffect.ShowToast("توکن نمی‌تواند خالی باشد"))
                    return
                }

                setState { copy(isLoading = true, showTokenDialog = false) }

                viewModelScope.launch {
                    sessionManager.authToken = token
                    sessionManager.csrfToken = null
                    sessionManager.isAuthenticated = true

                    val profileResult = authRepo.getProfile()
                    if (profileResult is NetworkResult.Success) {
                        setState { copy(isLoading = false) }
                        sendEffect(LoginEffect.LoginSuccess)
                    } else {
                        val sessionResult = authRepo.checkSession()
                        if (sessionResult is NetworkResult.Success && sessionResult.data.authenticated) {
                            setState { copy(isLoading = false) }
                            sendEffect(LoginEffect.LoginSuccess)
                        } else {
                            sessionManager.clearSession()
                            setState {
                                copy(
                                    isLoading = false,
                                    errorMessage = "توکن وارد شده نامعتبر یا منقضی است"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
