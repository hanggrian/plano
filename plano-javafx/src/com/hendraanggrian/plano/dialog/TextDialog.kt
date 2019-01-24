package com.hendraanggrian.plano.dialog

import com.hendraanggrian.plano.R2
import com.hendraanggrian.plano.Resources
import javafx.scene.layout.StackPane
import ktfx.layouts._VBox
import ktfx.layouts.label

class TextDialog(resources: Resources, container: StackPane) : Dialog(resources, container) {

    override fun _VBox.onCreateContent() {
        label(getString(R2.string.please_restart)) { styleClass.addAll("bold", "display") }
        label(getString(R2.string._please_restart_desc))
    }
}