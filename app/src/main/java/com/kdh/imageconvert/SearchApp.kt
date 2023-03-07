package com.kdh.imageconvert

import android.app.Application
import android.content.Context
import timber.log.Timber

class SearchApp : Application() {
    companion object {
        private lateinit var instance: SearchApp
        fun getInstance(): SearchApp {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Timber.plant(Timber.DebugTree())
    }


}