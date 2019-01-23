package com.hendraanggrian.plano

import java.util.ResourceBundle

interface Resources {

    val resources: ResourceBundle

    fun getString(id: String): String = resources.getString(id)
}