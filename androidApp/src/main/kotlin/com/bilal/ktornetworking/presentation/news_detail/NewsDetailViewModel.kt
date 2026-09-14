package com.bilal.ktornetworking.presentation.news_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilal.ktornetworking.domain.repository.NewsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewsDetailViewModel(
    private val newsId: Int,
    private val repository: NewsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<NewsDetailUiState>(NewsDetailUiState.Loading)
    val uiState: StateFlow<NewsDetailUiState> = _uiState.asStateFlow()
    private var loadJob: Job? = null

    init {
        loadNews()
    }

    fun loadNews() {
        if (loadJob?.isActive == true) return
        loadJob = viewModelScope.launch {
            _uiState.value = NewsDetailUiState.Loading
            try {
                _uiState.value = NewsDetailUiState.Success(repository.getNewsById(newsId))
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _uiState.value = NewsDetailUiState.Error
            }
        }
    }
}
