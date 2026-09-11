package com.bilal.ktornetworking.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ApiServiceTest {
    @Test
    fun sendsGetRequestAndDecodesNewsResponse() = runBlocking {
        val engine = MockEngine { request ->
            assertEquals(HttpMethod.Get, request.method)
            assertEquals(
                "https://api.spaceflightnewsapi.net/v4/articles/?limit=20",
                request.url.toString(),
            )
            assertEquals("application/json", request.headers[HttpHeaders.Accept])
            respond(
                content = """
                    {
                      "count": 1,
                      "results": [{
                        "id": 42,
                        "title": "A sample space mission",
                        "authors": [],
                        "url": "https://example.com/article",
                        "image_url": "https://example.com/image.jpg",
                        "news_site": "Example News",
                        "summary": "Sample summary",
                        "published_at": "2026-09-10T10:00:00Z",
                        "featured": false
                      }]
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = HttpClient(engine) { configureNewsClient() }
        try {
            val response = ApiService(client).getNews()
            assertEquals(1, response.count)
            assertEquals(42, response.results.single().id)
            assertEquals("https://example.com/image.jpg", response.results.single().imageUrl)
        } finally {
            client.close()
            engine.close()
        }
    }

    @Test
    fun propagatesServerErrorInsteadOfReturningEmptyNews() = runBlocking<Unit> {
        val engine = MockEngine {
            respond("Server error", HttpStatusCode.InternalServerError)
        }
        val client = HttpClient(engine) { configureNewsClient() }
        try {
            assertFailsWith<ServerResponseException> {
                ApiService(client).getNews()
            }
        } finally {
            client.close()
            engine.close()
        }
    }
}
