package com.bilal.ktornetworking.data.remote

import com.bilal.ktornetworking.data.remote.dto.NewsResponseDto
import com.bilal.ktornetworking.data.remote.dto.NewsDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class ApiService(private val client: HttpClient) {
    suspend fun getNewsById(newsId: Int): NewsDto {
        require(newsId > 0) { "News ID must be positive" }
        return client.get("articles/$newsId/").body<NewsDto>()
    }

    suspend fun getNews(): NewsResponseDto {
        val response = client.get("articles/") {
            parameter("limit", 20)
        }
        return response.body<NewsResponseDto>()
    }
}
