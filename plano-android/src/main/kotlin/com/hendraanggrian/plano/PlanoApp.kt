package com.hendraanggrian.plano

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import androidx.preference.PreferenceManager
import com.hendraanggrian.auto.prefs.PreferencesLogger
import com.hendraanggrian.auto.prefs.Prefs
import com.hendraanggrian.auto.prefs.android.Android

class PlanoApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Prefs.setLogger(PreferencesLogger.Android)
        }

        val theme = PreferenceManager.getDefaultSharedPreferences(this).getInt("theme", 99)
        if (theme != 99 && theme != AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
            AppCompatDelegate.setDefaultNightMode(theme)
        }
    }
}
