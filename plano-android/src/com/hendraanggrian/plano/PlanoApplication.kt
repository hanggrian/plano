package com.hendraanggrian.plano

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import androidx.preference.PreferenceManager
import com.hendraanggrian.defaults.Android
import com.hendraanggrian.defaults.Defaults
import com.hendraanggrian.defaults.DefaultsDebugger
import com.hendraanggrian.defaults.from

class PlanoApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Defaults.setDebug(DefaultsDebugger.Android)
        }
        val defaults = Defaults.from(PreferenceManager.getDefaultSharedPreferences(this))
        defaults {
            if (R2.preference.language !in defaults)
                set(R2.preference.language, Language.EN_US.fullCode)
            if (R2.preference.sheet_width !in defaults) set(R2.preference.sheet_width, 0f)
            if (R2.preference.sheet_height !in defaults) set(R2.preference.sheet_height, 0f)
            if (R2.preference.print_width !in defaults) set(R2.preference.print_width, 0f)
            if (R2.preference.print_height !in defaults) set(R2.preference.print_height, 0f)
            if (R2.preference.trim !in defaults) set(R2.preference.trim, 0f)
        }
    }
}