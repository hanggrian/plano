package com.hanggrian.plano.dialogs

import com.hanggrian.plano.PlanoApp
import ktfx.layouts.label

class TextDialog(app: PlanoApp, titleId: String, contentId: String) :
    Dialog(app, app.getString(titleId)) {
    init {
        label(getString(contentId)) {
            prefWidth = 400.0
            isWrapText = true
        }
    }
}
