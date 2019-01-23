package com.hendraanggrian.plano.io

import com.hendraanggrian.plano.Language
import java.util.Properties

class Preferences {

    companion object {
        const val LANGUAGE = "language"
        const val SHEET_WIDTH = "sheet_width"
        const val SHEET_HEIGHT = "sheet_height"
        const val PRINT_WIDTH = "print_width"
        const val PRINT_HEIGHT = "print_height"
        const val TRIM = "trim"
    }

    private val file = PreferencesFile()
    private val properties = Properties()

    init {
        if (!file.exists()) {
            file.createNewFile()
        }
        file.inputStream().use { properties.load(it) }

        if (!contains(LANGUAGE)) set(LANGUAGE, Language.EN_US.fullCode)
        if (!contains(SHEET_WIDTH)) set(SHEET_WIDTH, 0.0)
        if (!contains(SHEET_HEIGHT)) set(SHEET_HEIGHT, 0.0)
        if (!contains(PRINT_WIDTH)) set(PRINT_WIDTH, 0.0)
        if (!contains(PRINT_HEIGHT)) set(PRINT_HEIGHT, 0.0)
        if (!contains(TRIM)) set(TRIM, 0.0)
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