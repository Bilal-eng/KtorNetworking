package com.bilal.ktornetworking.di

import com.bilal.ktornetworking.presentation.news_list.NewsListViewModel
import com.bilal.ktornetworking.presentation.news_detail.NewsDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val androidModule = module {
    viewModel { NewsListViewModel(repository = get()) }
    viewModel { parameters ->
        NewsDetailViewModel(newsId = parameters.get(), repository = get())
    }
}
