package com.hendraanggrian.plano

data class MediaBox(
    val width: Double,
    val height: Double,
    val trimBoxes: List<TrimBox>
)

data class TrimBox(
    val x: Double,
    val y: Double,
    val width: Double,
    val height: Double
)
