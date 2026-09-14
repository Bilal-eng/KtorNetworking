package com.bilal.ktornetworking.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
data object NewsListDestination

@Serializable
data class NewsDetailDestination(val newsId: Int)
