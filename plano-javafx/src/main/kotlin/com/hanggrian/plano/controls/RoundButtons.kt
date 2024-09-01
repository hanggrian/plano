@file:Suppress("ktlint:rulebook:qualifier-consistency")

package com.hanggrian.plano.controls

import com.hanggrian.plano.Resources
import com.hanggrian.plano.Size
import com.hanggrian.plano.StandardSize
import com.hanggrian.plano.clean
import com.hanggrian.plano_javafx.R
import com.jfoenix.controls.JFXButton
import javafx.beans.value.ObservableBooleanValue
import javafx.scene.control.TextField
import javafx.scene.shape.Circle
import ktfx.bindings.stringBindingBy
import ktfx.controls.SIDE_RIGHT
import ktfx.coroutines.onAction
import ktfx.coroutines.onShowing
import ktfx.layouts.KtfxContextMenu
import ktfx.layouts.contextMenu
import ktfx.layouts.label
import ktfx.layouts.menu
import ktfx.layouts.menuItem
import ktfx.layouts.separatorMenuItem
import ktfx.layouts.tooltip
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction

open class RoundButton(resources: Resources, radius: Double, tooltipId: String) :
    JFXButton(),
    Resources by resources {
    init {
        shape = Circle(radius)
        (radius * 2).let {
            setMinSize(it, it)
            setMaxSize(it, it)
        }
        tooltip(getString(tooltipId))
    }

    companion object {
        const val RADIUS_BTN = 20.0
        const val RADIUS_IC = 24.0
    }
}

open class AdaptableRoundButton(
    resources: Resources,
    radius: Double,
    tooltipId: String,
    dependency: ObservableBooleanValue,
    id: Pair<String, String>,
) : RoundButton(resources, radius, tooltipId) {
    init {
        idProperty().bind(dependency.stringBindingBy { if (it) id.first else id.second })
    }
}

open class RoundMorePaperButton(
    resources: Resources,
    private val widthField: TextField,
    private val heightField: TextField,
    historyProvider: Transaction.() -> Iterable<Size>,
) : RoundButton(resources, RADIUS_BTN, R.string_more) {
    init {
        id = R.style_btn_more
        val contextMenu =
            contextMenu {
                onShowing {
                    items.removeAll(items.filter { it.userData != PERSISTENT })
                    transaction {
                        historyProvider().forEach { size ->
                            items.add(
                                0,
                                ktfx.layouts.menuItem(size.dimension) {
                                    onAction {
                                        widthField.text = size.width.clean()
                                        heightField.text = size.height.clean()
                                    }
                                },
                            )
                        }
                    }
                }
                separatorMenuItem { userData = PERSISTENT }
                standardPaperSizesMenu(R.string_a_series, StandardSize.SERIES_A)
                standardPaperSizesMenu(R.string_b_series, StandardSize.SERIES_B)
                standardPaperSizesMenu(R.string_c_series, StandardSize.SERIES_C)
                standardPaperSizesMenu(R.string_f_series, StandardSize.SERIES_F)
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
                    graphic =
                        label(paperSize.name) {
                            id = R.style_label_context_graphic
                        }
                    onAction {
                        widthField.text = paperSize.width.toString()
                        heightField.text = paperSize.height.toString()
                    }
                }
            }
        }

    internal companion object {
        const val PERSISTENT = "PERSISTENT"
    }
}
