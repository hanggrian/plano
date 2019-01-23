package com.hendraanggrian.plano.io

import com.hendraanggrian.plano.Language
import java.util.Properties

class Preferences {

    companion object {
        const val LANGUAGE = "language"
    }

    private val file = PreferencesFile()
    private val properties = Properties()

    init {
        if (!file.exists()) {
            file.createNewFile()
        }
        file.inputStream().use { properties.load(it) }

        if (!contains(LANGUAGE)) {
            setString(LANGUAGE, Language.EN_US.fullCode)
        }
    }

    fun contains(key: String): Boolean = properties.containsKey(key)

    fun getString(key: String): String = properties.getProperty(key)

    fun setString(key: String, value: String) {
        properties.setProperty(key, value)
    }

    fun save() {
        file.outputStream().use { properties.store(it, null) }
    }
}