package com.hendraanggrian.plano

import com.hendraanggrian.plano.control.AboutDialog
import com.hendraanggrian.plano.control.DoubleField
import com.hendraanggrian.plano.control.Toolbar
import com.hendraanggrian.plano.control.border
import com.hendraanggrian.plano.control.moreButton
import com.hendraanggrian.plano.control.morePaperButton
import com.hendraanggrian.plano.control.roundButton
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXMasonryPane
import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.geometry.HPos
import javafx.geometry.Side
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.control.ToggleGroup
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
import ktfx.collections.isEmptyBinding
import ktfx.controls.gap
import ktfx.controls.paddingAll
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.double
import ktfx.jfoenix.jfxMasonryPane
import ktfx.launchApplication
import ktfx.layouts.anchorPane
import ktfx.layouts.borderPane
import ktfx.layouts.circle
import ktfx.layouts.contextMenu
import ktfx.layouts.gridPane
import ktfx.layouts.hbox
import ktfx.layouts.imageView
import ktfx.layouts.label
import ktfx.layouts.menu
import ktfx.layouts.pane
import ktfx.layouts.radioMenuItem
import ktfx.layouts.region
import ktfx.layouts.scene
import ktfx.layouts.scrollPane
import ktfx.layouts.separatorMenuItem
import ktfx.layouts.stackPane
import ktfx.layouts.text
import ktfx.layouts.textFlow
import ktfx.layouts.tooltip
import ktfx.layouts.vbox
import ktfx.runLater
import ktfx.windows.setMinSize

class PlanoApplication : Application() {

    companion object {

        const val SCALE_SMALL = 2.0
        const val SCALE_BIG = 4.0

        val COLOR_YELLOW: Color = Color.web("#ffb300")
        val COLOR_RED: Color = Color.web("#f08077")

        @JvmStatic
        fun main(args: Array<String>) = launchApplication<PlanoApplication>(*args)
    }

