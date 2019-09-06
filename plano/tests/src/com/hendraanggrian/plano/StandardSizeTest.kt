package com.hendraanggrian.plano

import org.apache.commons.math3.util.Precision
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class StandardSizeTest {

    @Test
    fun aSeries() {
        val aSeries = StandardSize.aSeries()
        aSeries.filterIndexed { i, _ -> i != aSeries.lastIndex }
            .forEachIndexed { i, (width, _, _) ->
                val next = aSeries[i + 1]
                assertEquals(next.height, round(width / 2))
            }
    }

    @Test
    fun bSeries() {
        val bSeries = StandardSize.bSeries()
        bSeries.filterIndexed { i, _ -> i != bSeries.lastIndex }
            .forEachIndexed { i, (width, _, _) ->
                val next = bSeries[i + 1]
                assertEquals(next.height, round(width / 2))
            }
    }

    private fun round(value: Double): Double = Precision.round(value, 1, BigDecimal.ROUND_HALF_DOWN)
}