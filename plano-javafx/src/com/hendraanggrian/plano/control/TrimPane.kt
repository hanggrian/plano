package com.hendraanggrian.plano.control

import com.hendraanggrian.plano.PlanoApplication
import com.hendraanggrian.plano.PlanoApplication.Companion.SCALE_BIG
import com.hendraanggrian.plano.PlanoApplication.Companion.SCALE_SMALL
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
import ktfx.bindings.buildBinding
import ktfx.bindings.times
import ktfx.layouts._Pane

class TrimPane(
    size: TrimSize,
    scale: DoubleProperty,
    isFilled: BooleanProperty,
    isThicked: BooleanProperty
) : _Pane() {

    init {
        prefWidthProperty().bind(size.width * scale)
        prefHeightProperty().bind(size.height * scale)

        backgroundProperty().bind(buildBinding(isFilled) {
            Background(
                BackgroundFill(
                    when {
                        isFilled.value -> PlanoApplication.COLOR_RED_LIGHT
                        else -> Color.TRANSPARENT
                    },
                    CornerRadii.EMPTY,
                    Insets.EMPTY
                )
            )
        })
        borderProperty().bind(buildBinding(isThicked) {
            Border(
                BorderStroke(
                    PlanoApplication.COLOR_RED,
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