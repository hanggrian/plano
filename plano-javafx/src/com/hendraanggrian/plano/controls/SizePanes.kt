package com.hendraanggrian.plano.controls

import com.hendraanggrian.plano.MediaBox
import com.hendraanggrian.plano.PlanoApp
import com.hendraanggrian.plano.PlanoApp.Companion.SCALE_BIG
import com.hendraanggrian.plano.PlanoApp.Companion.SCALE_SMALL
import com.hendraanggrian.plano.TrimBox
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
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import ktfx.bindingOf
import ktfx.times
import ktfx.toBinding

class MediaPane(
    box: MediaBox,
    scale: DoubleProperty,
    isFilled: BooleanProperty,
    isThicked: BooleanProperty
) : Pane() {
    init {
        prefWidthProperty().bind(box.width * scale)
        prefHeightProperty().bind(box.height * scale)
        backgroundProperty().bind(bindingOf(isFilled) {
            Background(
                BackgroundFill(
                    when {
                        isFilled.value -> PlanoApp.COLOR_AMBER_LIGHT
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
                    PlanoApp.COLOR_AMBER,
                    BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY,
                    BorderWidths(if (isThicked.value) SCALE_BIG else SCALE_SMALL)
                )
            )
        })
    }
}

class TrimPane(
    box: TrimBox,
    scale: DoubleProperty,
    isFilled: BooleanProperty,
    isThicked: BooleanProperty
) : Pane() {
    init {
        prefWidthProperty().bind(box.width * scale)
        prefHeightProperty().bind(box.height * scale)
        backgroundProperty().bind(isFilled.toBinding {
            Background(
                BackgroundFill(
                    when {
                        isFilled.value -> PlanoApp.COLOR_RED_LIGHT
                        else -> Color.TRANSPARENT
                    },
                    CornerRadii.EMPTY,
                    Insets.EMPTY
                )
            )
        })
        borderProperty().bind(isThicked.toBinding {
            Border(
                BorderStroke(
                    PlanoApp.COLOR_RED,
                    BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY,
                    BorderWidths(if (isThicked.value) SCALE_BIG else SCALE_SMALL)
                )
            )
        })

        userData = box.x to box.y
        AnchorPane.setLeftAnchor(this, box.x * scale.value)
        AnchorPane.setTopAnchor(this, box.y * scale.value)
    }
}
