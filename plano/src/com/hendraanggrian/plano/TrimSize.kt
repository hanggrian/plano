package com.hendraanggrian.plano

import java.io.Serializable

class TrimSize(
    val x: Double,
    val y: Double,
    override val width: Double,
    override val height: Double
) : Size, Serializable
