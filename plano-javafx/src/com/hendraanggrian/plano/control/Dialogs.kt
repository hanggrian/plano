package com.hendraanggrian.plano.control

import com.hendraanggrian.plano.BuildConfig
import com.hendraanggrian.plano.R
import com.hendraanggrian.plano.Resources
import com.jfoenix.controls.JFXDialog
import javafx.scene.Node
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import ktfx.controls.paddingAll
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton
import ktfx.layouts.NodeManager
import ktfx.layouts._VBox
import ktfx.layouts.buttonBar
import ktfx.layouts.label
import ktfx.layouts.vbox
import java.awt.Desktop
import java.net.URI

sealed class Dialog(
    resources: Resources,
    container: StackPane,
    title: String
) : JFXDialog(container, _VBox(20.0), DialogTransition.CENTER), NodeManager, Resources by resources {

    private var contentPane: VBox

    override fun <R : Node> R.add(): R = also { contentPane.children += it }

    init {
        (content as _VBox).run {
            paddingAll = 20.0
            label(title) { styleClass.addAll("bold", "display") }
            contentPane = vbox()
            buttonBar {
                onButtons()
                jfxButton(getString(R.string.btn_close)) {
                    styleClass.addAll("flat", "bold")
                    onAction { close() }
                }
            }
        }
    }

    open fun NodeManager.onButtons() {
    }
}

class AboutDialog(
    resources: Resources,
    container: StackPane
) : Dialog(resources, container, "${BuildConfig.NAME} ${BuildConfig.VERSION}") {

    init {
        label(getString(R.string._about)) {
            prefWidth = 300.0
            isWrapText = true
        }
    }

    override fun NodeManager.onButtons() {
        jfxButton(getString(R.string.btn_homepage)) {
            styleClass.addAll("flat", "bold")
            onAction {
                Desktop.getDesktop()
                    .browse(URI(BuildConfig.HOMEPAGE))
                close()
            }
        }
    }
}

class TextDialog(
    resources: Resources,
    container: StackPane,
    titleId: String,
    contentId: String
) : Dialog(resources, container, resources.getString(titleId)) {

    init {
        label(getString(contentId)) {
            prefWidth = 400.0
            isWrapText = true
        }
    }
}
