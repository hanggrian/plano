package com.hendraanggrian.plano.control

import com.hendraanggrian.plano.BuildConfig
import javafx.scene.layout.StackPane
import ktfx.controls.paddingAll
import ktfx.coroutines.onAction
import ktfx.jfoenix._JFXDialog
import ktfx.jfoenix.jfxButton
import ktfx.layouts.buttonBar
import ktfx.layouts.hyperlink
import ktfx.layouts.label
import ktfx.layouts.textFlow
import ktfx.layouts.vbox
import java.awt.Desktop
import java.net.URI

class AboutDialog(container: StackPane) : _JFXDialog(container, DialogTransition.CENTER, true) {

    init {
        vbox(20.0) {
            paddingAll = 20
            label("${BuildConfig.NAME} ${BuildConfig.VERSION}") { styleClass.addAll("bold", "display") }
            textFlow {
                "Efficient paper size calculator."()
                newLine()
                "See "()
                hyperlink("homepage") {
                    onAction {
                        Desktop.getDesktop().browse(URI(BuildConfig.WEBSITE))
                    }
                }
                " for more information."()
            }
            buttonBar {
                jfxButton("Cancel") {
                    styleClass.addAll("flat", "bold")
                    onAction {
                        close()
                    }
                }
            }
        }
    }
}