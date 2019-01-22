package com.hendraanggrian.plano

import com.hendraanggrian.plano.control.DoubleField
import com.hendraanggrian.plano.control.Toolbar
import com.jfoenix.controls.JFXButton
import javafx.application.Application
import javafx.geometry.HPos
import javafx.geometry.Orientation
import javafx.geometry.Side
import javafx.scene.Node
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
import ktfx.jfoenix.jfxMasonryPane
import ktfx.jfoenix.jfxRadioButton
import ktfx.launchApplication
import ktfx.layouts.LayoutMarker
import ktfx.layouts.NodeInvokable
import ktfx.layouts.contextMenu
import ktfx.layouts.gridPane
import ktfx.layouts.hbox
import ktfx.layouts.label
import ktfx.layouts.menu
import ktfx.layouts.scene
import ktfx.layouts.separator
import ktfx.layouts.separatorMenuItem
import ktfx.layouts.vbox

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
                vbox {
                    Toolbar().apply {
                        leftItems {
                            label("Plano")
                        }
                    }()
                    separator()
                    gridPane {
                        paddingAll = 20
                        gap = 10
                        var row = 0

                        val radioGroup = ToggleGroup()
                        printSizeRadio = jfxRadioButton("From print size") {
                            graphic = ImageView(R.image.ic_print_size)
                            toggleGroup = radioGroup
                            isSelected = true
                        } row row++ col 0 colSpans 5
                        pieceSizeRadio = jfxRadioButton("From piece size") {
                            graphic = ImageView(R.image.ic_piece_size)
                            toggleGroup = radioGroup
                        } row row++ col 0 colSpans 5
                        row++

                        label("Plano size") row row col 0
                        planoWidthField() row row col 1
                        label("x") row row col 2
                        planoHeightField() row row col 3
                        moreButton(planoWidthField, planoHeightField) row row++ col 4

                        label {
                            textProperty().bind(buildBinding(printSizeRadio.selectedProperty()) {
                                when {
                                    printSizeRadio.isSelected -> "Print size"
                                    else -> "Piece size"
                                }
                            })
                        } row row col 0
                        printPieceWidthField() row row col 1
                        label("x") row row col 2
                        printPieceHeightField() row row col 3
                        moreButton(printPieceWidthField, printPieceHeightField) row row++ col 4

                        label("Trim") row row col 0
                        trimField() row row++ col 1

                        val init: Node.() -> Unit = {
                            visibleProperty().bind(pieceSizeRadio.selectedProperty())
                            managedProperty().bind(pieceSizeRadio.selectedProperty())
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

                        jfxButton("Calculate") {
                            styleClass += "raised"
                            maxWidth = Double.MAX_VALUE
                            buttonType = JFXButton.ButtonType.RAISED
                            prefHeight = 42.0
                            onAction {
                            }
                        } row row col 0 colSpans 5 halign HPos.CENTER
                    } vpriority Priority.ALWAYS
                }
                separator(Orientation.VERTICAL)
                jfxMasonryPane {
                } hpriority Priority.ALWAYS
            }
        }
        primaryStage.show()
    }

    private fun NodeInvokable.moreButton(
        widthField: TextField,
        heightField: TextField,
        init: ((@LayoutMarker JFXButton).() -> Unit)? = null
    ): Button = jfxButton(graphic = ImageView(R.image.ic_more)) {
        init?.invoke(this)
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