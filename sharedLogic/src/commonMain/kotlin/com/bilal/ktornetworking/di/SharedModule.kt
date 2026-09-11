package com.bilal.ktornetworking.di

import com.bilal.ktornetworking.data.remote.ApiService
import com.bilal.ktornetworking.data.remote.createNewsHttpClient
import com.bilal.ktornetworking.data.repository.NewsRepositoryImpl
import com.bilal.ktornetworking.domain.repository.NewsRepository
import io.ktor.client.HttpClient
import org.koin.dsl.module
import org.koin.dsl.onClose

internal val sharedModule = module {
    single<HttpClient> { createNewsHttpClient() } onClose { client ->
        client?.close()
    }
    single { ApiService(client = get()) }
    single<NewsRepository> { NewsRepositoryImpl(apiService = get()) }
}
