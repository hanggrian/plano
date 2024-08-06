package com.hanggrian.plano.help

import com.hanggrian.plano.License
import com.hanggrian.plano.PlanoApp
import com.hanggrian.plano.dialogs.Dialog
import com.hanggrian.plano_javafx.R
import ktfx.coroutines.onAction
import ktfx.layouts.hyperlink

class LicensesDialog(app: PlanoApp) :
    Dialog(app, app.getString(R.string_open_source_licenses)) {
    init {
        License
            .listAll("Ktfx" to "https://github.com/hanggrian/ktfx/blob/master/LICENSE")
            .forEach { license ->
                hyperlink(license.name) {
                    onAction { app.hostServices.showDocument(license.url) }
                }
            }
    }
}
