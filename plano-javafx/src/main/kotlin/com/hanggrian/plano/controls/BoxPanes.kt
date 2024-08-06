package com.hanggrian.plano.controls

import com.hanggrian.plano.MediaSize
import com.hanggrian.plano.Size
import com.hanggrian.plano.TrimSize
import com.hanggrian.plano_javafx.R
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import ktfx.bindings.times
import ktfx.coroutines.listener

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
        R.style_box_media_background,
        R.style_box_media_border,
        R.style_box_media_border_thick,
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
        R.style_box_trim_background,
        R.style_box_trim_border,
        R.style_box_trim_border_thick,
    ) {
    init {
        userData = size.x to size.y
        AnchorPane.setLeftAnchor(this, size.x * scaleProperty.value)
        AnchorPane.setTopAnchor(this, size.y * scaleProperty.value)
    }
}
