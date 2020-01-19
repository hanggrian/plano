package com.hendraanggrian.plano.controls

import com.hendraanggrian.plano.MediaBox
import com.hendraanggrian.plano.PlanoApp
import com.hendraanggrian.plano.R
import com.hendraanggrian.plano.Resources
import com.hendraanggrian.plano.ResultFile
import com.hendraanggrian.plano.util.toCleanString
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.ContextMenu
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.FlowPane
import javax.imageio.ImageIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import ktfx.controls.paddingAll
import ktfx.coroutines.onAction
import ktfx.jfoenix.controls.jfxSnackbar
import ktfx.layouts.KtfxGridPane
import ktfx.layouts.addChild
import ktfx.layouts.anchorPane
import ktfx.layouts.circle
import ktfx.layouts.contextMenu
import ktfx.layouts.flowPane
import ktfx.layouts.hbox
import ktfx.layouts.label
import ktfx.layouts.menuItem
import ktfx.layouts.separatorMenuItem
import ktfx.listeners.onContextMenuRequested
import ktfx.listeners.snapshot
import ktfx.minus
import ktfx.or
import ktfx.text.pt
import ktfx.times
import ktfx.util.toSwingImage

class ResultPane(
    private val app: PlanoApp,
    mediaWidth: Double,
    mediaHeight: Double,
    trimWidth: Double,
    trimHeight: Double,
    bleed: Double,
    allowFlip: Boolean,
    private val scaleProperty: DoubleProperty,
    private val fillProperty: BooleanProperty,
    private val thickProperty: BooleanProperty
) : KtfxGridPane(), Resources by app {

    private val infoFlowPane: FlowPane
    private val boxPaneContainer: AnchorPane
    private val closeButton: Button
    private val contextMenu: ContextMenu

    init {
        paddingAll = 10.0
        val mediaBox = MediaBox(mediaWidth, mediaHeight)
        mediaBox.populate(trimWidth, trimHeight, bleed, allowFlip)

        infoFlowPane = flowPane {
            hgap = 10.0
            hbox {
                alignment = Pos.CENTER_LEFT
                spacing = 5.0
                circle(radius = 4.0) {
                    id = R.style.circle_amber
                }
                label("${mediaWidth.toCleanString()} x ${mediaHeight.toCleanString()}") {
                    font = 11.pt
                }
            }
            hbox {
                alignment = Pos.CENTER_LEFT
                spacing = 5.0
                circle(radius = 4.0) {
                    id = R.style.circle_red
                }
                label("${mediaBox.size}") {
                    id = R.style.label_red
                    font = 11.pt
                }
                label("${(trimWidth + bleed * 2).toCleanString()} x ${(trimHeight + bleed * 2).toCleanString()}") {
                    font = 11.pt
                }
            }
        } row 0 col 0 fillHeight false
        closeButton = addChild(RoundMenuButton(this, R.string.close)) {
            id = R.style.menu_close
            onAction { close() }
        } row 0 col 1
        boxPaneContainer = anchorPane() row 1 col (0 to 2)

        contextMenu = contextMenu {
            menuItem(getString(R.string.rotate)) {
                id = R.style.menu_rotate
                onAction {
                    mediaBox.width = mediaBox.height.also { mediaBox.height = mediaBox.width }
                    mediaBox.populate(trimWidth, trimHeight, bleed, allowFlip)
                    populate(mediaBox)
                }
            }
            separatorMenuItem()
            menuItem(getString(R.string.close)) {
                id = R.style.menu_empty
                onAction { close() }
            }
            menuItem(getString(R.string.close_all)) {
                id = R.style.menu_empty
                onAction { app.closeAll() }
            }
            separatorMenuItem()
            menuItem(getString(R.string.save)) {
                id = R.style.menu_empty
                onAction {
                    val file = ResultFile()
                    @Suppress("LABEL_NAME_CLASH")
                    this@ResultPane.snapshot { ImageIO.write(it.image.toSwingImage(), "png", file) }
                    GlobalScope.launch(Dispatchers.JavaFx) {
                        delay(500)
                        app.rootPane.jfxSnackbar(
                            getString(R.string._save).format(file.name),
                            PlanoApp.DURATION_SHORT,
                            getString(R.string.btn_show_directory)
                        ) {
                            app.hostServices.showDocument(file.parentFile.toURI().toString())
                        }
                    }
                }
            }
        }
        onContextMenuRequested {
            contextMenu.x = it.screenX
            contextMenu.y = it.screenY
            contextMenu.show(scene.window)
        }

        closeButton.visibleProperty().bind(this@ResultPane.hoverProperty() or contextMenu.showingProperty())
        populate(mediaBox)
    }

    private fun populate(mediaBox: MediaBox) {
        infoFlowPane.prefWrapLengthProperty().bind(scaleProperty * mediaBox.width - RoundMenuButton.RADIUS * 2)
        boxPaneContainer.children.clear()
        boxPaneContainer.children += MediaBoxPane(mediaBox, scaleProperty, fillProperty, thickProperty)
        mediaBox.forEach { boxPaneContainer.children += TrimBoxPane(it, scaleProperty, fillProperty, thickProperty) }
    }

    private fun close() {
        app.outputPane.children -= this@ResultPane.parent
        if (app.outputPane.children.isEmpty()) {
            app.mediaWidthField.requestFocus()
        }
    }
}
