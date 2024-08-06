package com.hanggrian.plano.help

import com.hanggrian.plano.PlanoApp
import com.hanggrian.plano.dialogs.Dialog
import com.hanggrian.plano_javafx.BuildConfig
import com.hanggrian.plano_javafx.R
import ktfx.layouts.label

class AboutDialog(app: PlanoApp) : Dialog(app, "${BuildConfig.NAME} ${BuildConfig.VERSION}") {
    init {
        label(getString(R.string__about)) {
            prefWidth = 300.0
            isWrapText = true
        }
    }
}
