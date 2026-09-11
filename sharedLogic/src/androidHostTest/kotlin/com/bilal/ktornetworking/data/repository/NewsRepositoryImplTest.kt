package com.bilal.ktornetworking.data.repository

import com.bilal.ktornetworking.data.remote.ApiService
import com.bilal.ktornetworking.data.remote.configureNewsClient
import com.bilal.ktornetworking.domain.model.News
import com.bilal.ktornetworking.domain.repository.NewsRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class NewsRepositoryImplTest {
    @Test
    fun mapsApiResponseToDomainAndPreservesMultipleAuthors() = runBlocking {
        withRepository(
            body = """
                {
                  "count": 100,
                  "results": [{
                    "id": 42,
                    "title": "Sample mission",
                    "summary": "Mission summary",
                    "image_url": "https://example.com/image.jpg",
                    "authors": [{"name": "Alex"}, {"name": "Sam"}],
                    "published_at": "2026-09-11T10:00:00Z",
                    "news_site": "Example News",
                    "url": "https://example.com/article"
                  }]
                }
            """.trimIndent(),
        ) { repository ->
            assertEquals(
                listOf(
                    News(
                        id = 42,
                        title = "Sample mission",
                        description = "Mission summary",
                        imageUrl = "https://example.com/image.jpg",
                        authors = listOf("Alex", "Sam"),
                        publishedAt = "2026-09-11T10:00:00Z",
                        sourceName = "Example News",
                        sourceUrl = "https://example.com/article",
                    ),
                ),
                repository.getNews(),
            )
        }
    }

    @Test
    fun returnsEmptyListForSuccessfulEmptyResponse() = runBlocking {
        withRepository("""{"count":0,"results":[]}""") { repository ->
            assertTrue(repository.getNews().isEmpty())
        }
    }

    @Test
    fun propagatesFailureInsteadOfReturningEmptyList() = runBlocking {
        withRepository("Server error", HttpStatusCode.InternalServerError) { repository ->
            assertFailsWith<ServerResponseException> {
                repository.getNews()
            }
        }
    }

    private suspend fun withRepository(
        body: String,
        status: HttpStatusCode = HttpStatusCode.OK,
        check: suspend (NewsRepository) -> Unit,
    ) {
        val engine = MockEngine {
            respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))
        }
        val client = HttpClient(engine) { configureNewsClient() }
        try {
            check(NewsRepositoryImpl(ApiService(client)))
        } finally {
            client.close()
            engine.close()
        }
    }
}
