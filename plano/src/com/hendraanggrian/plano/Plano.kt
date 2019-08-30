package com.hendraanggrian.plano

object Plano {
    var DEBUG = false // change according to BuildConfig

    /** Using the total of 6 possible calculations, determine the most efficient of them. */
    fun calculate(
        mediaWidth: Double,
        mediaHeight: Double,
        trimWidth: Double,
        trimHeight: Double,
        bleed: Double = 0.0
    ): MediaSize = MediaSize(
        mediaWidth,
        mediaHeight,
        listOf(
            traditional(mediaWidth, mediaHeight, trimWidth + bleed * 2, trimHeight + bleed * 2),
            traditional(mediaWidth, mediaHeight, trimHeight + bleed * 2, trimWidth + bleed * 2),
            radicalColumns(mediaWidth, mediaHeight, trimWidth + bleed * 2, trimHeight + bleed * 2),
            radicalColumns(mediaWidth, mediaHeight, trimHeight + bleed * 2, trimWidth + bleed * 2),
            radicalRows(mediaWidth, mediaHeight, trimWidth + bleed * 2, trimHeight + bleed * 2),
            radicalRows(mediaWidth, mediaHeight, trimHeight + bleed * 2, trimWidth + bleed * 2)
        ).maxBy { it.size }!!
    )

    /** Lay columns and rows, then search for optional leftovers. */
    private fun traditional(
        mediaWidth: Double,
        mediaHeight: Double,
        trimWidth: Double,
        trimHeight: Double
    ): List<TrimSize> {
        if (DEBUG) {
            println("Calculating traditionally ${mediaWidth}x$mediaHeight - ${trimWidth}x$trimHeight:")
        }

        val sizes = mutableListOf<TrimSize>()
        val columns = (mediaWidth / trimWidth).toInt()
        val rows = (mediaHeight / trimHeight).toInt()
        sizes.populate(columns, rows, trimWidth, trimHeight)

        val flippedColumns =
            calculateFlippedColumns(columns, mediaWidth, mediaHeight, trimWidth, trimHeight)
        val flippedRows =
            calculateFlippedRows(rows, mediaWidth, mediaHeight, trimWidth, trimHeight)

        if (flippedColumns > flippedRows) {
            sizes.populateFlippedColumns(columns, flippedColumns, trimWidth, trimHeight)
        } else if (flippedRows > flippedColumns) {
            sizes.populateFlippedRows(rows, flippedRows, trimWidth, trimHeight)
        }

        return sizes
    }

    /** Columns are always flipped. */
    private fun radicalColumns(
        mediaWidth: Double,
        mediaHeight: Double,
        trimWidth: Double,
        trimHeight: Double
    ): List<TrimSize> {
        if (DEBUG) {
            println("Calculating radical column ${mediaWidth}x$mediaHeight - ${trimWidth}x$trimHeight:")
        }

        val sizes = mutableListOf<TrimSize>()
        val columns = ((mediaWidth - trimHeight) / trimWidth).toInt()
        val rows = (mediaHeight / trimHeight).toInt()
        sizes.populate(columns, rows, trimWidth, trimHeight)

        val flippedColumns =
            calculateFlippedColumns(columns, mediaWidth, mediaHeight, trimWidth, trimHeight)
        val flippedRows =
            calculateFlippedRows(rows, mediaWidth - trimHeight, mediaHeight, trimWidth, trimHeight)

        sizes.populateFlippedColumns(columns, flippedColumns, trimWidth, trimHeight)
        if (flippedRows > 0) {
            sizes.populateFlippedRows(rows, flippedRows, trimWidth, trimHeight)
        }
        return sizes
    }

    /** Rows are always flipped. */
    private fun radicalRows(
        mediaWidth: Double,
        mediaHeight: Double,
        trimWidth: Double,
        trimHeight: Double
    ): List<TrimSize> {
        if (DEBUG) {
            println("Calculating radical row ${mediaWidth}x$mediaHeight - ${trimWidth}x$trimHeight:")
        }

        val sizes = mutableListOf<TrimSize>()
        val columns = (mediaWidth / trimWidth).toInt()
        val rows = ((mediaHeight - trimWidth) / trimHeight).toInt()
        sizes.populate(columns, rows, trimWidth, trimHeight)

        val flippedColumns =
            calculateFlippedColumns(
                columns,
                mediaWidth,
                mediaHeight - trimWidth,
                trimWidth,
                trimHeight
            )
        val flippedRows =
            calculateFlippedRows(rows, mediaWidth, mediaHeight, trimWidth, trimHeight)

        sizes.populateFlippedRows(rows, flippedRows, trimWidth, trimHeight)
        if (flippedColumns > 0) {
            sizes.populateFlippedColumns(columns, flippedColumns, trimWidth, trimHeight)
        }
        return sizes
    }

    private fun MutableList<TrimSize>.populate(
        columns: Int,
        rows: Int,
        trimWidth: Double,
        trimHeight: Double
    ) {
        if (DEBUG) {
            println("* columns: $columns")
            println("* rows: $rows")
        }
        for (column in 0 until columns) {
            val x = column * trimWidth
            for (row in 0 until rows) {
                val y = row * trimHeight
                this += TrimSize(x, y, trimWidth, trimHeight)
            }
        }
    }

    private fun calculateFlippedColumns(
        columns: Int,
        mediaWidth: Double,
        mediaHeight: Double,
        trimWidth: Double,
        trimHeight: Double
    ): Int {
        var flippedColumns = 0
        if (columns > 0 && mediaWidth - trimWidth * columns >= trimHeight) {
            flippedColumns = (mediaHeight / trimWidth).toInt()
        }
        if (DEBUG) {
            println("* flippedColumns: $flippedColumns")
        }
        return flippedColumns
    }

    private fun calculateFlippedRows(
        rows: Int,
        mediaWidth: Double,
        mediaHeight: Double,
        trimWidth: Double,
        trimHeight: Double
    ): Int {
        var flippedRows = 0
        if (rows > 0 && mediaHeight - trimHeight * rows >= trimWidth) {
            flippedRows = (mediaWidth / trimHeight).toInt()
        }
        if (DEBUG) {
            println("* flippedRows: $flippedRows")
        }
        return flippedRows
    }

    private fun MutableList<TrimSize>.populateFlippedColumns(
        columns: Int,
        flippedColumns: Int,
        trimWidth: Double,
        trimHeight: Double
    ) {
        val x = trimWidth * columns
        for (leftover in 0 until flippedColumns) {
            this += TrimSize(x, leftover * trimWidth, trimHeight, trimWidth)
        }
    }

    private fun MutableList<TrimSize>.populateFlippedRows(
        rows: Int,
        flippedRows: Int,
        trimWidth: Double,
        trimHeight: Double
    ) {
        val y = trimHeight * rows
        for (leftover in 0 until flippedRows) {
            this += TrimSize(leftover * trimHeight, y, trimHeight, trimWidth)
        }
    }
}
