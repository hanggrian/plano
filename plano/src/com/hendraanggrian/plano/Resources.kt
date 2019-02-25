package com.hendraanggrian.plano

import java.util.ResourceBundle

interface Resources {

    val resourceBundle: ResourceBundle

    val resourceLanguage: Language get() = Language.ofCode(resourceBundle.locale.language)

    fun getString(id: String): String = resourceBundle.getString(id)

    fun getString(id: String, vararg args: Any): String = getString(id).format(*args)
}