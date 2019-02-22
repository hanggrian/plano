package com.hendraanggrian.plano

object Plano {

    fun getTrimSizes(
        mediaWidth: Double,
        mediaHeight: Double,
        trimWidth: Double,
        trimHeight: Double,
        bleed: Double = 0.0
    ): List<PrintSize> {
        val sizes1 = calculateTrimSizes(
            mediaWidth,
            mediaHeight,
            trimWidth + bleed,
            trimHeight + bleed
        )
        val sizes2 = calculateTrimSizes(
            mediaWidth,
            mediaHeight,
            trimHeight + bleed,
            trimWidth + bleed
        )
        return when {
            sizes1.size >= sizes2.size -> {
                if (BuildConfig.DEBUG) println("Choice 1 chosen")
                sizes1
            }
            else -> {
                if (BuildConfig.DEBUG) println("Choice 2 chosen")
                sizes2
            }
        }
    }

    private fun calculateTrimSizes(
        mediaWidth: Double,
        mediaHeight: Double,
        trimWidth: Double,
        trimHeight: Double
    ): List<PrintSize> {
        if (BuildConfig.DEBUG)
            println("----- ${mediaWidth}x$mediaHeight - ${trimWidth}x$trimHeight -----")

        val sizes = mutableListOf<PrintSize>()
        val columns = (mediaWidth / trimWidth).toInt()
        val rows = (mediaHeight / trimHeight).toInt()
        if (BuildConfig.DEBUG) {
            println("columns: $columns")
            println("rows: $rows")
        }
        for (column in 0 until columns) {
            val x = column * trimWidth
            for (row in 0 until rows) {
                val y = row * trimHeight
                sizes += PrintSize(x, y, trimWidth, trimHeight)
            }
        }

        var rightLeftovers = 0
        val widthLeftover = mediaWidth - trimWidth * columns
        if (columns > 0 && widthLeftover >= trimHeight) {
            rightLeftovers = (mediaHeight / trimWidth).toInt()
        }
        var bottomLeftovers = 0
        val heightLeftover = mediaHeight - trimHeight * rows
        if (rows > 0 && heightLeftover >= trimWidth) {
            bottomLeftovers = (mediaWidth / trimHeight).toInt()
        }
        if (BuildConfig.DEBUG) {
            println("rightLeftovers: $rightLeftovers")
            println("bottomLeftovers: $bottomLeftovers")
        }
        if (rightLeftovers > bottomLeftovers) {
            val x = trimWidth * columns
            for (leftover in 0 until rightLeftovers) {
                sizes += PrintSize(x, leftover * trimWidth, trimHeight, trimWidth)
            }
        } else if (bottomLeftovers > rightLeftovers) {
            val y = trimHeight * columns
            for (leftover in 0 until bottomLeftovers) {
                sizes += PrintSize(leftover * trimHeight, y, trimHeight, trimWidth)
            }
        }

        return sizes
    }
}