package com.hanggrian.plano.controls

import com.hanggrian.plano.clean
import com.jfoenix.controls.JFXTextField
import javafx.scene.control.TextFormatter
import ktfx.controls.CENTER
import ktfx.coroutines.listener
import java.util.regex.Pattern

class FloatField : JFXTextField("") {
    init {
        prefWidth = 50.0
        alignment = CENTER
        textFormatter =
            TextFormatter<TextFormatter.Change?> { change ->
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

    var value: Float
        get() = text?.toFloatOrNull() ?: 0f
        set(value) {
            text = value.clean()
        }

    private companion object {
        val PATTERN_DOUBLE: Pattern = Pattern.compile("\\d*|\\d+\\.\\d*")
    }
}
