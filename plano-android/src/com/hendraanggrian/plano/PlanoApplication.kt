package com.hendraanggrian.plano

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.multidex.MultiDex
import androidx.preference.PreferenceManager

class PlanoApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        preferences.edit {
            if (R2.preference.language !in preferences)
                putString(R2.preference.language, Language.EN_US.fullCode)
            if (R2.preference.sheet_width !in preferences) putFloat(R2.preference.sheet_width, 0f)
            if (R2.preference.sheet_height !in preferences) putFloat(R2.preference.sheet_height, 0f)
            if (R2.preference.print_width !in preferences) putFloat(R2.preference.print_width, 0f)
            if (R2.preference.print_height !in preferences) putFloat(R2.preference.print_height, 0f)
            if (R2.preference.trim !in preferences) putFloat(R2.preference.trim, 0f)
        }
    }
}