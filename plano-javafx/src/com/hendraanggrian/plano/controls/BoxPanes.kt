package com.hendraanggrian.plano.controls

import com.hendraanggrian.plano.Box
import com.hendraanggrian.plano.MediaBox
import com.hendraanggrian.plano.R
import com.hendraanggrian.plano.TrimBox
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import ktfx.listeners.listener
import ktfx.times

sealed class BoxPane(
    box: Box,
    scale: DoubleProperty,
    isFill: BooleanProperty,
    isThick: BooleanProperty,
    backgroundStyleClass: String,
    borderStyleClass: String,
    borderThickStyleClass: String
) : Pane() {
    init {
        prefWidthProperty().bind(box.width * scale)
        prefHeightProperty().bind(box.height * scale)

        if (isFill.value) styleClass += backgroundStyleClass
        isFill.listener { _, _, newValue ->
            when {
                newValue -> styleClass += backgroundStyleClass
                else -> styleClass -= backgroundStyleClass
            }
        }

        styleClass += borderStyleClass
        if (isThick.value) styleClass += borderThickStyleClass
        isThick.listener { _, _, newValue ->
            when {
                newValue -> styleClass += borderThickStyleClass
                else -> styleClass -= borderThickStyleClass
            }
        }
    }
}

class MediaBoxPane(box: MediaBox, scale: DoubleProperty, isFill: BooleanProperty, isThick: BooleanProperty) : BoxPane(
    box,
    scale,
    isFill,
    isThick,
    R.style.box_media_background,
    R.style.box_media_border,
    R.style.box_media_border_thick
)

class TrimBoxPane(box: TrimBox, scale: DoubleProperty, isFill: BooleanProperty, isThick: BooleanProperty) : BoxPane(
    box,
    scale,
    isFill,
    isThick,
    R.style.box_trim_background,
    R.style.box_trim_border,
    R.style.box_trim_border_thick
) {
    init {
        userData = box.x to box.y
        AnchorPane.setLeftAnchor(this, box.x * scale.value)
        AnchorPane.setTopAnchor(this, box.y * scale.value)
    }
}
