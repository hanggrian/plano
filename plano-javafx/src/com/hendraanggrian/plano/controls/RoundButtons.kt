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
import ktfx.layouts.tooltip
import ktfx.listeners.onAction
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

open class RoundButton(
    radius: Number,
    text: String
) : BaseRoundButton(radius, stringPropertyOf(text))

open class RoundMenuButton(
    resources: Resources,
    textId: String
) : RoundButton(RADIUS, resources.getString(textId)), Resources by resources {
    companion object {
        const val RADIUS = 16
    }
}

open class RoundMenuPaperButton(
    resources: Resources,
    private val widthField: TextField,
    private val heightField: TextField
) : RoundMenuButton(resources, R.string.more) {

    init {
        id = R.style.menu_more
        val contextMenu = contextMenu {
            seriesMenu(R.string.a_series, PaperSize.SERIES_A)
            seriesMenu(R.string.b_series, PaperSize.SERIES_B)
            seriesMenu(R.string.c_series, PaperSize.SERIES_C)
            seriesMenu(R.string.f_series, PaperSize.SERIES_F)
        }
        onAction {
            if (!contextMenu.isShowing) {
                contextMenu.show(this@RoundMenuPaperButton, Side.RIGHT, 0.0, 0.0)
            }
        }
    }

    private fun KtfxContextMenu.seriesMenu(textId: String, series: List<PaperSize>) = menu(getString(textId)) {
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
