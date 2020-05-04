package com.hendraanggrian.plano

import org.apache.commons.math3.util.Precision.round
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class PaperSizeTest {

    @Test fun aSeries() = PaperSize.SERIES_A.testSeries()
    @Test fun bSeries() = PaperSize.SERIES_B.testSeries()
    @Test fun cSeries() = PaperSize.SERIES_B.testSeries()
    @Test fun fSeries() = PaperSize.SERIES_F.testSeries()

    private fun List<PaperSize>.testSeries() = filterIndexed { i, _ -> i != lastIndex }
        .forEachIndexed { i, (_, width, _) ->
            val next = get(i + 1)
            assertEquals(next.height, round(width / 2, 1, BigDecimal.ROUND_HALF_DOWN))
        }
}