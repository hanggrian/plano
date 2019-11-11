package com.hendraanggrian.plano.control

import com.hendraanggrian.plano.App
import com.hendraanggrian.plano.App.Companion.SCALE_BIG
import com.hendraanggrian.plano.App.Companion.SCALE_SMALL
import com.hendraanggrian.plano.MediaSize
import com.hendraanggrian.plano.TrimSize
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.geometry.Insets
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Border
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.BorderWidths
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import ktfx.bindings.bindingOf
import ktfx.bindings.times
import ktfx.layouts.KtfxPane

class MediaPane(
    size: MediaSize,
    scale: DoubleProperty,
    isFilled: BooleanProperty,
    isThicked: BooleanProperty
) : KtfxPane() {

    init {
        prefWidthProperty().bind(size.width * scale)
        prefHeightProperty().bind(size.height * scale)

        backgroundProperty().bind(bindingOf(isFilled) {
            Background(
                BackgroundFill(
                    when {
                        isFilled.value -> App.COLOR_YELLOW_LIGHT
                        else -> Color.TRANSPARENT
                    },
                    CornerRadii.EMPTY,
                    Insets.EMPTY
                )
            )
        })
        borderProperty().bind(bindingOf(isThicked) {
            Border(
                BorderStroke(
                    App.COLOR_YELLOW,
                    BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY,
                    BorderWidths(if (isThicked.value) SCALE_BIG else SCALE_SMALL)
                )
            )
        })
    }
}

class TrimPane(
    size: TrimSize,
    scale: DoubleProperty,
    isFilled: BooleanProperty,
    isThicked: BooleanProperty
) : KtfxPane() {

    init {
        prefWidthProperty().bind(size.width * scale)
        prefHeightProperty().bind(size.height * scale)

        backgroundProperty().bind(bindingOf(isFilled) {
            Background(
                BackgroundFill(
                    when {
                        isFilled.value -> App.COLOR_RED_LIGHT
                        else -> Color.TRANSPARENT
                    },
                    CornerRadii.EMPTY,
                    Insets.EMPTY
                )
            )
        })
        borderProperty().bind(bindingOf(isThicked) {
            Border(
                BorderStroke(
                    App.COLOR_RED,
                    BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY,
                    BorderWidths(if (isThicked.value) SCALE_BIG else SCALE_SMALL)
                )
            )
        })

        userData = size.x to size.y
        AnchorPane.setLeftAnchor(this, size.x * scale.value)
        AnchorPane.setTopAnchor(this, size.y * scale.value)
    }
}
