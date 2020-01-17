package com.hendraanggrian.plano.util

/** Removes decimal when available. */
fun Double.toCleanString(): String = toString().let {
    when {
        it.endsWith(".0") -> it.substringBeforeLast(".0")
        else -> it
    }
}
