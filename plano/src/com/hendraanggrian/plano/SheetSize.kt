package com.hendraanggrian.plano

class SheetSize(
    x: Double,
    y: Double,
    width: Double,
    height: Double,
    val pieces: List<Size>
) : Size(x, y, width, height)