package com.hendraanggrian.plano

interface Box {
    val width: Double
    val height: Double
}

data class MediaBox(
    override val width: Double,
    override val height: Double,
    val trimBoxes: List<TrimBox>
) : Box

data class TrimBox(
    val x: Double,
    val y: Double,
    override val width: Double,
    override val height: Double
) : Box
