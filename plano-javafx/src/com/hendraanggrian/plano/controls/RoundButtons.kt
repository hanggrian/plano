package com.hendraanggrian.plano.controls

import com.hendraanggrian.plano.PaperSize
import com.hendraanggrian.plano.R
import com.hendraanggrian.plano.Resources
import com.jfoenix.controls.JFXButton
import javafx.beans.value.ObservableBooleanValue
import javafx.geometry.Side
import javafx.scene.control.TextField
import javafx.scene.shape.Circle
import ktfx.controls.maxSize
import ktfx.controls.minSize
import ktfx.layouts.KtfxContextMenu
import ktfx.layouts.contextMenu
import ktfx.layouts.menu
import ktfx.layouts.menuItem
import ktfx.layouts.tooltip
import ktfx.listeners.onAction
import ktfx.toStringBinding

open class RoundButton(
    resources: Resources,
    radius: Double,
    tooltipId: String
) : JFXButton(), Resources by resources {
    companion object {
        const val RADIUS_SMALL = 12.0
        const val RADIUS_MEDIUM = 16.0
        const val RADIUS_LARGE = 24.0
    }

    init {
        shape = Circle(radius)
        minSize = radius * 2
        maxSize = radius * 2
        @Suppress("LeakingThis") tooltip(getString(tooltipId))
    }
}

open class AdaptableRoundButton(
    resources: Resources,
    radius: Double,
    tooltipId: String,
    dependency: ObservableBooleanValue,
    id: Pair<String, String>
) : RoundButton(resources, radius, tooltipId) {

    init {
        idProperty().bind(dependency.toStringBinding { if (it) id.first else id.second })
    }
}

open class RoundMorePaperButton(
    resources: Resources,
    private val widthField: TextField,
    private val heightField: TextField
) : RoundButton(resources, RADIUS_MEDIUM, R.string.more) {

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
                contextMenu.show(this@RoundMorePaperButton, Side.RIGHT, 0.0, 0.0)
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
