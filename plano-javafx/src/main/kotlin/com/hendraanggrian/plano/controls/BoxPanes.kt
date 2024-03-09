package com.hendraanggrian.plano.controls

import com.hendraanggrian.plano.MediaSize
import com.hendraanggrian.plano.R
import com.hendraanggrian.plano.Size
import com.hendraanggrian.plano.TrimSize
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import ktfx.bindings.times
import ktfx.listeners.listener

sealed class SizePane(
    size: Size,
    scaleProperty: DoubleProperty,
    fillProperty: BooleanProperty,
    thickProperty: BooleanProperty,
    backgroundStyleClass: String,
    borderStyleClass: String,
    borderThickStyleClass: String,
) : Pane() {
    init {
        prefWidthProperty().bind(size.width * scaleProperty)
        prefHeightProperty().bind(size.height * scaleProperty)

        if (fillProperty.value) styleClass += backgroundStyleClass
        fillProperty.listener { _, _, newValue ->
            when {
                newValue -> styleClass += backgroundStyleClass
                else -> styleClass -= backgroundStyleClass
            }
        }

        styleClass += borderStyleClass
        if (thickProperty.value) styleClass += borderThickStyleClass
        thickProperty.listener { _, _, newValue ->
            when {
                newValue -> styleClass += borderThickStyleClass
                else -> styleClass -= borderThickStyleClass
            }
        }
    }
}

class MediaSizePane(
    size: MediaSize,
    scaleProperty: DoubleProperty,
    fillProperty: BooleanProperty,
    thickProperty: BooleanProperty,
) : SizePane(
        size,
        scaleProperty,
        fillProperty,
        thickProperty,
        R.style.box_media_background,
        R.style.box_media_border,
        R.style.box_media_border_thick,
    )

class TrimSizePane(
    size: TrimSize,
    scaleProperty: DoubleProperty,
    fillProperty: BooleanProperty,
    thickProperty: BooleanProperty,
) : SizePane(
        size,
        scaleProperty,
        fillProperty,
        thickProperty,
        R.style.box_trim_background,
        R.style.box_trim_border,
        R.style.box_trim_border_thick,
    ) {
    init {
        userData = size.x to size.y
        AnchorPane.setLeftAnchor(this, size.x * scaleProperty.value)
        AnchorPane.setTopAnchor(this, size.y * scaleProperty.value)
    }
}
