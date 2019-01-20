package com.hendraanggrian.plano.gui

import com.jfoenix.controls.JFXTextField
import javafx.geometry.Pos
import javafx.scene.control.TextFormatter
import ktfx.jfoenix.jfxTextField
import ktfx.layouts.LayoutMarker
import ktfx.layouts.NodeInvokable
import java.util.function.UnaryOperator
import java.util.regex.Pattern

private val PATTERN_DOUBLE = Pattern.compile("\\d*|\\d+\\.\\d*")

fun NodeInvokable.doubleField(init: ((@LayoutMarker JFXTextField).() -> Unit)? = null): JFXTextField = jfxTextField {
    init?.invoke(this)
    prefWidth = 50.0
    alignment = Pos.CENTER
    textFormatter = TextFormatter<TextFormatter.Change?>(UnaryOperator<TextFormatter.Change?> { change ->
        when {
            PATTERN_DOUBLE.matcher(change!!.controlNewText).matches() -> change
            else -> null
        }
    })
}