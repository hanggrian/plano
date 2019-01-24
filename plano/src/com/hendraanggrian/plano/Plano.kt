package com.hendraanggrian.plano

object Plano {

    fun getPrintSizes(
        sheetWidth: Double,
        sheetHeight: Double,
        printWidth: Double,
        printHeight: Double,
        trim: Double = 0.0
    ): List<PrintSize> {
        val sizes1 = calculatePrintSizes(
            sheetWidth,
            sheetHeight,
            printWidth + trim,
            printHeight + trim
        )
        val sizes2 = calculatePrintSizes(
            sheetWidth,
            sheetHeight,
            printHeight + trim,
            printWidth + trim
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

    private fun calculatePrintSizes(
        sheetWidth: Double,
        sheetHeight: Double,
        printWidth: Double,
        printHeight: Double
    ): List<PrintSize> {
        if (BuildConfig.DEBUG) println("----- ${sheetWidth}x$sheetHeight - ${printWidth}x$printHeight -----")

        val sizes = mutableListOf<PrintSize>()
        val columns = (sheetWidth / printWidth).toInt()
        val rows = (sheetHeight / printHeight).toInt()
        if (BuildConfig.DEBUG) {
            println("columns: $columns")
            println("rows: $rows")
        }
        for (column in 0 until columns) {
            val x = column * printWidth
            for (row in 0 until rows) {
                val y = row * printHeight
                sizes += PrintSize(x, y, printWidth, printHeight)
            }
        }

        var rightLeftovers = 0
        val widthLeftover = sheetWidth - printWidth * columns
        if (columns > 0 && widthLeftover >= printHeight) {
            rightLeftovers = (sheetHeight / printWidth).toInt()
        }
        var bottomLeftovers = 0
        val heightLeftover = sheetHeight - printHeight * rows
        if (rows > 0 && heightLeftover >= printWidth) {
            bottomLeftovers = (sheetWidth / printHeight).toInt()
        }
        if (BuildConfig.DEBUG) {
            println("rightLeftovers: $rightLeftovers")
            println("bottomLeftovers: $bottomLeftovers")
        }
        if (rightLeftovers > bottomLeftovers) {
            val x = printWidth * columns
            for (leftover in 0 until rightLeftovers) {
                sizes += PrintSize(x, leftover * printWidth, printHeight, printWidth)
            }
        } else if (bottomLeftovers > rightLeftovers) {
            val y = printHeight * columns
            for (leftover in 0 until bottomLeftovers) {
                sizes += PrintSize(leftover * printHeight, y, printHeight, printWidth)
            }
        }

        return sizes
    }
}