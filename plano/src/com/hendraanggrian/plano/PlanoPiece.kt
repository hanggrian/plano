package com.hendraanggrian.plano

class PlanoPiece(override val width: Double, override val height: Double) : Paper {

    fun calculate(paper: SheetPiece): Int = maxOf(
        count(width, height, paper.width, paper.height),
        count(width, height, paper.height, paper.width)
    )

    fun calculate(piece: Piece) {
    }
}