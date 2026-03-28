package com.seriesly.feature.progress.presentation.ratings

import androidx.lifecycle.viewModelScope
import com.seriesly.core.common.base.BaseViewModel
import com.seriesly.core.domain.repository.ProgressRepository
import com.seriesly.core.security.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyRatingsViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val sessionManager: SessionManager
) : BaseViewModel<MyRatingsUiState, MyRatingsIntent, MyRatingsEvent>(MyRatingsUiState()) {

    private val userId = sessionManager.getCurrentUserId()

    init {
        viewModelScope.launch {
            progressRepository.observeAllRatings(userId).collect { ratings ->
                setState { copy(ratings = ratings, isLoading = false) }
            }
        }
    }

    override fun onIntent(intent: MyRatingsIntent) {
        when (intent) {
            is MyRatingsIntent.ItemClicked ->
                sendEvent(MyRatingsEvent.NavigateToDetail(intent.tvdbId, intent.contentType))
        }
    }
}
