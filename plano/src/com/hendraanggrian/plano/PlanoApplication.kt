package com.hendraanggrian.plano

import com.hendraanggrian.plano.control.DoubleField
import com.hendraanggrian.plano.control.Toolbar
import com.hendraanggrian.plano.control.border
import com.hendraanggrian.plano.control.moreButton
import com.hendraanggrian.plano.control.roundButton
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXMasonryPane
import javafx.application.Application
import javafx.beans.binding.Bindings.`when`
import javafx.geometry.HPos
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.stage.Stage
import ktfx.bindings.buildBooleanBinding
import ktfx.bindings.eq
import ktfx.bindings.minus
import ktfx.bindings.otherwise
import ktfx.bindings.then
import ktfx.bindings.times
import ktfx.boolean
import ktfx.controls.gap
import ktfx.controls.paddingAll
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.double
import ktfx.jfoenix.jfxMasonryPane
import ktfx.launchApplication
import ktfx.layouts.anchorPane
import ktfx.layouts.circle
import ktfx.layouts.gridPane
import ktfx.layouts.hbox
import ktfx.layouts.imageView
import ktfx.layouts.label
import ktfx.layouts.pane
import ktfx.layouts.region
import ktfx.layouts.scene
import ktfx.layouts.scrollPane
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
    private val scale = double(2.0).apply {
        listener { _, _, newValue ->
            outputPane.children.forEach {
                val pane = it as Pane
                val gridPane = pane.children[0] as GridPane
                val anchorPane = gridPane.children[0] as AnchorPane
                anchorPane.children.forEachIndexed { index, node ->
                    @Suppress("UNCHECKED_CAST")
                    when (index) {
                        0 -> {
                            node as Pane
                            pane.prefWidth = gridPane.prefWidth
                            pane.prefHeight = gridPane.prefHeight
                        }
                        else -> {
                            val (x, y) = node.userData as Pair<Double, Double>
                            AnchorPane.setLeftAnchor(node, x * newValue.toDouble())
                            AnchorPane.setTopAnchor(node, y * newValue.toDouble())
                        }
                    }

                }
            }
            outputPane.clearLayout()
            outputPane.requestLayout()
        }
    }

    val sheetWidthField = DoubleField()
    val sheetHeightField = DoubleField()
    val printPieceWidthField = DoubleField()
    val printPieceHeightField = DoubleField()
    val trimField = DoubleField()
    val maxPrintWidthField = DoubleField()
    val maxPrintHeightField = DoubleField()

    lateinit var scrollPane: ScrollPane
    lateinit var outputPane: JFXMasonryPane

    override fun start(stage: Stage) {
        stage.run {
            title = "Plano"
            setMinSize(600.0, 450.0)
            scene = scene {
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
                                    `when`(scale eq 2)
                                        then ImageView(R.image.ic_fullscreen)
                                        otherwise ImageView((R.image.ic_fullscreen_exit))
                                )
                                onAction {
                                    scale.set(
                                        when (scale.value) {
                                            2.0 -> 4.0
                                            else -> 2.0
                                        }
                                    )
                                }
                            }
                            roundButton(24.0, R.image.ic_settings) {
                                tooltip("Settings")
                                onAction {
                                }
                            }
                        }
                    }()
                    hbox {
                        gridPane {
                            border(Color.web("#c8c8c8"))
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
                                    outputPane.children += ktfx.layouts.pane {
                                        gridPane {
                                            paddingAll = 10
                                            gap = 10
                                            val rectangles = Plano.getPrintPoints(
                                                sheetWidthField.value,
                                                sheetHeightField.value,
                                                printPieceWidthField.value,
                                                printPieceHeightField.value,
                                                trimField.value
                                            )
                                            anchorPane {
                                                border(COLOR_YELLOW, 3)
                                                prefWidthProperty().bind(sheetWidthField.value * scale)
                                                prefHeightProperty().bind(sheetHeightField.value * scale)

                                                rectangles.forEach { rect ->
                                                    pane {
                                                        prefWidthProperty().bind(rect.width * scale)
                                                        prefHeightProperty().bind(rect.height * scale)
                                                        border(COLOR_RED, 3)
                                                        userData = rect.minX to rect.minY
                                                    } anchorLeft rect.minX * scale.value anchorTop rect.minY * scale.value
                                                }
                                            } row 0 rowSpans 3 col 0
                                            circle(radius = 4.0, fill = COLOR_YELLOW) row 0 col 1
                                            label("1") row 0 col 2
                                            circle(radius = 4.0, fill = COLOR_RED) row 1 col 1
                                            label("${rectangles.size}") row 1 col 2
                                        }
                                    }
                                }
                            } row row col 0 colSpans 6 halign HPos.RIGHT
                        }
                        scrollPane = scrollPane {
                            outputPane = jfxMasonryPane {
                                paddingAll = 10
                                prefWidthProperty().bind(this@scrollPane.widthProperty() - 10)
                            }
                        } hpriority Priority.ALWAYS
                    } vpriority Priority.ALWAYS
                }
            }
        }
        stage.show()

        sheetWidthField.requestFocus()
    }
}