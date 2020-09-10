package com.hendraanggrian.plano.controls

import com.hendraanggrian.plano.R
import com.hendraanggrian.plano.Resources
import com.hendraanggrian.plano.Size
import com.hendraanggrian.plano.StandardSize
import com.jfoenix.controls.JFXButton
import javafx.beans.value.ObservableBooleanValue
import javafx.scene.control.TextField
import javafx.scene.shape.Circle
import javafx.scene.text.FontWeight
import ktfx.bindings.asString
import ktfx.controls.SIDE_RIGHT
import ktfx.coroutines.onShowing
import ktfx.layouts.KtfxContextMenu
import ktfx.layouts.contextMenu
import ktfx.layouts.label
import ktfx.layouts.menu
import ktfx.layouts.menuItem
import ktfx.layouts.separatorMenuItem
import ktfx.layouts.tooltip
import ktfx.listeners.onAction
import ktfx.text.fontOf
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction

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
        (radius * 2).let {
            setMinSize(it, it)
            setMaxSize(it, it)
        }
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
        idProperty().bind(dependency.asString { if (it) id.first else id.second })
    }
}

open class RoundMorePaperButton(
    resources: Resources,
    private val widthField: TextField,
    private val heightField: TextField,
    historyProvider: Transaction.() -> Iterable<Size>
) : RoundButton(resources, RADIUS_MEDIUM, R.string.more) {

    internal companion object {
        const val PERSISTENT = "PERSISTENT"
    }

    init {
        id = R.style.menu_more
        val contextMenu = contextMenu {
            onShowing {
                items.removeAll(items.filter { it.userData != PERSISTENT })
                transaction {
                    historyProvider().forEach { size ->
                        items.add(
                            0,
                            ktfx.layouts.menuItem(size.dimension) {
                                onAction {
                                    widthField.text = size.width.toString()
                                    heightField.text = size.height.toString()
                                }
                            }
                        )
                    }
                }
            }
            separatorMenuItem { userData = PERSISTENT }
            standardPaperSizesMenu(R.string.a_series, StandardSize.SERIES_A)
            standardPaperSizesMenu(R.string.b_series, StandardSize.SERIES_B)
            standardPaperSizesMenu(R.string.c_series, StandardSize.SERIES_C)
            standardPaperSizesMenu(R.string.f_series, StandardSize.SERIES_F)
        }
        onAction {
            if (!contextMenu.isShowing) {
                contextMenu.show(this@RoundMorePaperButton, SIDE_RIGHT, 0.0, 0.0)
            }
        }
    }

    private fun KtfxContextMenu.standardPaperSizesMenu(textId: String, series: List<StandardSize>) =
        menu(getString(textId)) {
            userData = PERSISTENT
            series.forEach { paperSize ->
                menuItem(paperSize.dimension) {
                    graphic = label(paperSize.name) {
                        font = fontOf("Roboto", FontWeight.BLACK)
                    }
                    onAction {
                        widthField.text = paperSize.width.toString()
                        heightField.text = paperSize.height.toString()
                    }
                }
            }
        }
}
