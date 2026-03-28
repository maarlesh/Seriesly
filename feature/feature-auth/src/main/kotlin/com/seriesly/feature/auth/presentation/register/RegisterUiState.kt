package com.seriesly.feature.auth.presentation.register

data class RegisterUiState(
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,
    val confirmVisible: Boolean = false,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val confirmError: String? = null,
    val isLoading: Boolean = false,
    val generalError: String? = null
)

sealed interface RegisterIntent {
    data class UsernameChanged(val value: String) : RegisterIntent
    data class PasswordChanged(val value: String) : RegisterIntent
    data class ConfirmChanged(val value: String) : RegisterIntent
    object TogglePasswordVisible : RegisterIntent
    object ToggleConfirmVisible : RegisterIntent
    object RegisterClicked : RegisterIntent
    object NavigateToLogin : RegisterIntent
}

sealed interface RegisterEvent {
    object NavigateToHome : RegisterEvent
    object NavigateToLogin : RegisterEvent
}
