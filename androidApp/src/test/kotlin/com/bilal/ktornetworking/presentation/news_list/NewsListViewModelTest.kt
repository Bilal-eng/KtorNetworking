package com.bilal.ktornetworking.presentation.news_list

import androidx.lifecycle.ViewModelStore
import com.bilal.ktornetworking.domain.model.News
import com.bilal.ktornetworking.domain.repository.NewsRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class NewsListViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val store = ViewModelStore()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        store.clear()
        Dispatchers.resetMain()
    }

    @Test
    fun startsLoadingThenPublishesNews() = runTest(dispatcher) {
        val news = listOf(
            News(
                1, "Title", "Summary", "image", listOf("Author"), "date", "Source", "url"
            )
        )
        val viewModel = createViewModel { news }
        assertEquals(NewsListUiState.Loading, viewModel.uiState.value)

        runCurrent()

        assertEquals(NewsListUiState.Success(news), viewModel.uiState.value)
    }

    @Test
    fun failureCanBeRetriedAndEmptyListIsSuccess() = runTest(dispatcher) {
        var calls = 0
        val retryResponse = CompletableDeferred<List<News>>()
        val viewModel = createViewModel {
            calls++
            if (calls == 1) error("Network failure")
            retryResponse.await()
        }
        runCurrent()
        assertEquals(NewsListUiState.Error, viewModel.uiState.value)

        viewModel.loadNews()
        runCurrent()
        assertEquals(NewsListUiState.Loading, viewModel.uiState.value)

        retryResponse.complete(emptyList())
        runCurrent()
        assertEquals(NewsListUiState.Success(emptyList()), viewModel.uiState.value)
        assertEquals(2, calls)
    }

    @Test
    fun ignoresDuplicateLoadsWhileRequestIsActive() = runTest(dispatcher) {
        var calls = 0
        val response = CompletableDeferred<List<News>>()
        val viewModel = createViewModel {
            calls++
            response.await()
        }
        viewModel.loadNews()
        runCurrent()
        viewModel.loadNews()
        runCurrent()

        assertEquals(1, calls)
        response.complete(emptyList())
        runCurrent()
    }

    @Test
    fun clearingViewModelCancelsRequestWithoutPublishingError() = runTest(dispatcher) {
        var cancelled = false
        val viewModel = createViewModel {
            try {
                awaitCancellation()
            } finally {
                cancelled = true
            }
        }
        runCurrent()
        store.clear()
        runCurrent()

        assertTrue(cancelled)
        assertEquals(NewsListUiState.Loading, viewModel.uiState.value)
    }

    private fun createViewModel(load: suspend () -> List<News>): NewsListViewModel {
        val repository = object : NewsRepository {
            override suspend fun getNews(): List<News> = load()
            override suspend fun getNewsById(newsId: Int): News = error("Not used in list tests")
        }
        return NewsListViewModel(repository).also { store.put("news", it) }
    }
}
