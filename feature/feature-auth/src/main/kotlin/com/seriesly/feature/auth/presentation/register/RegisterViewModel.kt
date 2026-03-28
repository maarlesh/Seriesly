package com.seriesly.feature.auth.presentation.register

import androidx.lifecycle.viewModelScope
import com.seriesly.core.common.base.BaseViewModel
import com.seriesly.core.common.result.AppException
import com.seriesly.core.common.result.Result
import com.seriesly.feature.auth.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : BaseViewModel<RegisterUiState, RegisterIntent, RegisterEvent>(RegisterUiState()) {

    override fun onIntent(intent: RegisterIntent) = when (intent) {
        is RegisterIntent.UsernameChanged    -> setState { copy(username = intent.value, usernameError = null, generalError = null) }
        is RegisterIntent.PasswordChanged    -> setState { copy(password = intent.value, passwordError = null, generalError = null) }
        is RegisterIntent.ConfirmChanged     -> setState { copy(confirmPassword = intent.value, confirmError = null, generalError = null) }
        RegisterIntent.TogglePasswordVisible -> setState { copy(passwordVisible = !passwordVisible) }
        RegisterIntent.ToggleConfirmVisible  -> setState { copy(confirmVisible = !confirmVisible) }
        RegisterIntent.RegisterClicked       -> register()
        RegisterIntent.NavigateToLogin       -> sendEvent(RegisterEvent.NavigateToLogin)
    }

    private fun register() {
        val s = uiState.value
        setState { copy(isLoading = true, usernameError = null, passwordError = null, confirmError = null, generalError = null) }
        viewModelScope.launch {
            when (val r = registerUseCase(s.username, s.password, s.confirmPassword)) {
                is Result.Success -> sendEvent(RegisterEvent.NavigateToHome)
                is Result.Error   -> handleError(r.exception)
                else              -> setState { copy(isLoading = false) }
            }
        }
    }

    private fun handleError(e: AppException) {
        val msg = e.message ?: "An error occurred"
        when {
            msg.contains("Username", ignoreCase = true) ->
                setState { copy(isLoading = false, usernameError = msg) }
            msg.contains("Password", ignoreCase = true) && !msg.contains("match", ignoreCase = true) ->
                setState { copy(isLoading = false, passwordError = msg) }
            msg.contains("match", ignoreCase = true) ->
                setState { copy(isLoading = false, confirmError = msg) }
            else ->
                setState { copy(isLoading = false, generalError = msg) }
        }
    }
}
