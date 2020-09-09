package com.hendraanggrian.plano

import java.io.Serializable

interface Box {

    val width: Double

    val height: Double

    val dimension: String get() = "${width.clean()} x ${height.clean()}"
}

class TrimBox2(
    val x: Double,
    val y: Double,
    override val width: Double,
    override val height: Double
) : Box, Serializable

class MediaBox2(
    override var width: Double,
    override var height: Double,
    private val trimBoxes: MutableList<TrimBox2> = mutableListOf()
) : Box, List<TrimBox2> by trimBoxes, Serializable {

    companion object {
        var DEBUG = false // change according to BuildConfig
    }

    private var _trimWidth: Double? = null
    private var _trimHeight: Double? = null
    private var _bleed: Double? = null
    private var _allowFlipColumn: Boolean? = null
    private var _allowFlipRow: Boolean? = null

    val trimWidth: Double get() = checkNotNull(_trimWidth) { "Must call populate at least once" }

    val trimHeight: Double get() = checkNotNull(_trimHeight) { "Must call populate at least once" }

    val bleed: Double get() = checkNotNull(_bleed) { "Must call populate at least once" }

    var allowFlipColumn: Boolean
        get() = checkNotNull(_allowFlipColumn) { "Must call populate at least once" }
        set(value) {
            _allowFlipColumn = value
            populate(trimWidth, trimHeight, bleed, value, allowFlipRow)
        }

    var allowFlipRow: Boolean
        get() = checkNotNull(_allowFlipRow) { "Must call populate at least once" }
        set(value) {
            _allowFlipRow = value
            populate(trimWidth, trimHeight, bleed, allowFlipColumn, value)
        }

    fun rotate() {
        width = height.also { height = width }
        populate(trimWidth, trimHeight, bleed, allowFlipColumn, allowFlipRow)
    }

    /** Using the total of 6 possible calculations, determine the most efficient of them. */
    fun populate(
        trimWidth: Double,
        trimHeight: Double,
        bleed: Double = 0.0,
        allowFlipColumn: Boolean = true,
        allowFlipRow: Boolean = true
    ) {
        _trimWidth = trimWidth
        _trimHeight = trimHeight
        _bleed = bleed
        _allowFlipColumn = allowFlipColumn
        _allowFlipRow = allowFlipRow
        trimBoxes.clear()
        trimBoxes.addAll(
            mutableListOf<List<TrimBox2>>().apply {
                add(
                    traditional(
                        width,
                        height,
                        trimWidth + bleed * 2,
                        trimHeight + bleed * 2,
                        allowFlipColumn,
                        allowFlipRow
                    )
                )
                add(
                    traditional(
                        width,
                        height,
                        trimHeight + bleed * 2,
                        trimWidth + bleed * 2,
                        allowFlipColumn,
                        allowFlipRow
                    )
                )
                if (allowFlipColumn) {
                    add(
                        radicalColumns(
                            width,
                            height,
                            trimWidth + bleed * 2,
                            trimHeight + bleed * 2,
                            allowFlipRow
                        )
                    )
                    add(
                        radicalColumns(
                            width,
                            height,
                            trimHeight + bleed * 2,
                            trimWidth + bleed * 2,
                            allowFlipRow
                        )
                    )
                }
                if (allowFlipRow) {
                    add(
                        radicalRows(
                            width,
                            height,
                            trimWidth + bleed * 2,
                            trimHeight + bleed * 2,
                            allowFlipColumn
                        )
                    )
                    add(
                        radicalRows(
                            width,
                            height,
                            trimHeight + bleed * 2,
                            trimWidth + bleed * 2,
                            allowFlipColumn
                        )
                    )
                }
            }.maxByOrNull { it.size }!!
        )
    }

    /** Lay columns and rows, then search for optional leftovers. */
    private fun traditional(
        mediaWidth: Double,
        mediaHeight: Double,
        trimWidth: Double,
        trimHeight: Double,
        allowFlipColumn: Boolean,
        allowFlipRow: Boolean
    ): List<TrimBox2> {
        if (DEBUG) println("Calculating traditionally ${mediaWidth}x$mediaHeight - ${trimWidth}x$trimHeight:")
        val sizes = mutableListOf<TrimBox2>()
        val columns = (mediaWidth / trimWidth).toInt()
        val rows = (mediaHeight / trimHeight).toInt()
        sizes.populate(columns, rows, trimWidth, trimHeight)

        if (allowFlipColumn) {
            val flippedColumns = measureFlippedColumns(columns, mediaWidth, mediaHeight, trimWidth, trimHeight)
            if (flippedColumns > 0) {
                sizes.populateFlippedColumns(columns, flippedColumns, trimWidth, trimHeight)
            }
        }

        if (allowFlipRow) {
            val flippedRows = measureFlippedRows(rows, mediaWidth, mediaHeight, trimWidth, trimHeight)
            if (flippedRows > 0) {
                sizes.populateFlippedRows(rows, flippedRows, trimWidth, trimHeight)
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
        allowFlipRow: Boolean
    ): List<TrimBox2> {
        if (DEBUG) println("Calculating radical column ${mediaWidth}x$mediaHeight - ${trimWidth}x$trimHeight:")
        val sizes = mutableListOf<TrimBox2>()
        val columns = ((mediaWidth - trimHeight) / trimWidth).toInt()
        val rows = (mediaHeight / trimHeight).toInt()
        sizes.populate(columns, rows, trimWidth, trimHeight)

        val flippedColumns = measureFlippedColumns(columns, mediaWidth, mediaHeight, trimWidth, trimHeight)
        sizes.populateFlippedColumns(columns, flippedColumns, trimWidth, trimHeight)
        if (allowFlipRow) {
            val flippedRows = measureFlippedRows(rows, mediaWidth - trimHeight, mediaHeight, trimWidth, trimHeight)
            if (flippedRows > 0) {
                sizes.populateFlippedRows(rows, flippedRows, trimWidth, trimHeight)
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
        allowFlipColumn: Boolean
    ): List<TrimBox2> {
        if (DEBUG) println("Calculating radical row ${mediaWidth}x$mediaHeight - ${trimWidth}x$trimHeight:")
        val sizes = mutableListOf<TrimBox2>()
        val columns = (mediaWidth / trimWidth).toInt()
        val rows = ((mediaHeight - trimWidth) / trimHeight).toInt()
        sizes.populate(columns, rows, trimWidth, trimHeight)

        val flippedRows = measureFlippedRows(rows, mediaWidth, mediaHeight, trimWidth, trimHeight)
        sizes.populateFlippedRows(rows, flippedRows, trimWidth, trimHeight)
        if (allowFlipColumn) {
            val flippedColumns =
                measureFlippedColumns(columns, mediaWidth, mediaHeight - trimWidth, trimWidth, trimHeight)
            if (flippedColumns > 0) {
                sizes.populateFlippedColumns(columns, flippedColumns, trimWidth, trimHeight)
            }
        }
        return sizes
    }

    private fun MutableList<TrimBox2>.populate(
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
                this += TrimBox2(x, y, trimWidth, trimHeight)
            }
        }
    }

    private fun measureFlippedColumns(
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
        if (DEBUG) println("* flippedColumns: $flippedColumns")
        return flippedColumns
    }

    private fun measureFlippedRows(
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
        if (DEBUG) println("* flippedRows: $flippedRows")
        return flippedRows
    }

    private fun MutableList<TrimBox2>.populateFlippedColumns(
        columns: Int,
        flippedColumns: Int,
        trimWidth: Double,
        trimHeight: Double
    ) {
        val x = trimWidth * columns
        for (leftover in 0 until flippedColumns) {
            this += TrimBox2(x, leftover * trimWidth, trimHeight, trimWidth)
        }
    }

    private fun MutableList<TrimBox2>.populateFlippedRows(
        rows: Int,
        flippedRows: Int,
        trimWidth: Double,
        trimHeight: Double
    ) {
        val y = trimHeight * rows
        for (leftover in 0 until flippedRows) {
            this += TrimBox2(leftover * trimHeight, y, trimHeight, trimWidth)
        }
    }
}
