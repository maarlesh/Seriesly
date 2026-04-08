package com.seriesly.feature.progress.presentation.history

import androidx.lifecycle.viewModelScope
import com.seriesly.core.common.base.BaseViewModel
import com.seriesly.core.domain.repository.ProgressRepository
import com.seriesly.core.security.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val sessionManager: SessionManager
) : BaseViewModel<HistoryUiState, HistoryIntent, HistoryEvent>(HistoryUiState()) {

    private val userId = sessionManager.getCurrentUserId()

    init {
        viewModelScope.launch {
            progressRepository.observeWatchHistory(userId).collect { entries ->
                setState { copy(entries = entries, isLoading = false) }
            }
        }
    }

    override fun onIntent(intent: HistoryIntent) {
        when (intent) {
            is HistoryIntent.ItemClicked ->
                sendEvent(HistoryEvent.NavigateToDetail(intent.tvdbId, intent.contentType))
        }
    }
}
