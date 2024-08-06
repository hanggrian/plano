package com.hanggrian.plano.util

import com.hanggrian.plano.PlanoApp

fun getResource(name: String): String = PlanoApp::class.java.getResource(name)!!.toExternalForm()
