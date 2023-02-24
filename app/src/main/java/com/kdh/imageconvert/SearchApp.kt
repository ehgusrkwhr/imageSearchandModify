package com.kdh.imageconvert

import android.app.Application
import android.content.Context
import timber.log.Timber

class SearchApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

    companion object{
        fun getAppContext(context: Context) : Context {
            return context.applicationContext
        }
    }

}