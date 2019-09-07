package com.hendraanggrian.plano

@Suppress("EnumEntryName")
enum class StandardSize {
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

    _61_86(61.0, 86.0),
    _61_92(61.0, 92.0),
    _65_90(65.0, 90.0),
    _65_100(65.0, 100.0),
    _79_109(79.0, 109.0),
    _70_108(70.0, 108.0),
    _86_106(86.0, 106.0);

    companion object {

        fun aSeries(): List<StandardSize> = listOf(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10)

        fun bSeries(): List<StandardSize> = listOf(B0, B1, B2, B3, B4, B5, B6, B7, B8, B9, B10)

        fun others(): List<StandardSize> =
            listOf(_61_86, _61_92, _65_90, _65_100, _79_109, _70_108, _86_106)
    }

    val width: Double
    val height: Double
    val title: String

    constructor(width: Double, height: Double) {
        this.width = width
        this.height = height
        this.title = "${width.tidy()} x ${height.tidy()}"
    }

    constructor(titlePrefix: String, width: Double, height: Double) {
        this.width = width
        this.height = height
        this.title = "$titlePrefix (${width.tidy()} x ${height.tidy()})"
    }

    operator fun component1(): Double = width
    operator fun component2(): Double = height
    operator fun component3(): String = title

    private fun Double.tidy(): String = toString().let {
        when {
            it.endsWith(".0") -> it.substringBeforeLast(".0")
            else -> it
        }
    }
}
