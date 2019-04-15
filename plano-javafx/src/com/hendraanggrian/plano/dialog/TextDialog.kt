package com.hendraanggrian.plano.dialog

import com.hendraanggrian.plano.R
import com.hendraanggrian.plano.Resources
import javafx.scene.layout.StackPane
import ktfx.layouts.NodeManager
import ktfx.layouts.label

class TextDialog(
    resources: Resources,
    container: StackPane
) : Dialog(resources, container, resources.getString(R.string.please_restart)) {

    override fun NodeManager.onContent() {
        label(getString(R.string._please_restart_desc))
    }
}