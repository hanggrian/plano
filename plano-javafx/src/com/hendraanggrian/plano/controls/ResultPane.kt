package com.hendraanggrian.plano.controls

import com.hendraanggrian.plano.Plano
import com.hendraanggrian.plano.PlanoApp
import com.hendraanggrian.plano.R
import com.hendraanggrian.plano.Resources
import com.hendraanggrian.plano.ResultFile
import com.hendraanggrian.plano.util.toCleanString
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.geometry.Pos
import javafx.scene.control.Button
import javax.imageio.ImageIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import ktfx.controls.paddingAll
import ktfx.coroutines.onAction
import ktfx.jfoenix.controls.jfxSnackbar
import ktfx.layouts.KtfxContextMenu
import ktfx.layouts.KtfxGridPane
import ktfx.layouts.anchorPane
import ktfx.layouts.circle
import ktfx.layouts.flowPane
import ktfx.layouts.hbox
import ktfx.layouts.label
import ktfx.layouts.menuItem
import ktfx.listeners.snapshot
import ktfx.minus
import ktfx.text.pt
import ktfx.times
import ktfx.util.toSwingImage

class ResultPane(
    app: PlanoApp,
    mediaWidth: Double,
    mediaHeight: Double,
    trimWidth: Double,
    trimHeight: Double,
    bleed: Double,
    allowFlip: Boolean,
    scaleProperty: DoubleProperty,
    fillProperty: BooleanProperty,
    thickProperty: BooleanProperty
) : KtfxGridPane(), Resources by app {

    init {
        paddingAll = 10.0
        val mediaBox = Plano.calculate(
            mediaWidth, mediaHeight,
            trimWidth, trimHeight,
            bleed, allowFlip
        )

        flowPane {
            prefWrapLengthProperty().bind(scaleProperty * mediaBox.width - MoreButton.RADIUS * 2)
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

        lateinit var moreButton: Button
        moreButton = addChild(
            object : MoreButton(app) {
                override fun KtfxContextMenu.onContextMenu() {
                    menuItem(getString(R.string.delete)) {
                        onAction { app.outputPane.children -= this@ResultPane.parent }
                    }
                    menuItem(getString(R.string.save)) {
                        onAction {
                            moreButton.isVisible = false
                            val file = ResultFile()
                            @Suppress("LABEL_NAME_CLASH")
                            this@ResultPane.snapshot { ImageIO.write(it.image.toSwingImage(), "png", file) }
                            GlobalScope.launch(Dispatchers.JavaFx) {
                                delay(500)
                                moreButton.isVisible = true
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
            }
        ) row 0 col 1 align Pos.CENTER_RIGHT
        anchorPane {
            addChild(MediaBoxPane(mediaBox, scaleProperty, fillProperty, thickProperty))
            mediaBox.forEach { addChild(TrimBoxPane(it, scaleProperty, fillProperty, thickProperty)) }
        } row 1 col (0 to 2)
    }
}
