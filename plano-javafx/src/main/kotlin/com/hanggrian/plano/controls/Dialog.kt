@file:Suppress("ktlint:rulebook:qualifier-consistency")

package com.hanggrian.plano.controls

import com.hanggrian.plano.PlanoApp
import com.hanggrian.plano.Resources
import com.hanggrian.plano_javafx.R
import com.jfoenix.controls.JFXDialog
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
    Resources by app {
    private val contentPane: VBox

    init {
        content =
            vbox(20.0) {
                padding = insetsOf(20)
                label(title) {
                    id = R.style_label_dialog_title
                }
                contentPane =
                    vbox {
                        onItems()
                    }
                buttonBar {
                    onButtons()
                    jfxButton(getString(R.string_btn_close)) {
                        onAction { close() }
                    }
                }
            }
    }

    open fun NodeContainer.onItems() {}

    open fun NodeContainer.onButtons() {}
}
