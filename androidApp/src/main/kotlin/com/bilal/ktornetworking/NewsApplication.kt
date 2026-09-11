package com.bilal.ktornetworking

import android.app.Application
import com.bilal.ktornetworking.di.KoinInitializer

class NewsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KoinInitializer.start()
    }
}
