package com.hendraanggrian.plano.dialogs

import com.hendraanggrian.plano.BuildConfig
import com.hendraanggrian.plano.PlanoApp
import com.hendraanggrian.plano.R
import ktfx.jfoenix.layouts.jfxButton
import ktfx.layouts.NodeManager
import ktfx.layouts.button
import ktfx.layouts.label
import ktfx.listeners.onAction

class AboutDialog(private val app: PlanoApp) : BaseDialog(app, app.getString(R.string.about)) {

    init {
        label(getString(R.string._about)) {
            prefWidth = 300.0
            isWrapText = true
        }
        button(getString(R.string.about_option1))
        button(getString(R.string.about_option2))
        button(getString(R.string.about_option3))
    }

    override fun NodeManager.onButtons() {
        jfxButton(getString(R.string.btn_github)) {
            onAction {
                app.hostServices.showDocument(BuildConfig.WEB)
                close()
            }
        }
    }
}
