package com.hendraanggrian.plano

class SheetPiece(override val width: Double, override val height: Double) : Paper {

    fun calculate(piece: Piece): Int {
        val targetWidth = piece.width + piece.trim * 2
        val targetHeight = piece.height + piece.trim * 2
        return maxOf(
            maxOf(
                count(width, height, targetWidth, targetHeight),
                count(width, height, targetHeight, targetWidth)
            ),
            maxOf(
                count(height, width, targetWidth, targetHeight),
                count(height, width, targetHeight, targetWidth)
            )
        )
    }
}