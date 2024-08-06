package com.hanggrian.plano.controls

import com.hanggrian.plano.MediaSize
import com.hanggrian.plano.PlanoApp
import com.hanggrian.plano.Resources
import com.hanggrian.plano.ResultFile
import com.hanggrian.plano.clean
import com.hanggrian.plano.util.getResource
import com.hanggrian.plano_javafx.R
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.scene.control.Button
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.FlowPane
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import ktfx.bindings.minus
import ktfx.bindings.or
import ktfx.bindings.times
import ktfx.controls.LEFT
import ktfx.controls.insetsOf
import ktfx.controls.toSwingImage
import ktfx.coroutines.capture
import ktfx.coroutines.onAction
import ktfx.coroutines.onContextMenuRequested
import ktfx.jfoenix.controls.jfxSnackbar
import ktfx.layouts.KtfxGridPane
import ktfx.layouts.anchorPane
import ktfx.layouts.checkMenuItem
import ktfx.layouts.contextMenu
import ktfx.layouts.flowPane
import ktfx.layouts.hbox
import ktfx.layouts.menuItem
import ktfx.layouts.pane
import ktfx.layouts.separatorMenuItem
import ktfx.layouts.styledCircle
import ktfx.layouts.styledLabel
import ktfx.windows.moveTo
import javax.imageio.ImageIO

class ResultPane(
    private val app: PlanoApp,
    mediaWidth: Float,
    mediaHeight: Float,
    private val trimWidth: Float,
    private val trimHeight: Float,
    gapHorizontal: Float,
    gapVertical: Float,
    allowFlipColumn: Boolean,
    allowFlipRow: Boolean,
    private val scaleProperty: DoubleProperty,
    private val fillProperty: BooleanProperty,
    private val thickProperty: BooleanProperty,
) : KtfxGridPane(),
    Resources by app {
    private val mediaLabel: Label
    private val trimSizeLabel: Label
    private val trimLabel: Label
    private val infoFlowPane: FlowPane
    private val boxPaneContainer: AnchorPane
    private val closeButton: Button
    private val contextMenu: ContextMenu

    init {
        padding = insetsOf(10)
        val mediaBox = MediaSize(mediaWidth, mediaHeight)
        mediaBox.populate(
            trimWidth,
            trimHeight,
            gapHorizontal,
            gapVertical,
            allowFlipColumn,
            allowFlipRow,
        )

        infoFlowPane =
            flowPane {
                hgap = 10.0
                hbox {
                    alignment = LEFT
                    styledCircle(radius = 4.0, id = R.style_circle_amber)
                    pane { minWidth = 5.0 }
                    mediaLabel = styledLabel(id = R.style_label_info)
                }
                hbox {
                    alignment = LEFT
                    styledCircle(radius = 4.0, id = R.style_circle_red)
                    trimSizeLabel = styledLabel(id = R.style_label_red)
                    pane { minWidth = 5.0 }
                    trimLabel = styledLabel(id = R.style_label_info)
                }
            }.grid(0, 0).fillHeight(false)
        closeButton =
            addChild(
                RoundButton(app, RoundButton.RADIUS_SMALL, R.string_close).apply {
                    id = R.style_menu_close
                    onAction { close() }
                },
            ).grid(0, 1)
        boxPaneContainer = anchorPane().grid(1, 0 to 2)

        contextMenu =
            contextMenu {
                menuItem(getString(R.string_rotate)) {
                    onAction {
                        mediaBox.rotate()
                        populate(mediaBox)
                    }
                }
                checkMenuItem(getString(R.string_allow_flip_right)) {
                    isSelected = mediaBox.isAllowFlipRight
                    onAction {
                        mediaBox.isAllowFlipRight = !mediaBox.isAllowFlipRight
                        populate(mediaBox)
                    }
                }
                checkMenuItem(getString(R.string_allow_flip_bottom)) {
                    isSelected = mediaBox.isAllowFlipBottom
                    onAction {
                        mediaBox.isAllowFlipBottom = !mediaBox.isAllowFlipBottom
                        populate(mediaBox)
                    }
                }
                separatorMenuItem()
                menuItem(getString(R.string_save)) {
                    onAction {
                        val isDarkTheme =
                            getResource(R.style_plano_dark) in this@ResultPane.scene.stylesheets
                        if (isDarkTheme) {
                            mediaLabel.id = R.style_label_black
                            trimLabel.id = R.style_label_black
                        }
                        val file = ResultFile()
                        this@ResultPane.capture {
                            ImageIO.write(it.image.toSwingImage(), "png", file)
                        }
                        launch(Dispatchers.JavaFx) {
                            delay(500)
                            if (isDarkTheme) {
                                mediaLabel.id = null
                                trimLabel.id = null
                            }
                            app.rootPane.jfxSnackbar(
                                getString(R.string__save).format(file.name),
                                PlanoApp.DURATION_SHORT,
                                getString(R.string_btn_show_directory),
                            ) {
                                app.hostServices.showDocument(file.parentFile.toURI().toString())
                            }
                        }
                    }
                }
            }
        onContextMenuRequested {
            contextMenu.moveTo(it)
            contextMenu.show(scene.window)
        }

        closeButton
            .visibleProperty()
            .bind(this@ResultPane.hoverProperty() or contextMenu.showingProperty())
        populate(mediaBox)
    }

    private fun populate(mediaBox: MediaSize) {
        mediaLabel.text = "${mediaBox.width.clean()} x ${mediaBox.height.clean()}"
        trimSizeLabel.text = " ${mediaBox.size}"
        trimLabel.text = "${trimWidth.clean()} x ${trimHeight.clean()}"
        infoFlowPane
            .prefWrapLengthProperty()
            .bind(scaleProperty * mediaBox.width - 12.0 * 2) // minus close button
        boxPaneContainer.children.clear()
        boxPaneContainer.children +=
            MediaSizePane(
                mediaBox,
                scaleProperty,
                fillProperty,
                thickProperty,
            )
        mediaBox.forEach {
            boxPaneContainer.children +=
                TrimSizePane(
                    it,
                    scaleProperty,
                    fillProperty,
                    thickProperty,
                )
        }
    }

    private fun close() {
        app.outputPane.children -= this@ResultPane.parent
        app.rootPane.requestFocus()
    }
}
