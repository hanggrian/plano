package com.hendraanggrian.plano.dialogs

import com.hendraanggrian.plano.R
import com.hendraanggrian.plano.Resources
import com.jfoenix.controls.JFXDialog
import javafx.scene.Node
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import ktfx.controls.paddingAll
import ktfx.jfoenix.layouts.jfxButton
import ktfx.layouts.NodeManager
import ktfx.layouts.buttonBar
import ktfx.layouts.label
import ktfx.layouts.vbox
import ktfx.listeners.onAction

abstract class PlanoDialog(
    resources: Resources,
    container: StackPane,
    title: String
) : JFXDialog(container, null, DialogTransition.CENTER), NodeManager, Resources by resources {

    private val contentPane: VBox

    override fun <T : Node> addChild(child: T): T = child.also { contentPane.children += it }
    override val childCount: Int get() = contentPane.children.size

    init {
        content = ktfx.layouts.vbox(20.0) {
            paddingAll = 20.0
            label(title) { styleClass.addAll("bold", "display") }
            contentPane = vbox()
            buttonBar {
                onButtons()
                jfxButton(getString(R.string.btn_close)) {
                    styleClass.addAll("flat", "bold")
                    onAction { close() }
                }
            }
        }
    }

    open fun NodeManager.onButtons() {
    }
}
