package com.hendraanggrian.plano

import kotlin.math.max

internal fun count(sourceWidth: Double, sourceHeight: Double, targetWidth: Double, targetHeight: Double): Int {
    val column = (sourceWidth / targetWidth).toInt()
    val widthLeftover = sourceWidth - targetWidth * column
    var columnLeftover = 0
    if (widthLeftover >= targetHeight) {
        columnLeftover = (widthLeftover / targetHeight).toInt()
    }

    val row = (sourceHeight / targetHeight).toInt()
    val heightLeftover = sourceHeight - targetHeight * row
    var rowLeftover = 0
    if (heightLeftover >= targetWidth) {
        rowLeftover = (heightLeftover / targetWidth).toInt()
    }

    return column * row + max(columnLeftover, rowLeftover)
}