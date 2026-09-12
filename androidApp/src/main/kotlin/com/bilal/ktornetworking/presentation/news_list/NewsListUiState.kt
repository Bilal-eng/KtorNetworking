package com.bilal.ktornetworking.presentation.news_list

import com.bilal.ktornetworking.domain.model.News

sealed interface NewsListUiState {
    data object Loading : NewsListUiState
    data class Success(val news: List<News>) : NewsListUiState
    data object Error : NewsListUiState
}
