package com.hendraanggrian.plano.dialog

import com.hendraanggrian.plano.BuildConfig
import com.hendraanggrian.plano.R2
import com.hendraanggrian.plano.Resources
import javafx.scene.layout.StackPane
import ktfx.coroutines.onAction
import ktfx.layouts._VBox
import ktfx.layouts.hyperlink
import ktfx.layouts.textFlow
import java.awt.Desktop
import java.net.URI

class AboutDialog(
    resources: Resources,
    container: StackPane
) : Dialog(resources, container, "${BuildConfig.NAME} ${BuildConfig.VERSION}") {

    override fun _VBox.onCreateContent() {
        textFlow {
            getString(R2.string._about1)()
            newLine()
            getString(R2.string._about2_1)()
            hyperlink(getString(R2.string._about2_2)) {
                onAction {
                    Desktop.getDesktop().browse(URI(BuildConfig.WEBSITE))
                }
            }
            getString(R2.string._about2_3)()
        }
    }
}