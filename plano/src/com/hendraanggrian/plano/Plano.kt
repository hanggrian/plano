package com.hendraanggrian.plano

object Plano {
    private var DEBUG = false // change according to BuildConfig

    fun setDebug(debug: Boolean) {
        DEBUG = debug
    }

    fun calculate(
        mediaWidth: Double,
        mediaHeight: Double,
        trimWidth: Double,
        trimHeight: Double,
        bleed: Double,
        allowFlip: Boolean
    ): List<Result> = mutableListOf<List<Result>>().apply {
        add(
            traditional(
                mediaWidth, mediaHeight,
                trimWidth + bleed * 2, trimHeight + bleed * 2,
                allowFlip
            )
        )
        add(
            traditional(
                mediaWidth, mediaHeight,
                trimHeight + bleed * 2, trimWidth + bleed * 2,
                allowFlip
            )
        )
        add(
            radicalColumns(
                mediaWidth, mediaHeight,
                trimWidth + bleed * 2, trimHeight + bleed * 2,
                allowFlip
            )
        )
        add(
            radicalColumns(
                mediaWidth, mediaHeight,
                trimHeight + bleed * 2, trimWidth + bleed * 2,
                allowFlip
            )
        )
        add(
            radicalRows(
                mediaWidth, mediaHeight,
                trimWidth + bleed * 2, trimHeight + bleed * 2,
                allowFlip
            )
        )
        add(
            radicalRows(
                mediaWidth, mediaHeight,
                trimHeight + bleed * 2, trimWidth + bleed * 2,
                allowFlip
            )
        )
    }.maxBy { it.size }!!

    /** Lay columns and rows, then search for optional leftovers. */
    private fun traditional(
        mediaWidth: Double,
        mediaHeight: Double,
        trimWidth: Double,
        trimHeight: Double,
        allowFlip: Boolean
    ): List<Result> {
        if (DEBUG) println("Calculating traditionally ${mediaWidth}x$mediaHeight - ${trimWidth}x$trimHeight:")

        val sizes = mutableListOf<Result>()
        val columns = (mediaWidth / trimWidth).toInt()
        val rows = (mediaHeight / trimHeight).toInt()
        sizes.populate(trimWidth, trimHeight, columns, rows)

        if (allowFlip) {
            val flippedColumns = calculateFlippedColumns(mediaWidth, mediaHeight, trimWidth, trimHeight, columns)
            val flippedRows = calculateFlippedRows(mediaWidth, mediaHeight, trimWidth, trimHeight, rows)

            when {
                flippedColumns > flippedRows ->
                    sizes.populateFlippedColumns(trimWidth, trimHeight, columns, flippedColumns)
                flippedRows > flippedColumns ->
                    sizes.populateFlippedRows(trimWidth, trimHeight, rows, flippedRows)
            }
        }
        return sizes
    }

    /** Columns are always flipped. */
    private fun radicalColumns(
        mediaWidth: Double,
        mediaHeight: Double,
        trimWidth: Double,
        trimHeight: Double,
        allowFlip: Boolean
    ): List<Result> {
        if (DEBUG) println("Calculating radical column ${mediaWidth}x$mediaHeight - ${trimWidth}x$trimHeight:")

        val sizes = mutableListOf<Result>()
        val columns = ((mediaWidth - trimHeight) / trimWidth).toInt()
        val rows = (mediaHeight / trimHeight).toInt()
        sizes.populate(trimWidth, trimHeight, columns, rows)

        if (allowFlip) {
            val flippedColumns = calculateFlippedColumns(mediaWidth, mediaHeight, trimWidth, trimHeight, columns)
            val flippedRows = calculateFlippedRows(mediaWidth - trimHeight, mediaHeight, trimWidth, trimHeight, rows)

            sizes.populateFlippedColumns(trimWidth, trimHeight, columns, flippedColumns)
            if (flippedRows > 0) {
                sizes.populateFlippedRows(trimWidth, trimHeight, rows, flippedRows)
            }
        }
        return sizes
    }

    /** Rows are always flipped. */
    private fun radicalRows(
        mediaWidth: Double,
        mediaHeight: Double,
        trimWidth: Double,
        trimHeight: Double,
        allowFlip: Boolean
    ): List<Result> {
        if (DEBUG) println("Calculating radical row ${mediaWidth}x$mediaHeight - ${trimWidth}x$trimHeight:")

        val sizes = mutableListOf<Result>()
        val columns = (mediaWidth / trimWidth).toInt()
        val rows = ((mediaHeight - trimWidth) / trimHeight).toInt()
        sizes.populate(trimWidth, trimHeight, columns, rows)

        if (allowFlip) {
            val flippedColumns =
                calculateFlippedColumns(mediaWidth, mediaHeight - trimWidth, trimWidth, trimHeight, columns)
            val flippedRows = calculateFlippedRows(mediaWidth, mediaHeight, trimWidth, trimHeight, rows)

            sizes.populateFlippedRows(trimWidth, trimHeight, rows, flippedRows)
            if (flippedColumns > 0) {
                sizes.populateFlippedColumns(trimWidth, trimHeight, columns, flippedColumns)
            }
        }
        return sizes
    }

    private fun MutableList<Result>.populate(
        trimWidth: Double,
        trimHeight: Double,
        columns: Int,
        rows: Int
    ) {
        if (DEBUG) {
            println("* columns: $columns")
            println("* rows: $rows")
        }
        for (column in 0 until columns) {
            val x = column * trimWidth
            for (row in 0 until rows) {
                val y = row * trimHeight
                this += Result(x, y, trimWidth, trimHeight)
            }
        }
    }

    private fun calculateFlippedColumns(
        mediaWidth: Double,
        mediaHeight: Double,
        trimWidth: Double,
        trimHeight: Double,
        columns: Int
    ): Int {
        var flippedColumns = 0
        if (columns > 0 && mediaWidth - trimWidth * columns >= trimHeight) {
            flippedColumns = (mediaHeight / trimWidth).toInt()
        }
        if (DEBUG) println("* flippedColumns: $flippedColumns")
        return flippedColumns
    }

    private fun calculateFlippedRows(
        mediaWidth: Double,
        mediaHeight: Double,
        trimWidth: Double,
        trimHeight: Double,
        rows: Int
    ): Int {
        var flippedRows = 0
        if (rows > 0 && mediaHeight - trimHeight * rows >= trimWidth) {
            flippedRows = (mediaWidth / trimHeight).toInt()
        }
        if (DEBUG) println("* flippedRows: $flippedRows")
        return flippedRows
    }

    private fun MutableList<Result>.populateFlippedColumns(
        trimWidth: Double,
        trimHeight: Double,
        columns: Int,
        flippedColumns: Int
    ) {
        val x = trimWidth * columns
        for (leftover in 0 until flippedColumns) {
            this += Result(x, leftover * trimWidth, trimHeight, trimWidth)
        }
    }

    private fun MutableList<Result>.populateFlippedRows(
        trimWidth: Double,
        trimHeight: Double,
        rows: Int,
        flippedRows: Int
    ) {
        val y = trimHeight * rows
        for (leftover in 0 until flippedRows) {
            this += Result(leftover * trimHeight, y, trimHeight, trimWidth)
        }
    }

    data class Result(
        val x: Double,
        val y: Double,
        val width: Double,
        val height: Double
    )
}
