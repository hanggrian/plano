package com.hendraanggrian.plano

import java.io.Serializable

class MediaSize(
    override var width: Float,
    override var height: Float,
    private val trimBoxes: MutableList<TrimSize> = mutableListOf()
) : Size, List<TrimSize> by trimBoxes, Serializable {

    companion object {
        var DEBUG = false // change according to BuildConfig
    }

    private var _trimWidth: Float? = null
    private var _trimHeight: Float? = null
    private var _gapHorizontal: Float? = null
    private var _gapVertical: Float? = null
    private var _allowFlipColumn: Boolean? = null
    private var _allowFlipRow: Boolean? = null

    val trimWidth: Float get() = checkNotNull(_trimWidth) { "Must call populate at least once" }

    val trimHeight: Float get() = checkNotNull(_trimHeight) { "Must call populate at least once" }

    val gapHorizontal: Float get() = checkNotNull(_gapHorizontal) { "Must call populate at least once" }

    val gapVertical: Float get() = checkNotNull(_gapVertical) { "Must call populate at least once" }

    var allowFlipColumn: Boolean
        get() = checkNotNull(_allowFlipColumn) { "Must call populate at least once" }
        set(value) {
            _allowFlipColumn = value
            populate(trimWidth, trimHeight, gapHorizontal, gapVertical, value, allowFlipRow)
        }

    var allowFlipRow: Boolean
        get() = checkNotNull(_allowFlipRow) { "Must call populate at least once" }
        set(value) {
            _allowFlipRow = value
            populate(trimWidth, trimHeight, gapHorizontal, gapVertical, allowFlipColumn, value)
        }

    fun rotate() {
        width = height.also { height = width }
        populate(trimWidth, trimHeight, gapHorizontal, gapVertical, allowFlipColumn, allowFlipRow)
    }

    /** Using the total of 6 possible calculations, determine the most efficient of them. */
    fun populate(
        trimWidth: Float,
        trimHeight: Float,
        gapHorizontal: Float,
        gapVertical: Float,
        allowFlipColumn: Boolean,
        allowFlipRow: Boolean
    ) {
        _trimWidth = trimWidth; _trimHeight = trimHeight
        _gapHorizontal = gapHorizontal; _gapVertical = gapVertical
        _allowFlipColumn = allowFlipColumn; _allowFlipRow = allowFlipRow
        trimBoxes.clear()
        trimBoxes.addAll(
            mutableListOf<List<TrimSize>>().apply {
                add(
                    traditional(
                        width, height,
                        trimWidth, trimHeight,
                        gapHorizontal, gapVertical,
                        allowFlipColumn, allowFlipRow
                    )
                )
                add(
                    traditional(
                        width, height,
                        trimHeight, trimWidth,
                        gapHorizontal, gapVertical,
                        allowFlipColumn, allowFlipRow
                    )
                )
                if (allowFlipColumn) {
                    add(
                        alwaysFlipColumn(
                            width, height,
                            trimWidth, trimHeight,
                            gapHorizontal, gapVertical,
                            allowFlipRow
                        )
                    )
                    add(
                        alwaysFlipColumn(
                            width, height,
                            trimHeight, trimWidth,
                            gapHorizontal, gapVertical,
                            allowFlipRow
                        )
                    )
                }
                if (allowFlipRow) {
                    add(
                        alwaysFlipRow(
                            width, height,
                            trimWidth, trimHeight,
                            gapHorizontal, gapVertical,
                            allowFlipColumn
                        )
                    )
                    add(
                        alwaysFlipRow(
                            width, height,
                            trimHeight, trimWidth,
                            gapHorizontal, gapVertical,
                            allowFlipColumn
                        )
                    )
                }
            }.maxByOrNull { it.size }!!
        )
    }

    /** Lay columns and rows, then search for optional leftovers. */
    private fun traditional(
        mwidth: Float,
        mheight: Float,
        twidth: Float,
        theight: Float,
        hgap: Float,
        vgap: Float,
        fcolumn: Boolean,
        frow: Boolean
    ): List<TrimSize> {
        if (DEBUG) println("Calculating traditionally ${mwidth}x$mheight - ${twidth}x$theight:")
        val sizes = mutableListOf<TrimSize>()
        val columns = (mwidth / (twidth + hgap)).toInt()
        val rows = (mheight / (theight + vgap)).toInt()
        sizes.populate(columns, rows, twidth, theight, hgap, vgap)

        if (fcolumn) {
            val flippedColumns = measureFlippedColumns(columns, mwidth, mheight, twidth, theight, hgap, vgap)
            if (flippedColumns > 0) {
                sizes.populateFlippedColumns(columns, flippedColumns, twidth, theight, hgap, vgap)
            }
        }

        if (frow) {
            val flippedRows = measureFlippedRows(rows, mwidth, mheight, twidth, theight, hgap, vgap)
            if (flippedRows > 0) {
                sizes.populateFlippedRows(rows, flippedRows, twidth, theight, hgap, vgap)
            }
        }
        return sizes
    }

    /** Columns are always flipped. */
    private fun alwaysFlipColumn(
        mwidth: Float,
        mheight: Float,
        twidth: Float,
        theight: Float,
        hgap: Float,
        vgap: Float,
        frow: Boolean
    ): List<TrimSize> {
        if (DEBUG) println("Calculating radical column ${mwidth}x$mheight - ${twidth}x$theight:")
        val sizes = mutableListOf<TrimSize>()
        val columns = ((mwidth - theight + vgap) / (twidth + hgap)).toInt()
        val rows = (mheight / (theight + vgap)).toInt()
        sizes.populate(columns, rows, twidth, theight, hgap, vgap)

        val flippedColumns = measureFlippedColumns(columns, mwidth, mheight, twidth, theight, hgap, vgap)
        sizes.populateFlippedColumns(columns, flippedColumns, twidth, theight, hgap, vgap)
        if (frow) {
            val flippedRows = measureFlippedRows(rows, mwidth - theight, mheight, twidth, theight, hgap, vgap)
            if (flippedRows > 0) {
                sizes.populateFlippedRows(rows, flippedRows, twidth, theight, hgap, vgap)
            }
        }
        return sizes
    }

    /** Rows are always flipped. */
    private fun alwaysFlipRow(
        mwdith: Float,
        mheight: Float,
        twidth: Float,
        theight: Float,
        hgap: Float,
        vgap: Float,
        fcolumn: Boolean
    ): List<TrimSize> {
        if (DEBUG) println("Calculating radical row ${mwdith}x$mheight - ${twidth}x$theight:")
        val sizes = mutableListOf<TrimSize>()
        val columns = (mwdith / (twidth + hgap)).toInt()
        val rows = ((mheight - twidth + hgap) / (theight + vgap)).toInt()
        sizes.populate(columns, rows, twidth, theight, hgap, vgap)

        val flippedRows = measureFlippedRows(rows, mwdith, mheight, twidth, theight, hgap, vgap)
        sizes.populateFlippedRows(rows, flippedRows, twidth, theight, hgap, vgap)
        if (fcolumn) {
            val flippedColumns = measureFlippedColumns(columns, mwdith, mheight - twidth, twidth, theight, hgap, vgap)
            if (flippedColumns > 0) {
                sizes.populateFlippedColumns(columns, flippedColumns, twidth, theight, hgap, vgap)
            }
        }
        return sizes
    }

    private fun MutableList<TrimSize>.populate(
        columns: Int,
        rows: Int,
        twidth: Float,
        theight: Float,
        hgap: Float,
        vgap: Float
    ) {
        if (DEBUG) {
            println("* columns: $columns")
            println("* rows: $rows")
        }
        for (column in 0 until columns) {
            val x = column * (twidth + hgap)
            for (row in 0 until rows) {
                val y = row * (theight + vgap)
                this += TrimSize(x, y, twidth, theight)
            }
        }
    }

    private fun measureFlippedColumns(
        columns: Int,
        mwidth: Float,
        mheight: Float,
        twidth: Float,
        theight: Float,
        hgap: Float,
        vgap: Float
    ): Int {
        var flippedColumns = 0
        if (columns > 0 && mwidth - ((twidth + hgap) * columns) >= theight + vgap) {
            flippedColumns = (mheight / (twidth + hgap)).toInt()
        }
        if (DEBUG) println("* flippedColumns: $flippedColumns")
        return flippedColumns
    }

    private fun measureFlippedRows(
        rows: Int,
        mwidth: Float,
        mheight: Float,
        twidth: Float,
        theight: Float,
        hgap: Float,
        vgap: Float
    ): Int {
        var flippedRows = 0
        if (rows > 0 && mheight - ((theight + vgap) * rows) >= twidth + hgap) {
            flippedRows = (mwidth / (theight + vgap)).toInt()
        }
        if (DEBUG) println("* flippedRows: $flippedRows")
        return flippedRows
    }

    private fun MutableList<TrimSize>.populateFlippedColumns(
        columns: Int,
        flippedColumns: Int,
        twidth: Float,
        theight: Float,
        hgap: Float,
        vgap: Float
    ) {
        val x = (twidth + hgap) * columns - hgap + vgap
        for (leftover in 0 until flippedColumns) {
            val y = leftover * (twidth + hgap)
            this += TrimSize(x, y, theight, twidth)
        }
    }

    private fun MutableList<TrimSize>.populateFlippedRows(
        rows: Int,
        flippedRows: Int,
        twidth: Float,
        theight: Float,
        hgap: Float,
        vgap: Float
    ) {
        val y = (theight + vgap) * rows - vgap + hgap
        for (leftover in 0 until flippedRows) {
            val x = leftover * (theight + vgap)
            this += TrimSize(x, y, theight, twidth)
        }
    }
}
