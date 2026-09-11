package com.bilal.ktornetworking.di

import org.koin.core.context.startKoin

object KoinInitializer {
    fun start() {
        startKoin {
            modules(sharedModule)
        }
    }
}
