package com.hendraanggrian.plano

import org.apache.commons.math3.util.Precision.round
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class SizeSeriesTest {

    @Test fun aSeries() = SizeSeries.A.testSeries()

    @Test fun bSeries() = SizeSeries.B.testSeries()

    @Test fun cSeries() = SizeSeries.C.testSeries()

    private fun List<SizeSeries>.testSeries() = filterIndexed { i, _ -> i != lastIndex }
        .forEachIndexed { i, (width, _, _) ->
            val next = get(i + 1)
            assertEquals(next.height, round(width / 2, 1, BigDecimal.ROUND_HALF_DOWN))
        }
}