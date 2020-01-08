package com.hendraanggrian.plano.dialogs

import com.hendraanggrian.plano.Resources
import javafx.scene.layout.StackPane
import ktfx.layouts.label

class TextDialog(
    resources: Resources,
    container: StackPane,
    titleId: String,
    contentId: String
) : BaseDialog(resources, container, resources.getString(titleId)) {

    init {
        label(getString(contentId)) {
            prefWidth = 400.0
            isWrapText = true
        }
    }
}
