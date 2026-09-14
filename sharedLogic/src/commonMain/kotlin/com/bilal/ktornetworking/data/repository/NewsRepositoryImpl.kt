package com.bilal.ktornetworking.data.repository

import com.bilal.ktornetworking.data.remote.ApiService
import com.bilal.ktornetworking.data.remote.dto.NewsDto
import com.bilal.ktornetworking.domain.model.News
import com.bilal.ktornetworking.domain.repository.NewsRepository

internal class NewsRepositoryImpl(
    private val apiService: ApiService,
) : NewsRepository {
    override suspend fun getNewsById(newsId: Int): News {
        return apiService.getNewsById(newsId).toDomain()
    }

    override suspend fun getNews(): List<News> {
        val response = apiService.getNews()
        return response.results.map { it.toDomain() }
    }
}

private fun NewsDto.toDomain(): News = News(
    id = id,
    title = title,
    description = summary,
    imageUrl = imageUrl,
    authors = authors.map { it.name },
    publishedAt = publishedAt,
    sourceName = newsSite,
    sourceUrl = url,
)
