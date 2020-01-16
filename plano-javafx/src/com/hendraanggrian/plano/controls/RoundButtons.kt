package com.hendraanggrian.plano.controls

import com.hendraanggrian.plano.PaperSeries
import com.hendraanggrian.plano.R
import com.hendraanggrian.plano.Resources
import com.jfoenix.controls.JFXButton
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableValue
import javafx.geometry.Side
import javafx.scene.control.TextField
import javafx.scene.shape.Circle
import ktfx.given
import ktfx.layouts.KtfxContextMenu
import ktfx.layouts.MenuItemManager
import ktfx.layouts.contextMenu
import ktfx.layouts.menu
import ktfx.layouts.menuItem
import ktfx.layouts.separatorMenuItem
import ktfx.layouts.tooltip
import ktfx.listeners.onAction
import ktfx.or
import ktfx.otherwise
import ktfx.stringPropertyOf
import ktfx.then
import ktfx.toStringBinding

@Suppress("LeakingThis")
sealed class BaseRoundButton(
    radius: Number,
    textBinding: ObservableValue<String>
) : JFXButton() {
    init {
        val r = radius.toDouble()
        shape = Circle(r)
        setMinSize(2 * r, 2 * r)
        setMaxSize(2 * r, 2 * r)
        tooltip { textProperty().bind(textBinding) }
    }
}

open class AdaptableRoundButton(
    radius: Number,
    dependency: ObservableBooleanValue,
    text: Pair<String, String>,
    id: Pair<String, String>
) : BaseRoundButton(radius, given(dependency) then text.first otherwise text.second) {
    init {
        idProperty().bind(dependency.toStringBinding { if (it) id.first else id.second })
    }
}

open class SimpleRoundButton(
    radius: Number,
    text: String
) : BaseRoundButton(radius, stringPropertyOf(text))

open class MoreButton(
    resources: Resources,
    init: KtfxContextMenu.() -> Unit
) : SimpleRoundButton(RADIUS, resources.getString(R.string.more)), Resources by resources {
    companion object {
        const val RADIUS = 16
    }

    init {
        id = R.style.menu_more
        val contextMenu = contextMenu(init)
        onAction {
            if (!contextMenu.isShowing) {
                contextMenu.show(this@MoreButton, Side.RIGHT, 0.0, 0.0)
            }
        }
    }
}

open class MorePaperButton(
    resources: Resources,
    widthField: TextField,
    heightField: TextField
) : MoreButton(resources, {
    menuItem(resources.getString(R.string.rotate)) {
        disableProperty().bind(widthField.textProperty().isEmpty or heightField.textProperty().isEmpty)
        onAction { widthField.text = heightField.text.also { heightField.text = widthField.text } }
    }
    separatorMenuItem()
    val append: MenuItemManager.(PaperSeries) -> Unit = { standardSize ->
        menuItem(standardSize.title) {
            onAction {
                widthField.text = standardSize.width.toString()
                heightField.text = standardSize.height.toString()
            }
        }
    }
    menu(resources.getString(R.string.a_series)) { PaperSeries.A.forEach { append(it) } }
    menu(resources.getString(R.string.b_series)) { PaperSeries.B.forEach { append(it) } }
    menu(resources.getString(R.string.c_series)) { PaperSeries.C.forEach { append(it) } }
})
