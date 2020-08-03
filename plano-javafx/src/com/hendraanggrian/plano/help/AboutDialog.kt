package com.hendraanggrian.plano.help

import com.hendraanggrian.plano.BuildConfig
import com.hendraanggrian.plano.PlanoApp
import com.hendraanggrian.plano.R
import com.hendraanggrian.plano.dialogs.BaseDialog
import ktfx.layouts.label

class AboutDialog(app: PlanoApp) : BaseDialog(app, "${BuildConfig.NAME} ${BuildConfig.VERSION}") {

    init {
        label(getString(R.string._about)) {
            prefWidth = 300.0
            isWrapText = true
        }
    }
}
