package com.hendraanggrian.plano.io

import com.hendraanggrian.plano.Language
import com.hendraanggrian.plano.R2
import java.util.Properties

class Preferences {

    private val file = PreferencesFile()
    private val properties = Properties()

    init {
        if (!file.exists()) {
            file.createNewFile()
        }
        file.inputStream().use { properties.load(it) }

        if (!contains(R2.preference.language)) set(R2.preference.language, Language.EN_US.fullCode)
        if (!contains(R2.preference.sheet_width)) set(R2.preference.sheet_width, 0.0)
        if (!contains(R2.preference.sheet_height)) set(R2.preference.sheet_height, 0.0)
        if (!contains(R2.preference.print_width)) set(R2.preference.print_width, 0.0)
        if (!contains(R2.preference.print_height)) set(R2.preference.print_height, 0.0)
        if (!contains(R2.preference.trim)) set(R2.preference.trim, 0.0)
    }

    fun contains(key: String): Boolean = properties.containsKey(key)

    fun getString(key: String): String = properties.getProperty(key)

    operator fun set(key: String, value: String) {
        properties.setProperty(key, value)
    }

    fun getDouble(key: String): Double = getString(key).toDouble()

    operator fun set(key: String, value: Double) = set(key, value.toString())

    fun save() {
        file.outputStream().use { properties.store(it, null) }
    }
}