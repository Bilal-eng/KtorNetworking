package com.bilal.ktornetworking.data.remote

import com.bilal.ktornetworking.data.remote.dto.NewsResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class ApiService(private val client: HttpClient) {
    suspend fun getNews(): NewsResponseDto {
        val response = client.get("articles/") {
            parameter("limit", 20)
        }
        return response.body<NewsResponseDto>()
    }
}
