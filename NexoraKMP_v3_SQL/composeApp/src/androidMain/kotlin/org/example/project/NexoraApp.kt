package org.example.project

import android.app.Application
import android.content.Context

class NexoraApp : Application() {
    companion object {
        lateinit var appContext: Context
    }
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}
