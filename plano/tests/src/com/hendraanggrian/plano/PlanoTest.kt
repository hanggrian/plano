package com.hendraanggrian.plano

import com.google.common.truth.Truth.assertThat
import kotlin.test.Test
import kotlin.test.assertEquals

class PlanoTest {

    @Test
    fun a5inA3plus() {
        val mediaBox = MediaSize(48.5f, 32.5f)
        mediaBox.populate(21f, 14.85f, 0f, 0f, false, false)
        assertEquals(4, mediaBox.size)
        assertThat(mediaBox.mapAsDouble()).containsExactly(
            0.0 to 0.0,
            21.0 to 0.0,
            0.0 to 14.85,
            21.0 to 14.85
        )
    }

    @Test
    fun f4in79109() {
        val mediaBox = MediaSize(79f, 109f)
        mediaBox.populate(21.5f, 33f, 0f, 0f, true, true)
        assertEquals(11, mediaBox.size)
        assertThat(mediaBox.mapAsDouble()).containsExactly(
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

    /** Convert to double for assertion accuracy. */
    private fun MediaSize.mapAsDouble() = map { it.x.toString().toDouble() to it.y.toString().toDouble() }
}
