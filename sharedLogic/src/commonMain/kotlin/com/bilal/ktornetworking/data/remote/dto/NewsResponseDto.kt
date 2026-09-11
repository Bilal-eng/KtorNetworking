package com.bilal.ktornetworking.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class NewsResponseDto(
    val count: Int,
    val results: List<NewsDto>,
    val next: String? = null,
    val previous: String? = null,
)
