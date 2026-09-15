package com.bilal.ktornetworking.bridge

import com.bilal.ktornetworking.domain.model.News
import com.bilal.ktornetworking.domain.repository.NewsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class IosNewsClientTest {
    private val news = News(42, "Title", "Summary", "image", listOf("Author"), "date", "Source", "url")

    @Test
    fun returnsListAndSelectedArticle() = runTest {
        val ids = mutableListOf<Int>()
        val repository = fake(detail = { id -> ids += id; news })
        val client = IosNewsClient(repository, backgroundScope)
        var list: List<News>? = null
        var detail: News? = null
        client.getNews(onSuccess = { list = it }, onError = { error("Unexpected failure") })
        client.getNewsById(42, onSuccess = { detail = it }, onError = { error("Unexpected failure") })
        runCurrent()
        assertEquals(listOf(news), list)
        assertEquals(news, detail)
        assertEquals(listOf(42), ids)
    }

    @Test
    fun reportsFailureAndAllowsAnotherRequest() = runTest {
        var calls = 0
        var failures = 0
        var successes = 0
        val client = IosNewsClient(fake(list = {
            calls++
            if (calls == 1) error("Network failure")
            emptyList()
        }), backgroundScope)
        client.getNews(onSuccess = { successes++ }, onError = { failures++ })
        runCurrent()
        client.getNews(onSuccess = { successes++ }, onError = { failures++ })
        runCurrent()
        assertEquals(1, failures)
        assertEquals(1, successes)
    }

    @Test
    fun requestCancellationDoesNotDeliverCallbacks() = runTest {
        var cancelled = false
        var callbackCalled = false
        val client = IosNewsClient(fake(list = {
            try { awaitCancellation() } finally { cancelled = true }
        }), backgroundScope)
        val request = client.getNews(onSuccess = { callbackCalled = true }, onError = { callbackCalled = true })
        runCurrent()
        request.cancel()
        runCurrent()
        assertEquals(true, cancelled)
        assertFalse(callbackCalled)
    }

    @Test
    fun closingClientCancelsAllRequests() = runTest {
        var cancellations = 0
        val scope = CoroutineScope(SupervisorJob() + StandardTestDispatcher(testScheduler))
        val client = IosNewsClient(fake(list = {
            try { awaitCancellation() } finally { cancellations++ }
        }), scope)
        try {
            repeat(2) {
                client.getNews(onSuccess = { error("Unexpected success") }, onError = { error("Unexpected error") })
            }
            runCurrent()
            client.close()
            runCurrent()
            assertEquals(2, cancellations)
        } finally {
            client.close()
        }
    }

    private fun fake(
        list: suspend () -> List<News> = { listOf(news) },
        detail: suspend (Int) -> News = { news },
    ) = object : NewsRepository {
        override suspend fun getNews(): List<News> = list()
        override suspend fun getNewsById(newsId: Int): News = detail(newsId)
    }
}
