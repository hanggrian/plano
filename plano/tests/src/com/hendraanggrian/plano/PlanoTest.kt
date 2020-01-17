package com.hendraanggrian.plano

import com.google.common.truth.Truth
import kotlin.test.Test
import kotlin.test.assertEquals

class PlanoTest {

    @Test fun a5inA3plus() {
        val mediaBox = Plano.calculate(48.7, 32.5, 21.0, 14.85)
        assertEquals(4, mediaBox.size)
        Truth.assertThat(mediaBox.map { it.x to it.y }).containsExactly(
            0.0 to 0.0,
            21.0 to 0.0,
            0.0 to 14.85,
            21.0 to 14.85
        )
    }

    @Test fun f4in79109() {
        val mediaBox = Plano.calculate(79.0, 109.0, 21.5, 33.0)
        assertEquals(11, mediaBox.size)
        Truth.assertThat(mediaBox.map { it.x to it.y }).containsExactly(
            0.0 to 0.0,
            21.5 to 0.0,
            0.0 to 33.0,
            21.5 to 33.0,
            0.0 to 66.0,
            21.5 to 66.0,
            43.0 to 0.0,
            43.0 to 21.5,
            43.0 to 43.0,
            43.0 to 64.5,
            43.0 to 86.0
        )
    }
}