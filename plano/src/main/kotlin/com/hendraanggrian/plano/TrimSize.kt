package com.hendraanggrian.plano

import java.io.Serializable

class TrimSize(
    val x: Float,
    val y: Float,
    override val width: Float,
    override val height: Float
) : Size, Serializable
