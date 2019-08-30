package com.hendraanggrian.plano.dialog

import com.hendraanggrian.plano.BuildConfig
import com.hendraanggrian.plano.R
import com.hendraanggrian.plano.Resources
import java.awt.Desktop
import java.net.URI
import javafx.scene.layout.StackPane
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton
import ktfx.layouts.NodeManager
import ktfx.layouts.label

class AboutDialog(
    resources: Resources,
    container: StackPane
) : Dialog(resources, container, "${BuildConfig.NAME} ${BuildConfig.VERSION}") {

    override fun NodeManager.onContent() {
        label(getString(R.string._about)) {
            prefWidth = 300.0
            isWrapText = true
        }
    }

    override fun NodeManager.onButtons() {
        jfxButton(getString(R.string.btn_homepage)) {
            styleClass.addAll("flat", "bold")
            onAction {
                Desktop.getDesktop().browse(URI(BuildConfig.HOMEPAGE))
                close()
            }
        }
    }
}
