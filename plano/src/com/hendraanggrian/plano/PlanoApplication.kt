package com.hendraanggrian.plano

import com.hendraanggrian.plano.control.DoubleField
import com.hendraanggrian.plano.control.Toolbar
import com.jfoenix.controls.JFXButton
import javafx.application.Application
import javafx.beans.binding.Bindings.`when`
import javafx.geometry.HPos
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.Tooltip
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority
import javafx.stage.Stage
import ktfx.bindings.buildBooleanBinding
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
import ktfx.layouts.tooltip
import ktfx.layouts.vbox
import ktfx.windows.setMinSize

class PlanoApplication : Application() {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) = launchApplication<PlanoApplication>(*args)
    }

    private val isPrintSizeMode = boolean(true)

    val planoWidthField = DoubleField()
    val planoHeightField = DoubleField()

    val printPieceWidthField = DoubleField()
    val printPieceHeightField = DoubleField()
    val trimField = DoubleField()

    val maxPrintWidthField = DoubleField()
    val maxPrintHeightField = DoubleField()

    override fun start(primaryStage: Stage) {
        primaryStage.setMinSize(600.0, 400.0)
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
                                planoWidthField.clear()
                                planoHeightField.clear()
                                printPieceWidthField.clear()
                                printPieceHeightField.clear()
                                trimField.clear()
                                maxPrintWidthField.clear()
                                maxPrintHeightField.clear()
                            }
                        }
                        roundButton(24.0, R.image.ic_print_size) {
                            graphicProperty().bind(
                                `when`(isPrintSizeMode)
                                    then ImageView(R.image.ic_print_size)
                                    otherwise ImageView((R.image.ic_piece_size))
                            )
                            tooltipProperty().bind(
                                `when`(isPrintSizeMode)
                                    then Tooltip("Print size mode")
                                    otherwise Tooltip("Piece size mode")
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
                            styleClass.addAll("accent", "bold")
                            textProperty().bind(
                                `when`(isPrintSizeMode)
                                    then "Mode 1"
                                    otherwise "Mode 2"
                            )
                        } row row++ col 0 colSpans 5

                        label("Plano size") row row col 0
                        planoWidthField() row row col 1
                        label("x") row row col 2
                        planoHeightField() row row col 3
                        moreButton(planoWidthField, planoHeightField) row row++ col 4

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
                                planoWidthField.textProperty(),
                                planoHeightField.textProperty(),
                                printPieceWidthField.textProperty(),
                                printPieceHeightField.textProperty(),
                                maxPrintWidthField.textProperty(),
                                maxPrintHeightField.textProperty()
                            ) {
                                when {
                                    planoWidthField.value <= 0.0 || planoHeightField.value <= 0.0 -> true
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