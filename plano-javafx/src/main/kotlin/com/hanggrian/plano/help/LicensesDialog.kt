package com.hanggrian.plano.help

import com.hanggrian.plano.License
import com.hanggrian.plano.PlanoApp
import com.hanggrian.plano.controls.Dialog
import com.hanggrian.plano_javafx.R
import ktfx.coroutines.onAction
import ktfx.layouts.NodeContainer
import ktfx.layouts.hyperlink

class LicensesDialog(private val app: PlanoApp) :
    Dialog(app, app.getString(R.string_open_source_licenses)) {
    override fun NodeContainer.onItems() {
        License
            .listAll("KtFX" to "https://github.com/hanggrian/ktfx/blob/master/LICENSE")
            .forEach { license ->
                hyperlink(license.name) {
                    onAction { app.hostServices.showDocument(license.url) }
                }
            }
    }
}
