package com.hendraanggrian.plano.dialog

import com.hendraanggrian.plano.R2
import com.hendraanggrian.plano.Resources
import javafx.scene.layout.StackPane
import ktfx.controls.paddingAll
import ktfx.coroutines.onAction
import ktfx.jfoenix._JFXDialog
import ktfx.jfoenix.jfxButton
import ktfx.layouts._VBox
import ktfx.layouts.buttonBar
import ktfx.layouts.vbox

abstract class Dialog(resources: Resources, container: StackPane) :
    _JFXDialog(container, DialogTransition.CENTER, true),
    Resources by resources {

    init {
        vbox(20.0) {
            paddingAll = 20
            onCreateContent()
            buttonBar {
                jfxButton(getString(R2.string.close)) {
                    styleClass.addAll("flat", "bold")
                    onAction {
                        close()
                    }
                }
            }
        }
    }

    abstract fun _VBox.onCreateContent()
}