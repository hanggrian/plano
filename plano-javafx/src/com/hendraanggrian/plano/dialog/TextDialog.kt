package com.hendraanggrian.plano.dialog

import com.hendraanggrian.plano.R2
import com.hendraanggrian.plano.Resources
import javafx.scene.layout.StackPane
import ktfx.layouts.NodeManager
import ktfx.layouts.label

class TextDialog(
    resources: Resources,
    container: StackPane
) : Dialog(resources, container, resources.getString(R2.string.please_restart)) {

    override fun NodeManager.onContent() {
        label(getString(R2.string._please_restart_desc))
    }
}