package com.hendraanggrian.plano.dialog

import com.hendraanggrian.plano.BuildConfig
import com.hendraanggrian.plano.R2
import com.hendraanggrian.plano.Resources
import javafx.scene.layout.StackPane
import ktfx.coroutines.onAction
import ktfx.layouts._VBox
import ktfx.layouts.hyperlink
import ktfx.layouts.label
import ktfx.layouts.textFlow
import java.awt.Desktop
import java.net.URI

class AboutDialog(resources: Resources, container: StackPane) : Dialog(resources, container) {

    override fun _VBox.onCreateContent() {
        label("${BuildConfig.NAME} ${BuildConfig.VERSION}") { styleClass.addAll("bold", "display") }
        textFlow {
            getString(R2.string._about_title1)()
            newLine()
            getString(R2.string._about_title2_1)()
            hyperlink(getString(R2.string._about_title2_2)) {
                onAction {
                    Desktop.getDesktop().browse(URI(BuildConfig.WEBSITE))
                }
            }
            getString(R2.string._about_title2_3)()
        }
    }
}