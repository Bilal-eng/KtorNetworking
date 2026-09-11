package com.bilal.ktornetworking.di

import com.bilal.ktornetworking.data.remote.ApiService
import com.bilal.ktornetworking.data.repository.NewsRepositoryImpl
import com.bilal.ktornetworking.domain.repository.NewsRepository
import io.ktor.client.HttpClient
import org.koin.dsl.koinApplication
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertSame

class SharedModuleTest {
    @Test
    fun resolvesRepositoryGraphAndReusesSingletons() {
        val application = koinApplication {
            modules(sharedModule)
        }
        try {
            val koin = application.koin
            val repository = koin.get<NewsRepository>()

            assertIs<NewsRepositoryImpl>(repository)
            assertSame(repository, koin.get<NewsRepository>())
            assertSame(koin.get<ApiService>(), koin.get<ApiService>())
            assertSame(koin.get<HttpClient>(), koin.get<HttpClient>())
        } finally {
            application.close()
        }
    }
}
