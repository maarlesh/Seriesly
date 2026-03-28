package com.seriesly.feature.profile.presentation

import com.seriesly.feature.profile.domain.UserStats

data class ProfileUiState(
    val stats: UserStats? = null,
    val isLoading: Boolean = true,
    val showLogoutDialog: Boolean = false
)

sealed interface ProfileIntent {
    object LogoutClicked : ProfileIntent
    object LogoutConfirmed : ProfileIntent
    object LogoutDismissed : ProfileIntent
    object MyRatingsClicked : ProfileIntent
}

sealed interface ProfileEvent {
    object NavigateToLogin : ProfileEvent
    object NavigateToMyRatings : ProfileEvent
}
