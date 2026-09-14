package com.bilal.ktornetworking.presentation.news_detail

import androidx.lifecycle.ViewModelStore
import com.bilal.ktornetworking.di.androidModule
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
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class NewsDetailViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val store = ViewModelStore()
    private val news = News(42, "Title", "Description", "image", listOf("Author"), "date", "Source", "url")

    @Before fun setUp() { Dispatchers.setMain(dispatcher) }
    @After fun tearDown() { store.clear(); Dispatchers.resetMain() }

    @Test
    fun koinPassesSelectedIdAndPublishesNews() = runTest(dispatcher) {
        var requestedId: Int? = null
        val repository = fake { id -> requestedId = id; news }
        val application = koinApplication {
            modules(androidModule, module { single<NewsRepository> { repository } })
        }
        try {
            val viewModel = application.koin.get<NewsDetailViewModel> { parametersOf(42) }
            store.put("detail", viewModel)
            assertEquals(NewsDetailUiState.Loading, viewModel.uiState.value)
            runCurrent()
            assertEquals(42, requestedId)
            assertEquals(NewsDetailUiState.Success(news), viewModel.uiState.value)
        } finally {
            store.clear()
            application.close()
        }
    }

    @Test
    fun retriesSameIdAfterFailureAndIgnoresDuplicateRequest() = runTest(dispatcher) {
        val ids = mutableListOf<Int>()
        val response = CompletableDeferred<News>()
        val viewModel = NewsDetailViewModel(42, fake { id ->
            ids += id
            if (ids.size == 1) error("Failed request")
            response.await()
        })
        store.put("detail", viewModel)
        runCurrent()
        assertEquals(NewsDetailUiState.Error, viewModel.uiState.value)
        viewModel.loadNews()
        runCurrent()
        assertEquals(NewsDetailUiState.Loading, viewModel.uiState.value)
        viewModel.loadNews()
        runCurrent()
        assertEquals(listOf(42, 42), ids)
        response.complete(news)
        runCurrent()
        assertEquals(NewsDetailUiState.Success(news), viewModel.uiState.value)
    }

    @Test
    fun clearingViewModelCancelsRequestWithoutError() = runTest(dispatcher) {
        var cancelled = false
        val viewModel = NewsDetailViewModel(42, fake {
            try { awaitCancellation() } finally { cancelled = true }
        })
        store.put("detail", viewModel)
        runCurrent()
        store.clear()
        runCurrent()
        assertTrue(cancelled)
        assertEquals(NewsDetailUiState.Loading, viewModel.uiState.value)
    }

    private fun fake(load: suspend (Int) -> News) = object : NewsRepository {
        override suspend fun getNews(): List<News> = error("List must not be requested")
        override suspend fun getNewsById(newsId: Int): News = load(newsId)
    }
}
