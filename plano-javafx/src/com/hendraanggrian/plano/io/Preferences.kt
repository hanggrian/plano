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

        if (R2.preference.language !in this) set(R2.preference.language, Language.EN_US.fullCode)
        if (R2.preference.sheet_width !in this) set(R2.preference.sheet_width, 0.0)
        if (R2.preference.sheet_height !in this) set(R2.preference.sheet_height, 0.0)
        if (R2.preference.print_width !in this) set(R2.preference.print_width, 0.0)
        if (R2.preference.print_height !in this) set(R2.preference.print_height, 0.0)
        if (R2.preference.trim !in this) set(R2.preference.trim, 0.0)
    }

    operator fun contains(key: String): Boolean = properties.containsKey(key)

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