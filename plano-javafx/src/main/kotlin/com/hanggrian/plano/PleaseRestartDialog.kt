package com.hanggrian.plano

import com.hanggrian.plano.controls.Dialog
import com.hanggrian.plano_javafx.R
import javafx.application.Platform
import ktfx.layouts.NodeContainer
import ktfx.layouts.label

class PleaseRestartDialog(app: PlanoApp) : Dialog(app, app.getString(R.string_please_restart)) {
    init {
        setOnDialogClosed { Platform.exit() }
    }

    override fun NodeContainer.onItems() {
        label(getString(R.string__please_restart)) {
            prefWidth = 400.0
            isWrapText = true
        }
    }
}
