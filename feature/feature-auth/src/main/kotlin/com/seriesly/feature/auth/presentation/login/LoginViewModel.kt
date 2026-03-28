package com.seriesly.feature.auth.presentation.login

import androidx.lifecycle.viewModelScope
import com.seriesly.core.common.base.BaseViewModel
import com.seriesly.core.common.result.Result
import com.seriesly.feature.auth.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : BaseViewModel<LoginUiState, LoginIntent, LoginEvent>(LoginUiState()) {

    override fun onIntent(intent: LoginIntent) = when (intent) {
        is LoginIntent.UsernameChanged    -> setState { copy(username = intent.value, error = null) }
        is LoginIntent.PasswordChanged    -> setState { copy(password = intent.value, error = null) }
        LoginIntent.TogglePasswordVisible -> setState { copy(passwordVisible = !passwordVisible) }
        LoginIntent.LoginClicked          -> login()
        LoginIntent.NavigateToRegister    -> sendEvent(LoginEvent.NavigateToRegister)
    }

    private fun login() {
        val s = uiState.value
        if (s.username.isBlank() || s.password.isBlank()) return
        setState { copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val r = loginUseCase(s.username, s.password)) {
                is Result.Success -> sendEvent(LoginEvent.NavigateToHome)
                is Result.Error   -> setState { copy(isLoading = false, error = r.exception.message) }
                else              -> setState { copy(isLoading = false) }
            }
        }
    }
}
