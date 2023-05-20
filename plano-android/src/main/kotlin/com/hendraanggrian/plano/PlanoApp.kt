package com.hendraanggrian.plano

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.hendraanggrian.auto.prefs.PreferencesLogger
import com.hendraanggrian.auto.prefs.Prefs
import com.hendraanggrian.auto.prefs.android.Android
import com.hendraanggrian.auto.prefs.android.preferences

class PlanoApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        Plano.setDebug(BuildConfig.DEBUG)
        if (BuildConfig.DEBUG) Prefs.setLogger(PreferencesLogger.Android)

        val theme = preferences.getInt("theme")
        if (theme != null && theme != AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
            AppCompatDelegate.setDefaultNightMode(theme)
        }
    }
}
