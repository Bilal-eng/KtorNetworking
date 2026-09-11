package com.bilal.ktornetworking.data.remote.dto

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NewsResponseDtoTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun decodesApiFieldNamesAndIgnoresUnusedFields() {
        val response = json.decodeFromString<NewsResponseDto>(sampleResponse)
        val news = response.results.single()

        assertEquals(1, response.count)
        assertNull(response.next)
        assertNull(response.previous)
        assertEquals(42, news.id)
        assertEquals("https://example.com/image.jpg", news.imageUrl)
        assertEquals("Example News", news.newsSite)
        assertEquals("2026-09-09T18:11:24Z", news.publishedAt)
        assertEquals("Example Author", news.authors.single().name)
    }

    @Test
    fun acceptsEmptyResultsAndOmittedPaginationLinks() {
        val response = json.decodeFromString<NewsResponseDto>(
            """{"count":0,"results":[]}""",
        )

        assertTrue(response.results.isEmpty())
        assertNull(response.next)
        assertNull(response.previous)
    }

    @Test
    fun rejectsMissingRequiredArticleId() {
        val invalidResponse = sampleResponse.replace("\"id\": 42,", "")

        assertFailsWith<SerializationException> {
            json.decodeFromString<NewsResponseDto>(invalidResponse)
        }
    }

    // Fictional data matching the API response structure; no network required.
    private val sampleResponse = """
        {
          "count": 1,
          "next": null,
          "previous": null,
          "results": [{
            "id": 42,
            "title": "A new space mission",
            "authors": [{"name": "Example Author", "socials": null}],
            "url": "https://example.com/article",
            "image_url": "https://example.com/image.jpg",
            "news_site": "Example News",
            "summary": "A sample article for serialization testing.",
            "published_at": "2026-09-09T18:11:24Z",
            "updated_at": "2026-09-09T19:00:00Z",
            "featured": false,
            "launches": [],
            "events": []
          }]
        }
    """.trimIndent()
}
