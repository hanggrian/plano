package com.hanggrian.plano

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import androidx.preference.PreferenceManager
import com.hanggrian.plano.prefs.THEME

class PlanoApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        val theme = PreferenceManager.getDefaultSharedPreferences(this).getInt(THEME, -1)
        if (theme != AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
            AppCompatDelegate.setDefaultNightMode(theme)
        }
    }
}
