package com.seriesly.feature.profile.presentation

import androidx.lifecycle.viewModelScope
import com.seriesly.core.common.base.BaseViewModel
import com.seriesly.core.common.result.Result
import com.seriesly.core.domain.repository.SyncRepository
import com.seriesly.core.security.session.SessionManager
import com.seriesly.feature.profile.domain.GetUserStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getStats: GetUserStatsUseCase,
    private val sessionManager: SessionManager,
    private val syncRepository: SyncRepository,
) : BaseViewModel<ProfileUiState, ProfileIntent, ProfileEvent>(ProfileUiState()) {

    init {
        // Collect stats reactively — updates whenever watchlists or ratings change in Room
        viewModelScope.launch {
            getStats().collect { result ->
                when (result) {
                    is Result.Success -> setState { copy(stats = result.data, isLoading = false) }
                    is Result.Error   -> setState { copy(isLoading = false) }
                    else              -> {}
                }
            }
        }

        val isFirstPull = sessionManager.getLastPullAt() == 0L
        if (isFirstPull) setState { copy(isSyncing = true) }

        viewModelScope.launch {
            runCatching { syncRepository.pullAll() }
            if (isFirstPull) setState { copy(isSyncing = false) }
            // No manual stats reload needed — the Flow collector above picks up changes automatically
        }
    }

    override fun onIntent(intent: ProfileIntent) = when (intent) {
        ProfileIntent.LogoutClicked    -> setState { copy(showLogoutDialog = true) }
        ProfileIntent.LogoutDismissed  -> setState { copy(showLogoutDialog = false) }
        ProfileIntent.LogoutConfirmed  -> logout()
        ProfileIntent.MyRatingsClicked -> sendEvent(ProfileEvent.NavigateToMyRatings)
    }

    private fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            sendEvent(ProfileEvent.NavigateToLogin)
        }
    }
}
