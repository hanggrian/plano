package com.hendraanggrian.plano

import org.apache.commons.math3.util.Precision.round
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class PaperSeriesTest {

    @Test fun aSeries() = PaperSeries.A.testSeries()

    @Test fun bSeries() = PaperSeries.B.testSeries()

    @Test fun cSeries() = PaperSeries.C.testSeries()

    private fun List<PaperSeries>.testSeries() = filterIndexed { i, _ -> i != lastIndex }
        .forEachIndexed { i, (width, _, _) ->
            val next = get(i + 1)
            assertEquals(next.height, round(width / 2, 1, BigDecimal.ROUND_HALF_DOWN))
        }
}