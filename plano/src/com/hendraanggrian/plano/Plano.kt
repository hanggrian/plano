package com.hendraanggrian.plano

import javafx.geometry.Point2D

object Plano {

    fun getPrintPoints() {
    }

    fun getPrintPoints(
        planoWidth: Double,
        planoHeight: Double,
        printWidth: Double,
        printHeight: Double,
        trim: Double = 0.0
    ): List<Point2D> {
        val points1 = calculatePrintPoints(
            planoWidth,
            planoHeight,
            printWidth + trim,
            printHeight + trim
        )
        val points2 = calculatePrintPoints(
            planoWidth,
            planoHeight,
            printHeight + trim,
            printWidth + trim
        )
        return when {
            points1.size > points2.size -> {
                println("possible 1 chosen")
                points1
            }
            else -> {
                println("possible 2 chosen")
                points2
            }
        }
    }

    private fun calculatePrintPoints(
        planoWidth: Double,
        planoHeight: Double,
        printWidth: Double,
        printHeight: Double
    ): List<Point2D> {
        println("=================")
        val points = mutableListOf<Point2D>()
        val columns = (planoWidth / printWidth).toInt()
        val rows = (planoHeight / printHeight).toInt()
        println("columns: $columns")
        println("rows: $rows")
        for (column in 0 until columns) {
            val x = column * printWidth
            for (row in 0 until rows) {
                val y = row * printHeight
                points += Point2D(x, y)
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
                points += Point2D(x, leftover * printHeight)
            }
        } else {
            val y = printHeight * columns
            for (leftover in 0 until bottomLeftovers) {
                points += Point2D(leftover * printWidth, y)
            }
        }

        return points
    }
}