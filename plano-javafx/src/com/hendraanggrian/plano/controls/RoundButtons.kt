package com.hendraanggrian.plano.controls

import com.hendraanggrian.plano.PaperSize
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
import ktfx.layouts.contextMenu
import ktfx.layouts.menu
import ktfx.layouts.menuItem
import ktfx.layouts.separatorMenuItem
import ktfx.layouts.tooltip
import ktfx.listeners.onAction
import ktfx.or
import ktfx.otherwise
import ktfx.runLater
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
    resources: Resources
) : SimpleRoundButton(RADIUS, resources.getString(R.string.more)), Resources by resources {
    companion object {
        const val RADIUS = 16
    }

    init {
        id = R.style.menu_more
        val contextMenu = contextMenu { onContextMenu() }
        onAction {
            if (!contextMenu.isShowing) {
                contextMenu.show(this@MoreButton, Side.RIGHT, 0.0, 0.0)
            }
        }
    }

    open fun KtfxContextMenu.onContextMenu() {
    }
}

open class MorePaperButton(
    resources: Resources,
    private val widthField: TextField,
    private val heightField: TextField
) : MoreButton(resources) {

    override fun KtfxContextMenu.onContextMenu() {
        menuItem(getString(R.string.rotate)) {
            id = R.style.menu_rotate
            runLater { disableProperty().bind(widthField.textProperty().isEmpty or heightField.textProperty().isEmpty) }
            onAction { widthField.text = heightField.text.also { heightField.text = widthField.text } }
        }
        separatorMenuItem()
        seriesMenu(R.string.a_series, PaperSize.SERIES_A)
        seriesMenu(R.string.b_series, PaperSize.SERIES_B)
        seriesMenu(R.string.c_series, PaperSize.SERIES_C)
        seriesMenu(R.string.f_series, PaperSize.SERIES_F)
    }

    private fun KtfxContextMenu.seriesMenu(textId: String, series: List<PaperSize>) = menu(getString(textId)) {
        id = R.style.menu_empty
        series.forEach { paperSize ->
            menuItem(paperSize.title) {
                onAction {
                    widthField.text = paperSize.width.toString()
                    heightField.text = paperSize.height.toString()
                }
            }
        }
    }
}