    private val scale = double(SCALE_SMALL).apply {
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

    val sheetWidthField = DoubleField().apply { onAction { sendButton.fire() } }
    val sheetHeightField = DoubleField().apply { onAction { sendButton.fire() } }
    val printWidthField = DoubleField().apply { onAction { sendButton.fire() } }
    val printHeightField = DoubleField().apply { onAction { sendButton.fire() } }
    val trimField = DoubleField().apply { onAction { sendButton.fire() } }

    lateinit var sendButton: Button
    lateinit var outputPane: JFXMasonryPane

    override fun start(stage: Stage) {
        stage.title = BuildConfig.NAME
        stage.setMinSize(700.0, 400.0)
        stage.scene = scene {
            stylesheets.addAll(
                PlanoApplication::class.java.getResource(R.style.css_plano).toExternalForm(),
                PlanoApplication::class.java.getResource(R.style.css_plano_font).toExternalForm()
            )
            stackPane {
                vbox {
                    Toolbar().apply {
                        leftItems {
                            imageView(R.image.ic_launcher)
                            region { prefWidth = 12.0 }
                            label(BuildConfig.NAME) { styleClass.addAll("display2", "dark") }
                        }
                        rightItems {
                            roundButton(24.0, R.image.ic_refresh) {
                                tooltip("Reset")
                                onAction { outputPane.children.clear() }
                                runLater { disableProperty().bind(outputPane.children.isEmptyBinding) }
                            }
                            roundButton(24.0, R.image.ic_fullscreen) {
                                tooltip("Toggle scale")
                                graphicProperty().bind(
                                    Bindings.`when`(scale eq SCALE_SMALL)
                                        then ImageView(R.image.ic_fullscreen)
                                        otherwise ImageView((R.image.ic_fullscreen_exit))
                                )
                                onAction {
                                    scale.set(
                                        when (scale.value) {
                                            SCALE_SMALL -> SCALE_BIG
                                            else -> SCALE_SMALL
                                        }
                                    )
                                }
                            }
                            roundButton(24.0, R.image.ic_settings) {
                                tooltip("Settings")
                                contextMenu {
                                    menu("Language") {
                                        val group = ToggleGroup()
                                        radioMenuItem("English") { toggleGroup = group }
                                        radioMenuItem("Bahasa Indonesia") { toggleGroup = group }
                                    }
                                    separatorMenuItem()
                                    "About" { onAction { AboutDialog(this@stackPane).show() } }
                                }
                                onAction {
                                    if (!contextMenu.isShowing) {
                                        contextMenu.show(this@roundButton, Side.RIGHT, 0.0, 0.0)
                                    }
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

                            text("Calculate how many print sizes fit in a sheet size") {
                                wrappingWidth = 200.0
                            } row row++ col 0 colSpans 6

                            circle(radius = 4.0, fill = COLOR_YELLOW) row row col 0
                            label("Sheet size") row row col 1
                            sheetWidthField() row row col 2
                            label("x") row row col 3
                            sheetHeightField() row row col 4
                            morePaperButton(sheetWidthField, sheetHeightField) row row++ col 5

                            circle(radius = 4.0, fill = COLOR_RED) row row col 0
                            label("Print size") row row col 1
                            printWidthField() row row col 2
                            label("x") row row col 3
                            printHeightField() row row col 4
                            morePaperButton(printWidthField, printHeightField) row row++ col 5

                            label("Trim") row row col 1
                            trimField() row row++ col 2

                            row++
                            row++
                            sendButton = roundButton(24.0, R.image.ic_send) {
                                styleClass += "raised"
                                buttonType = JFXButton.ButtonType.RAISED
                                disableProperty().bind(buildBooleanBinding(
                                    sheetWidthField.textProperty(),
                                    sheetHeightField.textProperty(),
                                    printWidthField.textProperty(),
                                    printHeightField.textProperty()
                                ) {
                                    when {
                                        sheetWidthField.value <= 0.0 || sheetHeightField.value <= 0.0 -> true
                                        printWidthField.value <= 0.0 || printHeightField.value <= 0.0 -> true
                                        else -> false
                                    }
                                })
                                onAction {
                                    outputPane.children += ktfx.layouts.pane {
                                        gridPane {
                                            paddingAll = 10
                                            gap = 10
                                            val rectangles = Plano.getPrintRectangles(
                                                sheetWidthField.value,
                                                sheetHeightField.value,
                                                printWidthField.value,
                                                printHeightField.value,
                                                trimField.value
                                            )
                                            anchorPane {
                                                pane {
                                                    border(COLOR_YELLOW, 3)
                                                    prefWidthProperty().bind(sheetWidthField.value * scale)
                                                    prefHeightProperty().bind(sheetHeightField.value * scale)
                                                }

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
                                            textFlow {
                                                "1pcs " { styleClass += "bold" }
                                                "${sheetWidthField.text}x${sheetHeightField.text}"()
                                            } row 0 col 2
                                            circle(radius = 4.0, fill = COLOR_RED) row 1 col 1
                                            textFlow {
                                                "${rectangles.size}pcs " { styleClass += "bold" }
                                                "${printWidthField.text}x${printHeightField.text}"()
                                            } row 1 col 2
                                            moreButton {
                                                "Remove" { onAction { outputPane.children -= this@pane } }
                                                separatorMenuItem()
                                                "Save" {
                                                }
                                            } row 2 col 1 colSpans 2
                                        }
                                    }
                                }
                            } row row col 0 colSpans 6 halign HPos.RIGHT
                        }
                        anchorPane {
                            scrollPane {
                                isFitToWidth = true
                                hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                                outputPane = jfxMasonryPane {
                                    paddingAll = 10
                                    prefWidthProperty().bind(this@scrollPane.widthProperty() - 10)
                                }
                            } anchorAll 0
                            borderPane {
                                label("No content")
                                visibleProperty().bind(outputPane.children.isEmptyBinding)
                                managedProperty().bind(outputPane.children.isEmptyBinding)
                            } anchorAll 0
                        } hpriority Priority.ALWAYS
                    } vpriority Priority.ALWAYS
                }
            }
        }
        stage.show()

        sheetWidthField.requestFocus()
    }
}