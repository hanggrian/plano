package com.hendraanggrian.plano

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.hendraanggrian.prefs.Prefs
import com.hendraanggrian.prefs.android.setDebug

class App : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        Plano.DEBUG = BuildConfig.DEBUG
        Prefs.setDebug(BuildConfig.DEBUG)
    }
}
