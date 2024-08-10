package com.hanggrian.plano.controls

import com.hanggrian.plano.MediaSize
import com.hanggrian.plano.PlanoApp
import com.hanggrian.plano.Resources
import com.hanggrian.plano.ResultFile
import com.hanggrian.plano.clean
import com.hanggrian.plano.dialogs.Dialog
import com.hanggrian.plano.util.getResource
import com.hanggrian.plano_javafx.R
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.geometry.HPos
import javafx.scene.control.Button
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.FlowPane
import kotlinx.coroutines.Dispatchers
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
import ktfx.layouts.gridPane
import ktfx.layouts.hbox
import ktfx.layouts.label
import ktfx.layouts.menuItem
import ktfx.layouts.pane
import ktfx.layouts.separatorMenuItem
import ktfx.layouts.styledCircle
import ktfx.layouts.styledImageView
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
    private val trimLabel: Label
    private val countImage: ImageView
    private val countLabel: Label
    private val coverageImage: ImageView
    private val coverageLabel: Label
    private val infoFlowPane: FlowPane
    private val boxPaneContainer: AnchorPane
    private val closeButton: Button
    private val contextMenu: ContextMenu

    init {
        padding = insetsOf(10)
        val mediaSize = MediaSize(mediaWidth, mediaHeight)
        mediaSize.populate(
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
                    pane { minWidth = 5.0 }
                    trimLabel = styledLabel(id = R.style_label_info)
                }
                hbox {
                    countImage = styledImageView(id = R.style_in_quantity)
                    countLabel = styledLabel(id = R.style_label_info)
                }
                hbox {
                    coverageImage = styledImageView(id = R.style_in_coverage)
                    coverageLabel = styledLabel(id = R.style_label_info)
                }
            }.grid(0, 0)
                .fillHeight(false)
        closeButton =
            addChild(
                RoundButton(app, RoundButton.RADIUS_BTN, R.string_close).apply {
                    id = R.style_btn_close
                    onAction { close() }
                },
            ).grid(0, 1)
        boxPaneContainer =
            anchorPane()
                .grid(1, 0 to 2)

        contextMenu =
            contextMenu {
                menuItem(getString(R.string_view_sizes)) {
                    onAction {
                        SizesDialog(app, mediaSize).show()
                    }
                }
                separatorMenuItem()
                menuItem(getString(R.string_rotate)) {
                    onAction {
                        mediaSize.rotate()
                        populate(mediaSize)
                    }
                }
                checkMenuItem(getString(R.string_allow_flip_right)) {
                    isSelected = mediaSize.isAllowFlipRight
                    onAction {
                        mediaSize.isAllowFlipRight = !mediaSize.isAllowFlipRight
                        populate(mediaSize)
                    }
                }
                checkMenuItem(getString(R.string_allow_flip_bottom)) {
                    isSelected = mediaSize.isAllowFlipBottom
                    onAction {
                        mediaSize.isAllowFlipBottom = !mediaSize.isAllowFlipBottom
                        populate(mediaSize)
                    }
                }
                separatorMenuItem()
                menuItem(getString(R.string_save)) {
                    onAction {
                        val isDarkTheme =
                            getResource(R.style_plano_dark) in this@ResultPane.scene.stylesheets
                        if (isDarkTheme) {
                            mediaLabel.id = R.style_label_info_black
                            trimLabel.id = R.style_label_info_black
                            countLabel.id = R.style_label_info_black
                            coverageLabel.id = R.style_label_info_black

                            countImage.id = null
                            coverageImage.id = null
                            countImage.image = Image(R.image_in_quantity)
                            coverageImage.image = Image(R.image_in_coverage)
                        }
                        val file = ResultFile()
                        this@ResultPane.capture {
                            ImageIO.write(it.image.toSwingImage(), "png", file)
                        }
                        launch(Dispatchers.JavaFx) {
                            if (isDarkTheme) {
                                mediaLabel.id = R.style_label_info
                                trimLabel.id = R.style_label_info
                                countLabel.id = R.style_label_info
                                coverageLabel.id = R.style_label_info

                                countImage.id = R.style_in_quantity
                                coverageImage.id = R.style_in_coverage
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
        populate(mediaSize)
    }

    private fun populate(mediaBox: MediaSize) {
        mediaLabel.text = "${mediaBox.width.clean()} \u00D7 ${mediaBox.height.clean()}"
        trimLabel.text = "${trimWidth.clean()} \u00D7 ${trimHeight.clean()}"
        countLabel.text = " ${mediaBox.size} pcs"
        coverageLabel.text = " ${mediaBox.coverage.clean()}%"
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
        boxPaneContainer.children +=
            mediaBox.map {
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

    private class SizesDialog(app: PlanoApp, mediaSize: MediaSize) :
        Dialog(app, app.getString(R.string_sizes)) {
        init {
            gridPane {
                hgap = 10.0
                vgap = 10.0

                label("${getString(R.string_main_width)}:")
                    .grid(0, 0)
                    .hgrow()
                    .halign(HPos.RIGHT)
                label(mediaSize.mainWidth.clean())
                    .grid(0, 1)
                label("${getString(R.string_main_height)}:")
                    .grid(1, 0)
                    .hgrow()
                    .halign(HPos.RIGHT)
                label(mediaSize.mainHeight.clean())
                    .grid(1, 1)
                label("${getString(R.string_remaining_width)}:")
                    .grid(2, 0)
                    .hgrow()
                    .halign(HPos.RIGHT)
                label(mediaSize.remainingWidth.clean())
                    .grid(2, 1)
                label("${getString(R.string_remaining_height)}:")
                    .grid(3, 0)
                    .hgrow()
                    .halign(HPos.RIGHT)
                label(mediaSize.remainingHeight.clean())
                    .grid(3, 1)
            }
        }
    }
}
