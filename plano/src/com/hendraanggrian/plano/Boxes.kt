package com.hendraanggrian.plano

interface Box {
    val width: Double
    val height: Double
}

class MediaBox(
    override val width: Double,
    override val height: Double,
    trimBoxes: List<TrimBox>
) : Box, List<TrimBox> by trimBoxes

class TrimBox(
    val x: Double,
    val y: Double,
    override val width: Double,
    override val height: Double
) : Box
