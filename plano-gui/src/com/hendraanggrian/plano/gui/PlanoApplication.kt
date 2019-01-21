package com.hendraanggrian.plano.gui

import com.hendraanggrian.plano.R
import com.hendraanggrian.plano.gui.control.DoubleField
import com.hendraanggrian.plano.gui.control.Toolbar
import javafx.application.Application
import javafx.geometry.Side
import javafx.scene.control.Button
import javafx.scene.control.RadioButton
import javafx.scene.control.TextField
import javafx.scene.control.ToggleGroup
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority
import javafx.scene.shape.Circle
import javafx.stage.Stage
import ktfx.bindings.buildBinding
import ktfx.controls.gap
import ktfx.controls.paddingAll
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton
import ktfx.jfoenix.jfxRadioButton
import ktfx.launchApplication
import ktfx.layouts.NodeInvokable
import ktfx.layouts.contextMenu
import ktfx.layouts.gridPane
import ktfx.layouts.hbox
import ktfx.layouts.label
import ktfx.layouts.menu
import ktfx.layouts.scene
import ktfx.layouts.scrollPane
import ktfx.layouts.separatorMenuItem

class PlanoApplication : Application() {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) = launchApplication<PlanoApplication>(*args)
    }

    lateinit var printSizeRadio: RadioButton
    lateinit var pieceSizeRadio: RadioButton

    val planoWidthField = DoubleField()
    val planoHeightField = DoubleField()

    val printPieceWidthField = DoubleField()
    val printPieceHeightField = DoubleField()
    val trimField = DoubleField()

    val maxPrintWidthField = DoubleField()
    val maxPrintHeightField = DoubleField()

    override fun start(primaryStage: Stage) {
        primaryStage.scene = scene {
            stylesheets += PlanoApplication::class.java.getResource(R.style.css_plano).toExternalForm()
            hbox {
                gridPane {
                    paddingAll = 20
                    gap = 10
                    var row = 0

                    val radioGroup = ToggleGroup()
                    printSizeRadio = jfxRadioButton("From print size") {
                        toggleGroup = radioGroup
                        isSelected = true
                    } row row++ col 0 colSpans 4
                    pieceSizeRadio = jfxRadioButton("From piece size") {
                        toggleGroup = radioGroup
                    } row row++ col 0 colSpans 4
                    row++

                    Toolbar().apply {
                        leftItems {
                            label("Plano size") {
                                maxWidth = Double.MAX_VALUE
                            }
                        }
                        rightItems {
                            moreButton(planoWidthField, planoHeightField)
                        }
                    }() row row++ col 0 colSpans 4
                    planoWidthField() row row col 0
                    label("x") row row col 1
                    planoHeightField() row row++ col 2

                    Toolbar().apply {
                        leftItems {
                            label {
                                maxWidth = Double.MAX_VALUE
                                textProperty().bind(buildBinding(printSizeRadio.selectedProperty()) {
                                    when {
                                        printSizeRadio.isSelected -> "Print size"
                                        else -> "Piece size"
                                    }
                                })
                            }
                        }
                        rightItems {
                            moreButton(printPieceWidthField, printPieceHeightField)
                        }
                    }() row row++ col 0 colSpans 4
                    printPieceWidthField() row row col 0
                    label("x") row row col 1
                    printPieceHeightField() row row++ col 2
                    label("Trim") row row++ col 0 colSpans 3
                    trimField() row row++ col 0

                    Toolbar().apply {
                        visibleProperty().bind(pieceSizeRadio.selectedProperty())
                        leftItems {
                            label("Max print size") {
                                maxWidth = Double.MAX_VALUE
                            }
                        }
                        rightItems {
                            moreButton(maxPrintWidthField, maxPrintHeightField)
                        }
                    }() row row++ col 0 colSpans 4
                    maxPrintWidthField.apply {
                        visibleProperty().bind(pieceSizeRadio.selectedProperty())
                    }() row row col 0
                    label("x") {
                        visibleProperty().bind(pieceSizeRadio.selectedProperty())
                    } row row col 1
                    maxPrintHeightField.apply {
                        visibleProperty().bind(pieceSizeRadio.selectedProperty())
                    }() row row col 2
                }
                scrollPane {

                } hpriority Priority.ALWAYS
            }
        }
        primaryStage.show()
    }

    private fun NodeInvokable.moreButton(widthField: TextField, heightField: TextField): Button =
        jfxButton(graphic = ImageView(R.image.ic_more)) {
            val r = 16.0
            shape = Circle(r)
            setMinSize(2 * r, 2 * r)
            setMaxSize(2 * r, 2 * r)
            val contextMenu = contextMenu {
                menu("A ...") {

                }
                menu("B ...") {

                }
                separatorMenuItem()
                "65 x 90" {
                    onAction {
                        widthField.text = "65"
                        heightField.text = "90"
                    }
                }
            }
            onAction {
                if (!contextMenu.isShowing) {
                    contextMenu.show(this@jfxButton, Side.RIGHT, 0.0, 0.0)
                }
            }
        }
}