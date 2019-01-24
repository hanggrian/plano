package com.hendraanggrian.plano.control

import com.hendraanggrian.plano.R
import com.hendraanggrian.plano.R2
import com.hendraanggrian.plano.Resources
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
import ktfx.layouts._ContextMenu
import ktfx.layouts.contextMenu
import ktfx.layouts.menu
import ktfx.layouts.menuItem
import ktfx.layouts.separatorMenuItem

fun NodeInvokable.morePaperButton(
    resources: Resources,
    widthField: TextField,
    heightField: TextField
): Button = moreButton {
    menu(resources.getString(R2.string.a_series)) {
        paperMenuItem(118.9, 84.1, widthField, heightField, "A0")
        paperMenuItem(84.1, 59.4, widthField, heightField, "A1")
        paperMenuItem(59.4, 42, widthField, heightField, "A2")
        paperMenuItem(42, 29.7, widthField, heightField, "A3")
        paperMenuItem(29.7, 21, widthField, heightField, "A4")
        paperMenuItem(21, 14.8, widthField, heightField, "A5")
        paperMenuItem(14.8, 10.5, widthField, heightField, "A6")
        paperMenuItem(10.5, 7.4, widthField, heightField, "A7")
        paperMenuItem(7.4, 5.2, widthField, heightField, "A8")
        paperMenuItem(5.2, 3.7, widthField, heightField, "A9")
        paperMenuItem(3.7, 2.6, widthField, heightField, "A10")
    }
    menu(resources.getString(R2.string.b_series)) {
        paperMenuItem(141.4, 100, widthField, heightField, "B0")
        paperMenuItem(100, 70.7, widthField, heightField, "B1")
        paperMenuItem(70.7, 50, widthField, heightField, "B2")
        paperMenuItem(50, 35.3, widthField, heightField, "B3")
        paperMenuItem(35.3, 25, widthField, heightField, "B4")
        paperMenuItem(25, 17.6, widthField, heightField, "B5")
        paperMenuItem(17.6, 12.5, widthField, heightField, "B6")
        paperMenuItem(12.5, 8.8, widthField, heightField, "B7")
        paperMenuItem(8.8, 6.2, widthField, heightField, "B8")
        paperMenuItem(6.2, 4.4, widthField, heightField, "B9")
        paperMenuItem(4.4, 3.1, widthField, heightField, "B10")
    }
    separatorMenuItem()
    paperMenuItem(61, 86, widthField, heightField)
    paperMenuItem(61, 92, widthField, heightField)
    paperMenuItem(65, 90, widthField, heightField)
    paperMenuItem(65, 100, widthField, heightField)
    paperMenuItem(79, 109, widthField, heightField)
    separatorMenuItem()
    paperMenuItem(70, 108, widthField, heightField)
    paperMenuItem(86, 106, widthField, heightField)
}

fun NodeInvokable.moreButton(
    init: ((@LayoutMarker _ContextMenu).() -> kotlin.Unit)? = null
): Button = roundButton(16.0, R.image.ic_more) {
    val contextMenu = contextMenu(init)
    onAction {
        if (!contextMenu.isShowing) {
            contextMenu.show(this@roundButton, Side.RIGHT, 0.0, 0.0)
        }
    }
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

private fun MenuItemInvokable.paperMenuItem(
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