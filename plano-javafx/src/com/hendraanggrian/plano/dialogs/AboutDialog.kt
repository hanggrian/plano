package com.hendraanggrian.plano.dialogs

import com.hendraanggrian.plano.BuildConfig
import com.hendraanggrian.plano.R
import com.hendraanggrian.plano.Resources
import java.awt.Desktop
import java.net.URI
import javafx.scene.layout.StackPane
import ktfx.jfoenix.layouts.jfxButton
import ktfx.layouts.NodeManager
import ktfx.layouts.label
import ktfx.listeners.onAction

class AboutDialog(
    resources: Resources,
    container: StackPane
) : BaseDialog(resources, container, "${BuildConfig.NAME} ${BuildConfig.VERSION}") {

    init {
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
