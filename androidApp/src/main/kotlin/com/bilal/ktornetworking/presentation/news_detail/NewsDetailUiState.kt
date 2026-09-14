package com.bilal.ktornetworking.presentation.news_detail

import com.bilal.ktornetworking.domain.model.News

sealed interface NewsDetailUiState {
    data object Loading : NewsDetailUiState
    data class Success(val news: News) : NewsDetailUiState
    data object Error : NewsDetailUiState
}
