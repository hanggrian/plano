package com.hanggrian.plano.dialogs

import com.hanggrian.plano.PlanoApp
import com.hanggrian.plano.Resources
import com.hanggrian.plano_javafx.R
import com.jfoenix.controls.JFXDialog
import javafx.scene.Node
import javafx.scene.layout.VBox
import ktfx.controls.insetsOf
import ktfx.coroutines.onAction
import ktfx.jfoenix.layouts.jfxButton
import ktfx.layouts.NodeContainer
import ktfx.layouts.buttonBar
import ktfx.layouts.label
import ktfx.layouts.vbox

abstract class Dialog(app: PlanoApp, title: String) :
    JFXDialog(app.rootPane, null, DialogTransition.CENTER),
    NodeContainer,
    Resources by app {
    private val contentPane: VBox

    init {
        @Suppress("ktlint:rulebook:qualifier-consistency")
        content =
            ktfx.layouts.vbox(20.0) {
                padding = insetsOf(20)
                label(title) {
                    id = R.style_label_dialog_title
                }
                contentPane = vbox()
                buttonBar {
                    onButtons()
                    jfxButton(getString(R.string_btn_close)) {
                        onAction { close() }
                    }
                }
            }
    }

    override fun <T : Node> addChild(child: T): T = child.also { contentPane.children += it }

    open fun NodeContainer.onButtons() {}
}
