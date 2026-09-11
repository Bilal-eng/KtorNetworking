package com.bilal.ktornetworking.domain.model

data class News(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String,
    val authors: List<String>,
    val publishedAt: String,
    val sourceName: String,
    val sourceUrl: String,
)
