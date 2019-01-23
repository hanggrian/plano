package com.hendraanggrian.plano

import com.google.common.truth.Truth
import org.junit.Test
import kotlin.test.assertEquals

class PlanoTest {

    @Test
    fun a5inA3plus() {
        val points = Plano.getPrintRectangles(48.7, 32.5, 21.0, 14.85)
        assertEquals(4, points.size)
        Truth.assertThat(points.map { it.minX to it.minY }).containsExactly(
            21.0 to 14.85,
            0.0 to 0.0,
            0.0 to 14.85,
            21.0 to 0.0
        )
    }
}