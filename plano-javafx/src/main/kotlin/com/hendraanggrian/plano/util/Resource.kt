package com.hendraanggrian.plano.util

import com.hendraanggrian.plano.PlanoApp

fun getResource(name: String): String = PlanoApp::class.java.getResource(name).toExternalForm()
