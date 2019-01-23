package com.hendraanggrian.plano

import com.google.common.truth.Truth
import org.junit.Test
import kotlin.test.assertEquals

class PlanoTest {

    @Test
    fun a5inA3plus() {
        val sizes = Plano.getPrintSizes(48.7, 32.5, 21.0, 14.85)
        assertEquals(4, sizes.size)
        Truth.assertThat(sizes.map { it.minX to it.minY }).containsExactly(
            21.0 to 14.85,
            0.0 to 0.0,
            0.0 to 14.85,
            21.0 to 0.0
        )
    }
}