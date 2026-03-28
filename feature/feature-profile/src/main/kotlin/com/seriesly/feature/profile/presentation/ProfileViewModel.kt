package com.seriesly.feature.profile.presentation

import androidx.lifecycle.viewModelScope
import com.seriesly.core.common.base.BaseViewModel
import com.seriesly.core.common.result.Result
import com.seriesly.core.security.session.SessionManager
import com.seriesly.feature.profile.domain.GetUserStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getStats: GetUserStatsUseCase,
    private val sessionManager: SessionManager
) : BaseViewModel<ProfileUiState, ProfileIntent, ProfileEvent>(ProfileUiState()) {

    init {
        viewModelScope.launch {
            when (val r = getStats()) {
                is Result.Success -> setState { copy(stats = r.data, isLoading = false) }
                is Result.Error   -> setState { copy(isLoading = false) }
                else              -> {}
            }
        }
    }

    override fun onIntent(intent: ProfileIntent) = when (intent) {
        ProfileIntent.LogoutClicked   -> setState { copy(showLogoutDialog = true) }
        ProfileIntent.LogoutDismissed -> setState { copy(showLogoutDialog = false) }
        ProfileIntent.LogoutConfirmed -> logout()
        ProfileIntent.MyRatingsClicked -> sendEvent(ProfileEvent.NavigateToMyRatings)
    }

    private fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            sendEvent(ProfileEvent.NavigateToLogin)
        }
    }
}
