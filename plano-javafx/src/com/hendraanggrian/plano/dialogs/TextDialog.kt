package com.hendraanggrian.plano.dialogs

import com.hendraanggrian.plano.PlanoApp
import ktfx.layouts.label

class TextDialog(app: PlanoApp, titleId: String, contentId: String) :
    BaseDialog(app, app.getString(titleId)) {

    init {
        label(getString(contentId)) {
            prefWidth = 400.0
            isWrapText = true
        }
    }
}
