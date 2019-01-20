package com.hendraanggrian.plano

object Plano {

    fun getPrintSizes() {

    }

    fun getPrintSizes(
        planoWidth: Double,
        planoHeight: Double,
        printWidth: Double,
        printHeight: Double,
        trim: Double = 0.0
    ): List<Size> {
        val pcs1 = calculatePrintingSizes(planoWidth, planoHeight, printWidth + trim, printHeight + trim)
        val pcs2 = calculatePrintingSizes(planoWidth, planoHeight, printHeight + trim, printWidth + trim)
        return when {
            pcs1.size > pcs2.size -> {
                println("possible 1 chosen")
                pcs1
            }
            else -> {
                println("possible 2 chosen")
                pcs2
            }
        }
    }

    private fun calculatePrintingSizes(
        planoWidth: Double,
        planoHeight: Double,
        printWidth: Double,
        printHeight: Double
    ): List<Size> {
        println("=================")
        val pieces = mutableListOf<Size>()
        val columns = (planoWidth / printWidth).toInt()
        val rows = (planoHeight / printHeight).toInt()
        println("columns: $columns")
        println("rows: $rows")
        for (column in 0 until columns) {
            val x = column * printWidth
            for (row in 0 until rows) {
                val y = row * printHeight
                pieces += Size(x, y, printWidth, printHeight)
            }
        }

        var rightLeftovers = 0
        val widthLeftover = planoWidth - printWidth * columns
        if (widthLeftover >= printHeight) {
            rightLeftovers = (widthLeftover / printHeight).toInt()
        }
        var bottomLeftovers = 0
        val heightLeftover = planoHeight - printHeight * rows
        if (heightLeftover >= printWidth) {
            bottomLeftovers = (heightLeftover / printWidth).toInt()
        }
        if (rightLeftovers > bottomLeftovers) {
            val x = printWidth * columns
            for (leftover in 0 until rightLeftovers) {
                pieces += Size(x, leftover * printHeight, printWidth, printHeight)
            }
        } else {
            val y = printHeight * columns
            for (leftover in 0 until bottomLeftovers) {
                pieces += Size(leftover * printWidth, y, printWidth, printHeight)
            }
        }

        return pieces
    }
}