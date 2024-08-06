package com.hanggrian.plano

import java.io.Serializable

public class MediaSize(
    override var width: Float,
    override var height: Float,
    private val trimBoxes: MutableList<TrimSize> = mutableListOf(),
) : Size,
    List<TrimSize> by trimBoxes,
    Serializable {
    private var _trimWidth: Float? = null
    private var _trimHeight: Float? = null
    private var _gapHorizontal: Float? = null
    private var _gapVertical: Float? = null
    private var _isAllowFlipRight: Boolean? = null
    private var _isAllowFlipBottom: Boolean? = null

    public val trimWidth: Float
        get() = checkNotNull(_trimWidth) { "Must call populate at least once" }

    public val trimHeight: Float
        get() = checkNotNull(_trimHeight) { "Must call populate at least once" }

    public val gapHorizontal: Float
        get() = checkNotNull(_gapHorizontal) { "Must call populate at least once" }

    public val gapVertical: Float
        get() = checkNotNull(_gapVertical) { "Must call populate at least once" }

    public var isAllowFlipRight: Boolean
        get() = checkNotNull(_isAllowFlipRight) { "Must call populate at least once" }
        set(value) {
            _isAllowFlipRight = value
            populate(trimWidth, trimHeight, gapHorizontal, gapVertical, value, isAllowFlipBottom)
        }

    public var isAllowFlipBottom: Boolean
        get() = checkNotNull(_isAllowFlipBottom) { "Must call populate at least once" }
        set(value) {
            _isAllowFlipBottom = value
            populate(trimWidth, trimHeight, gapHorizontal, gapVertical, isAllowFlipRight, value)
        }

    public fun rotate() {
        width = height.also { height = width }
        populate(
            trimWidth,
            trimHeight,
            gapHorizontal,
            gapVertical,
            isAllowFlipRight,
            isAllowFlipBottom,
        )
    }

    /** Using the total of 6 possible calculations, determine the most efficient of them. */
    public fun populate(
        trimWidth: Float,
        trimHeight: Float,
        gapHorizontal: Float,
        gapVertical: Float,
        allowFlipRight: Boolean,
        allowFlipBottom: Boolean,
    ) {
        _trimWidth = trimWidth
        _trimHeight = trimHeight
        _gapHorizontal = gapHorizontal
        _gapVertical = gapVertical
        _isAllowFlipRight = allowFlipRight
        _isAllowFlipBottom = allowFlipBottom
        trimBoxes.clear()
        trimBoxes.addAll(
            buildList {
                add(
                    traditional(
                        width,
                        height,
                        trimWidth,
                        trimHeight,
                        gapHorizontal,
                        gapVertical,
                        allowFlipRight,
                        allowFlipBottom,
                    ),
                )
                add(
                    traditional(
                        width,
                        height,
                        trimHeight,
                        trimWidth,
                        gapHorizontal,
                        gapVertical,
                        allowFlipRight,
                        allowFlipBottom,
                    ),
                )
                if (allowFlipRight) {
                    add(
                        alwaysFlipRight(
                            width,
                            height,
                            trimWidth,
                            trimHeight,
                            gapHorizontal,
                            gapVertical,
                            allowFlipBottom,
                        ),
                    )
                    add(
                        alwaysFlipRight(
                            width,
                            height,
                            trimHeight,
                            trimWidth,
                            gapHorizontal,
                            gapVertical,
                            allowFlipBottom,
                        ),
                    )
                }
                if (!allowFlipBottom) {
                    return@buildList
                }
                add(
                    alwaysFlipBottom(
                        width,
                        height,
                        trimWidth,
                        trimHeight,
                        gapHorizontal,
                        gapVertical,
                        allowFlipRight,
                    ),
                )
                add(
                    alwaysFlipBottom(
                        width,
                        height,
                        trimHeight,
                        trimWidth,
                        gapHorizontal,
                        gapVertical,
                        allowFlipRight,
                    ),
                )
            }.maxByOrNull { it.size }!!,
        )
    }

    /** Lay columns and rows, then search for optional leftovers. */
    private fun traditional(
        mediaWidth: Float,
        mediaHeight: Float,
        trimWidth: Float,
        trimHeight: Float,
        horizontalGap: Float,
        verticalGap: Float,
        flipRight: Boolean,
        flipBottom: Boolean,
    ): List<TrimSize> {
        println(
            "Calculating traditionally ${mediaWidth}x$mediaHeight - " +
                "${trimWidth}x$trimHeight:",
        )
        val finalTrimWidth = trimWidth + horizontalGap
        val finalTrimHeight = trimHeight + verticalGap
        val sizes = mutableListOf<TrimSize>()
        val columns = (mediaWidth / finalTrimWidth).toInt()
        val rows = (mediaHeight / finalTrimHeight).toInt()
        sizes.populate(columns, rows, trimWidth, trimHeight, horizontalGap, verticalGap)
        if (flipRight) {
            val flippedRights =
                measureFlippedRights(
                    columns,
                    mediaWidth,
                    mediaHeight,
                    trimWidth,
                    trimHeight,
                    horizontalGap,
                    verticalGap,
                )
            if (flippedRights.first > 0) {
                sizes.populateFlippedRights(
                    columns,
                    flippedRights,
                    trimWidth,
                    trimHeight,
                    horizontalGap,
                    verticalGap,
                )
            }
        }
        if (flipBottom) {
            val flippedBottoms =
                measureFlippedBottoms(
                    rows,
                    mediaWidth,
                    mediaHeight,
                    trimWidth,
                    trimHeight,
                    horizontalGap,
                    verticalGap,
                )
            if (flippedBottoms.second > 0) {
                sizes.populateFlippedBottoms(
                    rows,
                    flippedBottoms,
                    trimWidth,
                    trimHeight,
                    horizontalGap,
                    verticalGap,
                )
            }
        }
        return sizes
    }

    /** Columns are always flipped. */
    private fun alwaysFlipRight(
        mediaWidth: Float,
        mediaHeight: Float,
        trimWidth: Float,
        trimHeight: Float,
        horizontalGap: Float,
        verticalGap: Float,
        flipBottom: Boolean,
    ): List<TrimSize> {
        println(
            "Calculating radical column ${mediaWidth}x$mediaHeight - " +
                "${trimWidth}x$trimHeight:",
        )
        val finalTrimWidth = trimWidth + horizontalGap
        val finalTrimHeight = trimHeight + verticalGap
        val sizes = mutableListOf<TrimSize>()
        val columns = ((mediaWidth - finalTrimHeight) / finalTrimWidth).toInt()
        val rows = (mediaHeight / finalTrimHeight).toInt()
        sizes.populate(columns, rows, trimWidth, trimHeight, horizontalGap, verticalGap)
        val flippedColumns =
            measureFlippedRights(
                columns,
                mediaWidth,
                mediaHeight,
                trimWidth,
                trimHeight,
                horizontalGap,
                verticalGap,
            )
        sizes.populateFlippedRights(
            columns,
            flippedColumns,
            trimWidth,
            trimHeight,
            horizontalGap,
            verticalGap,
        )
        if (flipBottom) {
            val flippedBottoms =
                measureFlippedBottoms(
                    rows,
                    mediaWidth - trimHeight,
                    mediaHeight,
                    trimWidth,
                    trimHeight,
                    horizontalGap,
                    verticalGap,
                )
            if (flippedBottoms.second > 0) {
                sizes.populateFlippedBottoms(
                    rows,
                    flippedBottoms,
                    trimWidth,
                    trimHeight,
                    horizontalGap,
                    verticalGap,
                )
            }
        }
        return sizes
    }

    /** Rows are always flipped. */
    private fun alwaysFlipBottom(
        mediaWidth: Float,
        mediaHeight: Float,
        trimWidth: Float,
        trimHeight: Float,
        horizontalGap: Float,
        verticalGap: Float,
        flipRight: Boolean,
    ): List<TrimSize> {
        println(
            "Calculating radical row ${mediaWidth}x$mediaHeight - " +
                "${trimWidth}x$trimHeight:",
        )
        val finalTrimWidth = trimWidth + horizontalGap
        val finalTrimHeight = trimHeight + verticalGap
        val sizes = mutableListOf<TrimSize>()
        val columns = (mediaWidth / finalTrimWidth).toInt()
        val rows = ((mediaHeight - finalTrimWidth) / finalTrimHeight).toInt()
        sizes.populate(columns, rows, trimWidth, trimHeight, horizontalGap, verticalGap)
        val flippedBottoms =
            measureFlippedBottoms(
                rows,
                mediaWidth,
                mediaHeight,
                trimWidth,
                trimHeight,
                horizontalGap,
                verticalGap,
            )
        sizes.populateFlippedBottoms(
            rows,
            flippedBottoms,
            trimWidth,
            trimHeight,
            horizontalGap,
            verticalGap,
        )
        if (flipRight) {
            val flippedRights =
                measureFlippedRights(
                    columns,
                    mediaWidth,
                    mediaHeight - trimWidth,
                    trimWidth,
                    trimHeight,
                    horizontalGap,
                    verticalGap,
                )
            if (flippedRights.first > 0) {
                sizes.populateFlippedRights(
                    columns,
                    flippedRights,
                    trimWidth,
                    trimHeight,
                    horizontalGap,
                    verticalGap,
                )
            }
        }
        return sizes
    }

    private fun MutableList<TrimSize>.populate(
        columns: Int,
        rows: Int,
        trimWidth: Float,
        trimHeight: Float,
        horizontalGap: Float,
        verticalGap: Float,
    ) {
        println("* columns: $columns")
        println("* rows: $rows")
        val finalTrimWidth = trimWidth + horizontalGap
        val finalTrimHeight = trimHeight + verticalGap
        for (column in 0 until columns) {
            val x = column * finalTrimWidth
            for (row in 0 until rows) {
                val y = row * finalTrimHeight
                this += TrimSize(x, y, trimWidth, trimHeight)
            }
        }
    }

    private fun measureFlippedRights(
        columns: Int,
        mediaWidth: Float,
        mediaHeight: Float,
        trimWidth: Float,
        trimHeight: Float,
        horizontalGap: Float,
        verticalGap: Float,
    ): Pair<Int, Int> {
        var flippedRightColumns = 0
        var flippedRightRows = 0
        if (columns > 0) {
            val finalTrimWidth = trimWidth + horizontalGap
            val finalTrimHeight = trimHeight + verticalGap
            flippedRightColumns =
                ((mediaWidth - columns * finalTrimWidth) / finalTrimHeight).toInt()
            if (flippedRightColumns > 0) {
                flippedRightRows = (mediaHeight / finalTrimWidth).toInt()
            }
        }
        println("* flippedRightRows: $flippedRightRows")
        println("* flippedRightColumns: $flippedRightColumns")
        return flippedRightColumns to flippedRightRows
    }

    private fun measureFlippedBottoms(
        rows: Int,
        mediaWidth: Float,
        mediaHeight: Float,
        trimWidth: Float,
        trimHeight: Float,
        horizontalGap: Float,
        verticalGap: Float,
    ): Pair<Int, Int> {
        var flippedBottomColumns = 0
        var flippedBottomRows = 0
        if (rows > 0) {
            val finalTrimWidth = trimWidth + horizontalGap
            val finalTrimHeight = trimHeight + verticalGap
            flippedBottomRows = ((mediaHeight - rows * finalTrimHeight) / finalTrimWidth).toInt()
            if (flippedBottomRows > 0) {
                flippedBottomColumns = (mediaWidth / finalTrimHeight).toInt()
            }
        }
        println("* flippedBottomRows: $flippedBottomRows")
        println("* flippedBottomColumns: $flippedBottomColumns")
        return flippedBottomColumns to flippedBottomRows
    }

    private fun MutableList<TrimSize>.populateFlippedRights(
        columns: Int,
        flippedRights: Pair<Int, Int>,
        trimWidth: Float,
        trimHeight: Float,
        horizontalGap: Float,
        verticalGap: Float,
    ) {
        val finalTrimWidth = trimWidth + horizontalGap
        val finalTrimHeight = trimHeight + verticalGap
        val startX = columns * finalTrimWidth - horizontalGap + verticalGap
        for (flippedRightColumn in 0 until flippedRights.first) {
            val x = startX + flippedRightColumn * finalTrimHeight
            for (flippedRightRow in 0 until flippedRights.second) {
                val y = flippedRightRow * finalTrimWidth
                this += TrimSize(x, y, trimHeight, trimWidth)
            }
        }
    }

    private fun MutableList<TrimSize>.populateFlippedBottoms(
        rows: Int,
        flippedBottoms: Pair<Int, Int>,
        trimWidth: Float,
        trimHeight: Float,
        horizontalGap: Float,
        verticalGap: Float,
    ) {
        val finalTrimWidth = trimWidth + horizontalGap
        val finalTrimHeight = trimHeight + verticalGap
        val startY = rows * finalTrimHeight - verticalGap + horizontalGap
        for (flippedBottomRow in 0 until flippedBottoms.second) {
            val y = startY + flippedBottomRow * finalTrimWidth
            for (flippedBottomColumn in 0 until flippedBottoms.first) {
                val x = flippedBottomColumn * finalTrimHeight
                this += TrimSize(x, y, trimHeight, trimWidth)
            }
        }
    }
}
