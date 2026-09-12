package com.bilal.ktornetworking

import android.app.Application
import com.bilal.ktornetworking.di.KoinInitializer
import com.bilal.ktornetworking.di.androidModule
import org.koin.core.context.loadKoinModules

class NewsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KoinInitializer.start()
        loadKoinModules(androidModule)
    }
}
