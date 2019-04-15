package com.hendraanggrian.plano.dialog

import com.hendraanggrian.plano.R
import com.hendraanggrian.plano.Resources
import javafx.scene.layout.StackPane
import ktfx.controls.paddingAll
import ktfx.coroutines.onAction
import ktfx.jfoenix._JFXDialog
import ktfx.jfoenix.jfxButton
import ktfx.layouts.NodeManager
import ktfx.layouts.buttonBar
import ktfx.layouts.label
import ktfx.layouts.vbox

abstract class Dialog(
    resources: Resources,
    container: StackPane,
    title: String
) : _JFXDialog(container, DialogTransition.CENTER, true), Resources by resources {

    init {
        vbox(20.0) {
            paddingAll = 20
            label(title) { styleClass.addAll("bold", "display") }
            onContent()
            buttonBar {
                onButtons()
                jfxButton(getString(R.string.btn_close)) {
                    styleClass.addAll("flat", "bold")
                    onAction {
                        close()
                    }
                }
            }
        }
    }

    abstract fun NodeManager.onContent()

    open fun NodeManager.onButtons() {
    }
}