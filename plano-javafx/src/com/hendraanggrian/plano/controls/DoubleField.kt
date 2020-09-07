package com.hendraanggrian.plano.controls

import com.hendraanggrian.plano.clean
import com.jfoenix.controls.JFXTextField
import javafx.scene.control.TextFormatter
import ktfx.controls.CENTER
import ktfx.coroutines.listener
import java.util.regex.Pattern

class DoubleField : JFXTextField() {
    private companion object {
        val PATTERN_DOUBLE: Pattern = Pattern.compile("\\d*|\\d+\\.\\d*")
    }

    init {
        prefWidth = 50.0
        alignment = CENTER
        textFormatter = TextFormatter<TextFormatter.Change?> { change ->
            when {
                PATTERN_DOUBLE.matcher(change!!.controlNewText).matches() -> change
                else -> null
            }
        }
        focusedProperty().listener { _, _, focused ->
            if (focused && text?.isNotEmpty() == true) {
                selectAll()
            }
        }
    }

    var value: Double
        get() = text?.toDoubleOrNull() ?: 0.0
        set(value) {
            text = value.clean()
        }
}
