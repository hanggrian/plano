package com.hanggrian.plano

import java.io.Serializable

public class TrimSize(
    public val x: Float,
    public val y: Float,
    override val width: Float,
    override val height: Float,
) : Size,
    Serializable
