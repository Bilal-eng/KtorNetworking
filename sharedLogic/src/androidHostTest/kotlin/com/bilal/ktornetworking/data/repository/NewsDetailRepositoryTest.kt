package com.bilal.ktornetworking.data.repository

import com.bilal.ktornetworking.data.remote.ApiService
import com.bilal.ktornetworking.data.remote.configureNewsClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NewsDetailRepositoryTest {
    @Test
    fun requestsIdAndMapsSingleObjectWithoutResultsWrapper() = runBlocking {
        val engine = MockEngine { request ->
            assertEquals(HttpMethod.Get, request.method)
            assertEquals("https://api.spaceflightnewsapi.net/v4/articles/42/", request.url.toString())
            respond(
                """{"id":42,"title":"Title","summary":"Full summary","authors":[{"name":"Alex"},{"name":"Sam"}],"image_url":"https://example.com/image.jpg","url":"https://example.com/article","news_site":"Source","published_at":"2026-09-14T10:00:00Z"}""",
                HttpStatusCode.OK,
                headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = HttpClient(engine) { configureNewsClient() }
        try {
            val news = NewsRepositoryImpl(ApiService(client)).getNewsById(42)
            assertEquals(42, news.id)
            assertEquals("Full summary", news.description)
            assertEquals(listOf("Alex", "Sam"), news.authors)
        } finally {
            client.close()
            engine.close()
        }
    }

    @Test
    fun missingArticlePropagatesNotFound() = runBlocking<Unit> {
        val engine = MockEngine { respond("""{"detail":"Not found."}""", HttpStatusCode.NotFound) }
        val client = HttpClient(engine) { configureNewsClient() }
        try {
            val error = assertFailsWith<ClientRequestException> {
                NewsRepositoryImpl(ApiService(client)).getNewsById(42)
            }
            assertEquals(HttpStatusCode.NotFound, error.response.status)
        } finally {
            client.close()
            engine.close()
        }
    }
}
