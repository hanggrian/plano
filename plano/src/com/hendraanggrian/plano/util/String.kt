package com.hendraanggrian.plano.util

/** Removes decimal when available. */
fun Number.clean(): String {
    val s = "$this"
    return when {
        this !is Double && this !is Float -> s
        s.endsWith(".0") -> s.substringBeforeLast(".0")
        else -> s
    }
}
