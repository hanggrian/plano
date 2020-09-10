package com.hendraanggrian.plano

/** Represents a dimension of a paper. */
interface Size {

    val width: Double

    val height: Double

    val dimension: String get() = "${width.clean()} x ${height.clean()}"
}
