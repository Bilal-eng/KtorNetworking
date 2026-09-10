package com.bilal.ktornetworking.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal expect fun platformEngine(): HttpClientEngineFactory<*>

internal fun createNewsHttpClient(): HttpClient = HttpClient(platformEngine()) {
    expectSuccess = true

    defaultRequest {
        url("https://api.spaceflightnewsapi.net/v4/")
        accept(ContentType.Application.Json)
    }

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }

    install(HttpTimeout) {
        requestTimeoutMillis = 15_000
    }
}
