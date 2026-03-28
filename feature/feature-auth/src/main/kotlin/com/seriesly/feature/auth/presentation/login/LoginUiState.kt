package com.seriesly.feature.auth.presentation.login

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface LoginIntent {
    data class UsernameChanged(val value: String) : LoginIntent
    data class PasswordChanged(val value: String) : LoginIntent
    object TogglePasswordVisible : LoginIntent
    object LoginClicked : LoginIntent
    object NavigateToRegister : LoginIntent
}

sealed interface LoginEvent {
    object NavigateToHome : LoginEvent
    object NavigateToRegister : LoginEvent
}
