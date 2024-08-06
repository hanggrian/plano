package com.hanggrian.plano

/** Common paper sizes as documented on [Wikipedia](https://en.wikipedia.org/wiki/Paper_size). */
public enum class StandardSize(override val width: Float, override val height: Float) : Size {
    A0(118.9f, 84.1f),
    A1(84.1f, 59.4f),
    A2(59.4f, 42f),
    A3(42f, 29.7f),
    A4(29.7f, 21f),
    A5(21f, 14.8f),
    A6(14.8f, 10.5f),
    A7(10.5f, 7.4f),
    A8(7.4f, 5.2f),
    A9(5.2f, 3.7f),
    A10(3.7f, 2.6f),

    B0(141.4f, 100f),
    B1(100f, 70.7f),
    B2(70.7f, 50f),
    B3(50f, 35.3f),
    B4(35.3f, 25f),
    B5(25f, 17.6f),
    B6(17.6f, 12.5f),
    B7(12.5f, 8.8f),
    B8(8.8f, 6.2f),
    B9(6.2f, 4.4f),
    B10(4.4f, 3.1f),

    C0(129.7f, 91.7f),
    C1(91.7f, 64.8f),
    C2(64.8f, 45.8f),
    C3(45.8f, 32.4f),
    C4(32.4f, 22.9f),
    C5(22.9f, 16.2f),
    C6(16.2f, 11.4f),
    C7(11.4f, 8.1f),
    C8(8.1f, 5.7f),
    C9(5.7f, 4f),
    C10(4f, 2.8f),

    F0(132.1f, 84.1f),
    F1(84.1f, 66f),
    F2(66f, 42f),
    F3(42f, 33f),
    F4(33f, 21f),
    F5(21f, 16.5f),
    F6(16.5f, 10.5f),
    F7(10.5f, 8.2f),
    F8(8.2f, 5.2f),
    F9(5.2f, 4.1f),
    F10(4.1f, 2.6f),
    ;

    /** For Android menu. */
    public val extendedTitle: String
        get() {
            val spaces = if (name.length > 2) " " else "    "
            return "$name$spaces\t$dimension"
        }

    public operator fun component1(): String = name

    public operator fun component2(): Float = width

    public operator fun component3(): Float = height

    public companion object {
        public val SERIES_A: List<StandardSize> =
            listOf(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10)

        public val SERIES_B: List<StandardSize> =
            listOf(B0, B1, B2, B3, B4, B5, B6, B7, B8, B9, B10)

        public val SERIES_C: List<StandardSize> =
            listOf(C0, C1, C2, C3, C4, C5, C6, C7, C8, C9, C10)

        public val SERIES_F: List<StandardSize> =
            listOf(F0, F1, F2, F3, F4, F5, F6, F7, F8, F9, F10)
    }
}
