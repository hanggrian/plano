package com.hendraanggrian.plano.help

import com.hendraanggrian.plano.License
import com.hendraanggrian.plano.PlanoApp
import com.hendraanggrian.plano.R
import com.hendraanggrian.plano.dialogs.Dialog
import ktfx.layouts.hyperlink
import ktfx.listeners.onAction

class LicensesDialog(app: PlanoApp) :
    Dialog(app, app.getString(R.string.open_source_licenses)) {
    init {
        License.listAll(
            "Ktfx" to "https://github.com/hendraanggrian/ktfx/blob/master/LICENSE",
        ).forEach { license ->
            hyperlink(license.name) {
                onAction { app.hostServices.showDocument(license.url) }
            }
        }
    }
}
