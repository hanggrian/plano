package com.hendraanggrian.plano

import org.apache.commons.math3.util.Precision.round
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class StandardSizeTest {
    @Test
    fun aSeries() = StandardSize.SERIES_A.testSeries()

    @Test
    fun bSeries() = StandardSize.SERIES_B.testSeries()

    @Test
    fun cSeries() = StandardSize.SERIES_B.testSeries()

    @Test
    fun fSeries() = StandardSize.SERIES_F.testSeries()

    private fun List<StandardSize>.testSeries() = filterIndexed { i, _ -> i != lastIndex }
        .forEachIndexed { i, (_, width, _) ->
            val next = get(i + 1)
            assertEquals(next.height, round(width / 2, 1, BigDecimal.ROUND_HALF_DOWN))
        }
}
