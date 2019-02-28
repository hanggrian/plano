package com.hendraanggrian.plano

data class MediaSize(
    val width: Double,
    val height: Double,
    val trimSizes: List<TrimSize>
)

data class TrimSize(
    val x: Double,
    val y: Double,
    val width: Double,
    val height: Double
)