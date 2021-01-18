package com.ikhokha.techcheck.app

import android.content.Context
import androidx.multidex.MultiDex

class CustomApplication : android.app.Application() {

    override fun onCreate() {
        super.onCreate()

        instance = this
    }

    public override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        try {
            MultiDex.install(this)
        } catch (multiDexException: RuntimeException) {
            multiDexException.printStackTrace()
        }
    }

    companion object {

        @get:Synchronized
        var instance: CustomApplication? = null
            private set
    }
}