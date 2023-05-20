package com.hendraanggrian.plano

/** Represents a dimension of a paper. */
interface Size {
    val width: Float

    val height: Float

    val dimension: String get() = "${width.clean()} x ${height.clean()}"
}
