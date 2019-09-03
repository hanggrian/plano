package com.hendraanggrian.plano.control

import javafx.scene.layout.Border
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.BorderWidths
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.Region
import javafx.scene.paint.Paint

fun Region.setBorder(
    fill: Paint,
    width: Number = 1.0,
    style: BorderStrokeStyle = BorderStrokeStyle.SOLID,
    radii: CornerRadii = CornerRadii.EMPTY
) {
    border = Border(BorderStroke(fill, style, radii, BorderWidths(width.toDouble())))
}
