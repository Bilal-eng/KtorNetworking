package com.bilal.ktornetworking.domain.repository

import com.bilal.ktornetworking.domain.model.News

interface NewsRepository {
    suspend fun getNews(): List<News>
    suspend fun getNewsById(newsId: Int): News
}
