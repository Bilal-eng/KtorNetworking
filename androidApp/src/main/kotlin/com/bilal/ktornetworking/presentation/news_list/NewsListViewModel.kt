package com.bilal.ktornetworking.presentation.news_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilal.ktornetworking.domain.repository.NewsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewsListViewModel(
    private val repository: NewsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<NewsListUiState>(NewsListUiState.Loading)
    val uiState: StateFlow<NewsListUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadNews()
    }

    fun loadNews() {
        if (loadJob?.isActive == true) return

        loadJob = viewModelScope.launch {
            _uiState.value = NewsListUiState.Loading
            try {
                val news = repository.getNews()
                _uiState.value = NewsListUiState.Success(news)
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _uiState.value = NewsListUiState.Error
            }
        }
    }
}
