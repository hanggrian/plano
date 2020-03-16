package com.hendraanggrian.plano.dialogs

import com.hendraanggrian.plano.BuildConfig
import com.hendraanggrian.plano.PlanoApp
import com.hendraanggrian.plano.R
import ktfx.jfoenix.layouts.jfxButton
import ktfx.layouts.NodeManager
import ktfx.layouts.label
import ktfx.listeners.onAction

class AboutDialog(private val app: PlanoApp) :
    BaseDialog(app, "${BuildConfig.NAME} ${BuildConfig.VERSION}") {

    init {
        label(getString(R.string._about)) {
            prefWidth = 300.0
            isWrapText = true
        }
    }

    override fun NodeManager.onButtons() {
        jfxButton(getString(R.string.btn_homepage)) {
            onAction {
                app.hostServices.showDocument(BuildConfig.HOMEPAGE)
                close()
            }
        }
    }
}
