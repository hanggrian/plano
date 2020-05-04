package com.hendraanggrian.plano

import com.hendraanggrian.plano.util.clean

/** Common paper sizes as documented on [Wikipedia](https://en.wikipedia.org/wiki/Paper_size). */
enum class PaperSize(val title: String, val width: Double, val height: Double) {
    A0("A0", 118.9, 84.1),
    A1("A1", 84.1, 59.4),
    A2("A2", 59.4, 42.0),
    A3("A3", 42.0, 29.7),
    A4("A4", 29.7, 21.0),
    A5("A5", 21.0, 14.8),
    A6("A6", 14.8, 10.5),
    A7("A7", 10.5, 7.4),
    A8("A8", 7.4, 5.2),
    A9("A9", 5.2, 3.7),
    A10("A10", 3.7, 2.6),

    B0("B0", 141.4, 100.0),
    B1("B1", 100.0, 70.7),
    B2("B2", 70.7, 50.0),
    B3("B3", 50.0, 35.3),
    B4("B4", 35.3, 25.0),
    B5("B5", 25.0, 17.6),
    B6("B6", 17.6, 12.5),
    B7("B7", 12.5, 8.8),
    B8("B8", 8.8, 6.2),
    B9("B9", 6.2, 4.4),
    B10("B10", 4.4, 3.1),

    C0("C0", 129.7, 91.7),
    C1("C1", 91.7, 64.8),
    C2("C2", 64.8, 45.8),
    C3("C3", 45.8, 32.4),
    C4("C4", 32.4, 22.9),
    C5("C5", 22.9, 16.2),
    C6("C6", 16.2, 11.4),
    C7("C7", 11.4, 8.1),
    C8("C8", 8.1, 5.7),
    C9("C9", 5.7, 4.0),
    C10("C10", 4.0, 2.8),

    F0("F0", 132.1, 84.1),
    F1("F1", 84.1, 66.0),
    F2("F2", 66.0, 42.0),
    F3("F3", 42.0, 33.0),
    F4("F4", 33.0, 21.0),
    F5("F5", 21.0, 16.5),
    F6("F6", 16.5, 10.5),
    F7("F7", 10.5, 8.2),
    F8("F8", 8.2, 5.2),
    F9("F9", 5.2, 4.1),
    F10("F10", 4.1, 2.6);

    companion object {
        val SERIES_A: List<PaperSize> = listOf(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10)
        val SERIES_B: List<PaperSize> = listOf(B0, B1, B2, B3, B4, B5, B6, B7, B8, B9, B10)
        val SERIES_C: List<PaperSize> = listOf(C0, C1, C2, C3, C4, C5, C6, C7, C8, C9, C10)
        val SERIES_F: List<PaperSize> = listOf(F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10)
    }

    val sizeText: String = "${width.clean()} x ${height.clean()}"

    operator fun component1(): String = title
    operator fun component2(): Double = width
    operator fun component3(): Double = height
}
