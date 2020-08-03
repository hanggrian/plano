package com.hendraanggrian.plano.dialogs

import com.hendraanggrian.plano.PlanoApp
import com.hendraanggrian.plano.R
import com.hendraanggrian.plano.Resources
import com.jfoenix.controls.JFXDialog
import javafx.scene.Node
import javafx.scene.layout.VBox
import ktfx.controls.paddings
import ktfx.jfoenix.layouts.jfxButton
import ktfx.layouts.NodeManager
import ktfx.layouts.buttonBar
import ktfx.layouts.label
import ktfx.layouts.vbox
import ktfx.listeners.onAction

abstract class BaseDialog(app: PlanoApp, title: String) :
    JFXDialog(app.rootPane, null, DialogTransition.CENTER), NodeManager, Resources by app {

    private val contentPane: VBox

    override fun <T : Node> addChild(child: T): T = child.also { contentPane.children += it }

    init {
        content = ktfx.layouts.vbox(20.0) {
            paddings = 20.0
            label(title) {
                id = R.style.label_dialog_title
            }
            contentPane = vbox()
            buttonBar {
                onButtons()
                jfxButton(getString(R.string.btn_close)) {
                    onAction { close() }
                }
            }
        }
    }

    open fun NodeManager.onButtons() {
    }
}
