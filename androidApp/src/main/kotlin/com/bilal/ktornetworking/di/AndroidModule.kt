package com.bilal.ktornetworking.di

import com.bilal.ktornetworking.presentation.news_list.NewsListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val androidModule = module {
    viewModel { NewsListViewModel(repository = get()) }
}
