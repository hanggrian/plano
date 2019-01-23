package com.hendraanggrian.plano

import com.hendraanggrian.plano.control.DoubleField
import com.hendraanggrian.plano.control.Toolbar
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXMasonryPane
import javafx.application.Application
import javafx.beans.binding.Bindings.`when`
import javafx.geometry.HPos
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.stage.Stage
import ktfx.bindings.buildBooleanBinding
import ktfx.bindings.eq
import ktfx.bindings.otherwise
import ktfx.bindings.then
import ktfx.bindings.times
import ktfx.boolean
import ktfx.controls.gap
import ktfx.controls.paddingAll
import ktfx.coroutines.onAction
import ktfx.double
import ktfx.jfoenix.jfxMasonryPane
import ktfx.launchApplication
import ktfx.layouts.circle
import ktfx.layouts.gridPane
import ktfx.layouts.hbox
import ktfx.layouts.imageView
import ktfx.layouts.label
import ktfx.layouts.rectangle
import ktfx.layouts.region
import ktfx.layouts.scene
import ktfx.layouts.separator
import ktfx.layouts.text
import ktfx.layouts.tooltip
import ktfx.layouts.vbox
import ktfx.windows.setMinSize

class PlanoApplication : Application() {

    companion object {

        val COLOR_YELLOW: Color = Color.web("#ffb300")
        val COLOR_RED: Color = Color.web("#f08077")
        val COLOR_GREEN: Color = Color.web("#c5ff85")

        @JvmStatic
        fun main(args: Array<String>) = launchApplication<PlanoApplication>(*args)
    }

    private val isPrintSizeMode = boolean(true)
    private val scale = double(1.0)

    val sheetWidthField = DoubleField()
    val sheetHeightField = DoubleField()
    val printPieceWidthField = DoubleField()
    val printPieceHeightField = DoubleField()
    val trimField = DoubleField()
    val maxPrintWidthField = DoubleField()
    val maxPrintHeightField = DoubleField()

    lateinit var outputPane: JFXMasonryPane

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
                        label("Plano") { styleClass += "display" }
                    }
                    rightItems {
                        roundButton(24.0, R.image.ic_refresh) {
                            tooltip("Reset")
                            onAction { outputPane.children.clear() }
                        }
                        roundButton(24.0, R.image.ic_print_size) {
                            tooltip("Toggle mode")
                            graphicProperty().bind(
                                `when`(isPrintSizeMode)
                                    then ImageView(R.image.ic_print_size)
                                    otherwise ImageView((R.image.ic_piece_size))
                            )
                            onAction { isPrintSizeMode.set(!isPrintSizeMode.value) }
                        }
                        roundButton(24.0, R.image.ic_fullscreen) {
                            tooltip("Toggle scale")
                            graphicProperty().bind(
                                `when`(scale eq 1)
                                    then ImageView(R.image.ic_fullscreen)
                                    otherwise ImageView((R.image.ic_fullscreen_exit))
                            )
                            onAction {
                                scale.set(
                                    when (scale.value) {
                                        1.0 -> 2.5
                                        else -> 1.0
                                    }
                                )
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
                            styleClass += "bold"
                            textProperty().bind(
                                `when`(isPrintSizeMode)
                                    then "Print size mode"
                                    otherwise "Piece size mode"
                            )
                        } row row++ col 0 colSpans 6
                        text {
                            wrappingWidth = 200.0
                            textProperty().bind(
                                `when`(isPrintSizeMode)
                                    then "Calculate how many print sizes fit in a sheet size"
                                    otherwise "Get the most efficient print size from piece size and max print size"
                            )
                        } row row++ col 0 colSpans 6

                        circle(radius = 4.0, fill = COLOR_YELLOW) row row col 0
                        label("Sheet size") row row col 1
                        sheetWidthField() row row col 2
                        label("x") row row col 3
                        sheetHeightField() row row col 4
                        moreButton(sheetWidthField, sheetHeightField) row row++ col 5

                        circle(radius = 4.0, fill = COLOR_RED) row row col 0
                        label {
                            textProperty().bind(
                                `when`(isPrintSizeMode)
                                    then "Print size"
                                    otherwise "Piece size"
                            )
                        } row row col 1
                        printPieceWidthField() row row col 2
                        label("x") row row col 3
                        printPieceHeightField() row row col 4
                        moreButton(printPieceWidthField, printPieceHeightField) row row++ col 5

                        label("Trim") row row col 1
                        trimField() row row++ col 2

                        val init: Node.() -> Unit = {
                            visibleProperty().bind(!isPrintSizeMode)
                            managedProperty().bind(!isPrintSizeMode)
                        }
                        circle(radius = 4.0, fill = COLOR_GREEN) { init() } row row col 0
                        label("Max print size") { init() } row row col 1
                        maxPrintWidthField.apply { init() }() row row col 2
                        label("x") { init() } row row col 3
                        maxPrintHeightField.apply { init() }() row row col 4
                        moreButton(maxPrintWidthField, maxPrintHeightField) { init() } row row++ col 5

                        row++
                        row++
                        roundButton(24.0, R.image.ic_send) {
                            styleClass += "raised"
                            buttonType = JFXButton.ButtonType.RAISED
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
                                outputPane.children += ktfx.layouts.stackPane {
                                    rectangle {
                                        widthProperty().bind(sheetWidthField.value * scale)
                                        heightProperty().bind(sheetHeightField.value * scale)
                                        fill = COLOR_RED
                                    }

                                    val rectangles = Plano.getPrintPoints(
                                        sheetWidthField.value * 2,
                                        sheetHeightField.value * 2,
                                        printPieceWidthField.value * 2,
                                        printPieceHeightField.value * 2,
                                        trimField.value * 2
                                    )

                                    rectangles.forEach {
                                        rectangle(it.minX, it.minY, it.width, it.height) {
                                            fill = COLOR_YELLOW
                                        }
                                    }
                                }
                            }
                        } row row col 0 colSpans 6 halign HPos.RIGHT
                    }
                    separator(Orientation.VERTICAL)
                    outputPane = jfxMasonryPane {
                    } hpriority Priority.ALWAYS
                } vpriority Priority.ALWAYS
            }
        }
        primaryStage.show()
    }
}