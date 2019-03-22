@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.plano.control

import com.jfoenix.controls.JFXTextField
import javafx.geometry.Pos
import javafx.scene.control.TextFormatter
import ktfx.coroutines.listener
import ktfx.layouts.LayoutMarker
import ktfx.layouts.NodeManager
import java.util.function.UnaryOperator
import java.util.regex.Pattern

class SizeField : JFXTextField() {

    private companion object {

        val PATTERN_DOUBLE: Pattern = Pattern.compile("\\d*|\\d+\\.\\d*")
    }

    init {
        prefWidth = 50.0
        alignment = Pos.CENTER
        textFormatter =
            TextFormatter<TextFormatter.Change?>(UnaryOperator<TextFormatter.Change?> { change ->
                when {
                    PATTERN_DOUBLE.matcher(change!!.controlNewText).matches() -> change
                    else -> null
                }
            })
        focusedProperty().listener { _, _, focused ->
            if (focused && text?.isNotEmpty() == true) {
                selectAll()
            }
        }
    }

    val value: Double get() = text?.toDoubleOrNull() ?: 0.0
}

fun sizeField(
    init: ((@LayoutMarker SizeField).() -> Unit)? = null
): SizeField = SizeField().also { init?.invoke(it) }

inline fun NodeManager.sizeField(
    noinline init: ((@LayoutMarker SizeField).() -> Unit)? = null
): SizeField = com.hendraanggrian.plano.control.sizeField(init).add()