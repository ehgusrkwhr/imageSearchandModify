package com.kdh.imageconvert

import android.app.Application
import timber.log.Timber

class SearchApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}