package com.hendraanggrian.plano

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import androidx.preference.PreferenceManager
import com.hendraanggrian.defaults.Android
import com.hendraanggrian.defaults.Defaults
import com.hendraanggrian.defaults.DefaultsDebugger
import com.hendraanggrian.defaults.toDefaults

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
        val defaults = PreferenceManager.getDefaultSharedPreferences(this).toDefaults()
        defaults {
            if (R2.preference.language !in this)
                it[R2.preference.language] = Language.EN_US.fullCode
            if (R2.preference.media_width !in this) it[R2.preference.media_width] = 0f
            if (R2.preference.media_height !in this) it[R2.preference.media_height] = 0f
            if (R2.preference.trim_width !in this) it[R2.preference.trim_width] = 0f
            if (R2.preference.trim_height !in this) it[R2.preference.trim_height] = 0f
            if (R2.preference.bleed !in this) it[R2.preference.bleed] = 0f
        }
    }
}