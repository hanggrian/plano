package com.hendraanggrian.plano

import com.hendraanggrian.plano.control.DoubleField
import com.hendraanggrian.plano.control.Toolbar
import com.jfoenix.controls.JFXButton
import javafx.application.Application
import javafx.beans.binding.Bindings.`when`
import javafx.geometry.HPos
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority
import javafx.stage.Stage
import ktfx.bindings.buildBooleanBinding
import ktfx.bindings.minus
import ktfx.bindings.otherwise
import ktfx.bindings.then
import ktfx.boolean
import ktfx.controls.gap
import ktfx.controls.paddingAll
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxMasonryPane
import ktfx.launchApplication
import ktfx.layouts.gridPane
import ktfx.layouts.hbox
import ktfx.layouts.imageView
import ktfx.layouts.label
import ktfx.layouts.region
import ktfx.layouts.scene
import ktfx.layouts.separator
import ktfx.layouts.text
import ktfx.layouts.tooltip
import ktfx.layouts.vbox
import ktfx.windows.setMinSize

class PlanoApplication : Application() {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) = launchApplication<PlanoApplication>(*args)
    }

    private val isPrintSizeMode = boolean(true)

    val sheetWidthField = DoubleField()
    val sheetHeightField = DoubleField()

    val printPieceWidthField = DoubleField()
    val printPieceHeightField = DoubleField()
    val trimField = DoubleField()

    val maxPrintWidthField = DoubleField()
    val maxPrintHeightField = DoubleField()

    override fun start(primaryStage: Stage) {
        primaryStage.setMinSize(600.0, 450.0)
        primaryStage.scene = scene {
            stylesheets.addAll(
                PlanoApplication::class.java.getResource(R.style.css_plano).toExternalForm(),
                PlanoApplication::class.java.getResource(R.style.css_plano_font).toExternalForm()
            )
            vbox {
                Toolbar().apply {
                    leftItems {
                        imageView(R.image.ic_launcher)
                        region { prefWidth = 12.0 }
                        label("Plano") {
                            styleClass += "display"
                        }
                    }
                    rightItems {
                        roundButton(24.0, R.image.ic_refresh) {
                            tooltip("Reset")
                            onAction {
                                sheetWidthField.clear()
                                sheetHeightField.clear()
                                printPieceWidthField.clear()
                                printPieceHeightField.clear()
                                trimField.clear()
                                maxPrintWidthField.clear()
                                maxPrintHeightField.clear()
                            }
                        }
                        roundButton(24.0, R.image.ic_print_size) {
                            tooltip("Toggle mode")
                            graphicProperty().bind(
                                `when`(isPrintSizeMode)
                                    then ImageView(R.image.ic_print_size)
                                    otherwise ImageView((R.image.ic_piece_size))
                            )
                            onAction {
                                isPrintSizeMode.set(!isPrintSizeMode.value)
                            }
                        }
                        roundButton(24.0, R.image.ic_settings) {
                            tooltip("Settings")
                        }
                    }
                }()
                separator()
                hbox {
                    gridPane {
                        paddingAll = 20
                        gap = 10
                        var row = 0

                        label {
                            styleClass.addAll("bold", "accent")
                            textProperty().bind(
                                `when`(isPrintSizeMode)
                                    then "Print size mode"
                                    otherwise "Piece size mode"
                            )
                        } row row++ col 0 colSpans 5
                        text {
                            wrappingWidthProperty().bind(this@gridPane.widthProperty() - 40)
                            textProperty().bind(
                                `when`(isPrintSizeMode)
                                    then "Calculate how many print sizes fit in a sheet size"
                                    otherwise "Get the most efficient print size from piece size and max print size"
                            )
                        } row row++ col 0 colSpans 5

                        label("Sheet size") row row col 0
                        sheetWidthField() row row col 1
                        label("x") row row col 2
                        sheetHeightField() row row col 3
                        moreButton(sheetWidthField, sheetHeightField) row row++ col 4

                        label {
                            textProperty().bind(
                                `when`(isPrintSizeMode)
                                    then "Print size"
                                    otherwise "Piece size"
                            )
                        } row row col 0
                        printPieceWidthField() row row col 1
                        label("x") row row col 2
                        printPieceHeightField() row row col 3
                        moreButton(printPieceWidthField, printPieceHeightField) row row++ col 4

                        label("Trim") row row col 0
                        trimField() row row++ col 1

                        val init: Node.() -> Unit = {
                            visibleProperty().bind(!isPrintSizeMode)
                            managedProperty().bind(!isPrintSizeMode)
                        }
                        label("Max print size") {
                            init()
                        } row row col 0
                        maxPrintWidthField.apply {
                            init()
                        }() row row col 1
                        label("x") {
                            init()
                        } row row col 2
                        maxPrintHeightField.apply {
                            init()
                        }() row row col 3
                        moreButton(maxPrintWidthField, maxPrintHeightField) {
                            init()
                        } row row++ col 4

                        row++
                        row++
                        roundButton(24.0, R.image.ic_send) {
                            styleClass += "raised"
                            buttonType = JFXButton.ButtonType.RAISED
                            prefHeight = 24.0
                            disableProperty().bind(buildBooleanBinding(
                                isPrintSizeMode,
                                sheetWidthField.textProperty(),
                                sheetHeightField.textProperty(),
                                printPieceWidthField.textProperty(),
                                printPieceHeightField.textProperty(),
                                maxPrintWidthField.textProperty(),
                                maxPrintHeightField.textProperty()
                            ) {
                                when {
                                    sheetWidthField.value <= 0.0 || sheetHeightField.value <= 0.0 -> true
                                    printPieceWidthField.value <= 0.0 || printPieceHeightField.value <= 0.0 -> true
                                    else -> when {
                                        !isPrintSizeMode.value -> when {
                                            maxPrintWidthField.value <= 0.0 || maxPrintWidthField.value <= 0.0 -> true
                                            else -> false
                                        }
                                        else -> false
                                    }
                                }
                            })
                            onAction {
                            }
                        } row row col 0 colSpans 5 halign HPos.RIGHT
                    }
                    separator(Orientation.VERTICAL)
                    jfxMasonryPane {
                    } hpriority Priority.ALWAYS
                } vpriority Priority.ALWAYS
            }
        }
        primaryStage.show()
    }
}