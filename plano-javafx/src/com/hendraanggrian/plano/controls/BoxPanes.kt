package com.hendraanggrian.plano.controls

import com.hendraanggrian.plano.Box
import com.hendraanggrian.plano.MediaBox2
import com.hendraanggrian.plano.R
import com.hendraanggrian.plano.TrimBox2
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import ktfx.bindings.times
import ktfx.listeners.listener

sealed class BoxPane(
    box: Box,
    scaleProperty: DoubleProperty,
    fillProperty: BooleanProperty,
    thickProperty: BooleanProperty,
    backgroundStyleClass: String,
    borderStyleClass: String,
    borderThickStyleClass: String
) : Pane() {
    init {
        prefWidthProperty().bind(box.width * scaleProperty)
        prefHeightProperty().bind(box.height * scaleProperty)

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

class MediaBoxPane(
    box: MediaBox2,
    scaleProperty: DoubleProperty,
    fillProperty: BooleanProperty,
    thickProperty: BooleanProperty
) : BoxPane(
    box,
    scaleProperty,
    fillProperty,
    thickProperty,
    R.style.box_media_background,
    R.style.box_media_border,
    R.style.box_media_border_thick
)

class TrimBoxPane(
    box: TrimBox2,
    scaleProperty: DoubleProperty,
    fillProperty: BooleanProperty,
    thickProperty: BooleanProperty
) : BoxPane(
    box,
    scaleProperty,
    fillProperty,
    thickProperty,
    R.style.box_trim_background,
    R.style.box_trim_border,
    R.style.box_trim_border_thick
) {
    init {
        userData = box.x to box.y
        AnchorPane.setLeftAnchor(this, box.x * scaleProperty.value)
        AnchorPane.setTopAnchor(this, box.y * scaleProperty.value)
    }
}
