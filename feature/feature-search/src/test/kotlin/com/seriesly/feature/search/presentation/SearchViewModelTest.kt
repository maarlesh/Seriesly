package com.seriesly.feature.search.presentation

import app.cash.turbine.test
import com.seriesly.core.common.model.ContentType
import com.seriesly.core.domain.model.ContentFilter
import com.seriesly.core.domain.model.ContentItem
import com.seriesly.feature.search.FakeSearchRepository
import com.seriesly.feature.search.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val fakeRepo = FakeSearchRepository()
    private val vm by lazy { SearchViewModel(fakeRepo) }

    @Test
    fun `query under 3 chars shows no results and no loading`() = runTest {
        vm.onIntent(SearchIntent.QueryChanged("ab"))
        advanceTimeBy(500)
        vm.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertTrue(state.results.isEmpty())
            assertNull(state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `query of 3+ chars triggers search and emits results`() = runTest {
        val item = ContentItem(tvdbId = 1, title = "Dune", contentType = ContentType.MOVIE, year = 2021, posterUrl = null, tvdbRating = 8.0f)
        fakeRepo.results.add(item)

        vm.onIntent(SearchIntent.QueryChanged("Dune"))
        advanceTimeBy(500)

        vm.uiState.test {
            val state = expectMostRecentItem()
            assertTrue(state.results.contains(item))
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `network error shows error state when no cache`() = runTest {
        fakeRepo.shouldError = true
        vm.onIntent(SearchIntent.QueryChanged("Dune"))
        advanceTimeBy(500)

        vm.uiState.test {
            val state = expectMostRecentItem()
            assertNotNull(state.error)
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clear query resets results and query`() = runTest {
        val item = ContentItem(tvdbId = 1, title = "Dune", contentType = ContentType.MOVIE, year = 2021, posterUrl = null, tvdbRating = null)
        fakeRepo.results.add(item)

        vm.onIntent(SearchIntent.QueryChanged("Dune"))
        advanceTimeBy(500)
        vm.onIntent(SearchIntent.ClearQuery)

        vm.uiState.test {
            val state = expectMostRecentItem()
            assertEquals("", state.query)
            assertTrue(state.results.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filter change re-triggers search`() = runTest {
        val movie = ContentItem(tvdbId = 1, title = "Dune", contentType = ContentType.MOVIE, year = 2021, posterUrl = null, tvdbRating = null)
        val series = ContentItem(tvdbId = 2, title = "House", contentType = ContentType.SERIES, year = 2004, posterUrl = null, tvdbRating = null)
        fakeRepo.results.addAll(listOf(movie, series))

        vm.onIntent(SearchIntent.QueryChanged("test"))
        advanceTimeBy(500)
        vm.onIntent(SearchIntent.FilterSelected(ContentFilter.MOVIES))
        advanceTimeBy(500)

        vm.uiState.test {
            val state = expectMostRecentItem()
            assertEquals(ContentFilter.MOVIES, state.filter)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `item clicked emits NavigateToDetail event`() = runTest {
        vm.events.test {
            vm.onIntent(SearchIntent.ItemClicked(tvdbId = 42, contentType = ContentType.SERIES))
            val event = awaitItem()
            assertTrue(event is SearchEvent.NavigateToDetail)
            assertEquals(42, (event as SearchEvent.NavigateToDetail).tvdbId)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
