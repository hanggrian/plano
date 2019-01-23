package com.hendraanggrian.plano.control

import com.hendraanggrian.plano.R
import com.jfoenix.controls.JFXButton
import javafx.geometry.Side
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.shape.Circle
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton
import ktfx.layouts.LayoutMarker
import ktfx.layouts.MenuItemInvokable
import ktfx.layouts.NodeInvokable
import ktfx.layouts.contextMenu
import ktfx.layouts.menu
import ktfx.layouts.menuItem
import ktfx.layouts.separatorMenuItem

fun NodeInvokable.moreButton(
    widthField: TextField,
    heightField: TextField,
    init: ((@LayoutMarker JFXButton).() -> Unit)? = null
): Button = roundButton(16.0, R.image.ic_more) {
    val contextMenu = contextMenu {
        menu("A Series") {
            sizeMenuItem(84.1, 118.9, widthField, heightField, "A0")
            sizeMenuItem(59.4, 84.1, widthField, heightField, "A1")
            sizeMenuItem(42, 59.4, widthField, heightField, "A2")
            sizeMenuItem(29.7, 42, widthField, heightField, "A3")
            sizeMenuItem(21, 29.7, widthField, heightField, "A4")
            sizeMenuItem(14.8, 21, widthField, heightField, "A5")
            sizeMenuItem(10.5, 14.8, widthField, heightField, "A6")
            sizeMenuItem(7.4, 10.5, widthField, heightField, "A7")
            sizeMenuItem(5.2, 7.4, widthField, heightField, "A8")
            sizeMenuItem(3.7, 5.2, widthField, heightField, "A9")
            sizeMenuItem(2.6, 3.7, widthField, heightField, "A10")
        }
        menu("B Series") {
        }
        separatorMenuItem()
        sizeMenuItem(61, 86, widthField, heightField)
        sizeMenuItem(61, 92, widthField, heightField)
        sizeMenuItem(65, 90, widthField, heightField)
        sizeMenuItem(65, 100, widthField, heightField)
        sizeMenuItem(79, 109, widthField, heightField)
        separatorMenuItem()
        menu("Stickers") {
            sizeMenuItem(70, 108, widthField, heightField, "Cromo")
            sizeMenuItem(86, 106, widthField, heightField, "Vinyl")
        }
    }
    onAction {
        if (!contextMenu.isShowing) {
            contextMenu.show(this@roundButton, Side.RIGHT, 0.0, 0.0)
        }
    }
    init?.invoke(this)
}

fun NodeInvokable.roundButton(
    radius: Double,
    graphicUrl: String,
    init: ((@LayoutMarker JFXButton).() -> Unit)? = null
): Button = jfxButton(graphic = ImageView(graphicUrl)) {
    shape = Circle(radius)
    setMinSize(2 * radius, 2 * radius)
    setMaxSize(2 * radius, 2 * radius)
    init?.invoke(this)
}

private fun MenuItemInvokable.sizeMenuItem(
    width: Number,
    height: Number,
    widthField: TextField,
    heightField: TextField,
    name: String? = null
) {
    val defaultTitle = "$width x $height"
    menuItem(name?.let { "$it ($defaultTitle)" } ?: defaultTitle) {
        onAction {
            widthField.text = width.toString()
            heightField.text = height.toString()
        }
    }
}