package com.hendraanggrian.plano

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.hendraanggrian.defaults.Android
import com.hendraanggrian.defaults.Defaults
import com.hendraanggrian.defaults.DefaultsDebugger

class PlanoApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Defaults.setDebugger(DefaultsDebugger.Android)
        }
    }
}