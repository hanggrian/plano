package com.hendraanggrian.plano

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import com.hendraanggrian.prefs.Prefs
import com.hendraanggrian.prefs.android.Android
import com.hendraanggrian.prefs.android.get

class PlanoApp : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        MediaBox.DEBUG = BuildConfig.DEBUG
        if (BuildConfig.DEBUG) Prefs.setLogger(Prefs.Logger.Android)

        val theme = Prefs[this].getInt("theme")
        if (theme != null && theme != AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
            AppCompatDelegate.setDefaultNightMode(theme)
        }
    }
}
