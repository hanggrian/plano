package com.hendraanggrian.plano

import javafx.geometry.Rectangle2D

object Plano {

    fun getPrintRectangles(
        sheetWidth: Double,
        sheetHeight: Double,
        printWidth: Double,
        printHeight: Double,
        trim: Double = 0.0
    ): List<Rectangle2D> {
        val rectangles1 = calculatePrintPoints(
            sheetWidth,
            sheetHeight,
            printWidth + trim,
            printHeight + trim
        )
        val rectangles2 = calculatePrintPoints(
            sheetWidth,
            sheetHeight,
            printHeight + trim,
            printWidth + trim
        )
        return when {
            rectangles1.size >= rectangles2.size -> {
                if (BuildConfig.DEBUG) println("Choice 1 chosen")
                rectangles1
            }
            else -> {
                if (BuildConfig.DEBUG) println("Choice 2 chosen")
                rectangles2
            }
        }
    }

    private fun calculatePrintPoints(
        sheetWidth: Double,
        sheetHeight: Double,
        printWidth: Double,
        printHeight: Double
    ): List<Rectangle2D> {
        if (BuildConfig.DEBUG) println("----- ${sheetWidth}x$sheetHeight - ${printWidth}x$printHeight -----")

        val rectangles = mutableListOf<Rectangle2D>()
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
                rectangles += Rectangle2D(x, y, printWidth, printHeight)
            }
        }

        var rightLeftovers = 0
        val widthLeftover = sheetWidth - printWidth * columns
        if (columns > 0 && widthLeftover >= printHeight) {
            rightLeftovers = (widthLeftover / printHeight).toInt()
        }
        var bottomLeftovers = 0
        val heightLeftover = sheetHeight - printHeight * rows
        if (rows > 0 && heightLeftover >= printWidth) {
            bottomLeftovers = (heightLeftover / printWidth).toInt()
        }
        if (BuildConfig.DEBUG) {
            println("rightLeftovers: $rightLeftovers")
            println("bottomLeftovers: $bottomLeftovers")
        }
        if (rightLeftovers > bottomLeftovers) {
            val x = printWidth * columns
            for (leftover in 0 until rightLeftovers) {
                rectangles += Rectangle2D(x, leftover * printHeight, printHeight, printWidth)
            }
        } else if (bottomLeftovers > rightLeftovers) {
            val y = printHeight * columns
            for (leftover in 0 until bottomLeftovers) {
                rectangles += Rectangle2D(leftover * printWidth, y, printHeight, printWidth)
            }
        }

        return rectangles
    }
}