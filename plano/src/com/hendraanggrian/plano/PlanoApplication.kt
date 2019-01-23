package com.hendraanggrian.plano

import com.hendraanggrian.plano.control.DoubleField
import com.hendraanggrian.plano.control.Toolbar
import com.hendraanggrian.plano.control.border
import com.hendraanggrian.plano.control.moreButton
import com.hendraanggrian.plano.control.morePaperButton
import com.hendraanggrian.plano.control.roundButton
import com.hendraanggrian.plano.dialog.AboutDialog
import com.hendraanggrian.plano.dialog.TextDialog
import com.hendraanggrian.plano.io.Preferences
import com.hendraanggrian.plano.io.ResultFile
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXMasonryPane
import javafx.application.Application
import javafx.application.Platform
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
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
import ktfx.coroutines.snapshot
import ktfx.double
import ktfx.jfoenix.jfxMasonryPane
import ktfx.jfoenix.jfxSnackbar
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
import ktfx.swing.toSwingImage
import ktfx.windows.setMinSize
import java.awt.Desktop
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.ResourceBundle
import javax.imageio.ImageIO

class PlanoApplication : Application(), Resources {

    companion object {

        const val DURATION_DEFAULT = 3000L

        const val SCALE_SMALL = 2.0
        const val SCALE_BIG = 4.0

        val COLOR_YELLOW: Color = Color.web("#ffb300")
        val COLOR_RED: Color = Color.web("#f08077")
        val COLOR_BORDER: Color = Color.web("#c8c8c8")

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

    lateinit var preferences: Preferences
    override lateinit var resources: ResourceBundle

    override fun init() {
        preferences = Preferences()
        resources = Language.ofFullCode(preferences.getString(Preferences.LANGUAGE)).toResourcesBundle()
    }

    override fun start(stage: Stage) {
        println(DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm").format(LocalDateTime.now()))
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
                                tooltip(getString(R.string.reset))
                                onAction { outputPane.children.clear() }
                                runLater { disableProperty().bind(outputPane.children.isEmptyBinding) }
                            }
                            roundButton(24.0, R.image.ic_fullscreen) {
                                tooltip(getString(R.string.toggle_scale))
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
                                tooltip(getString(R.string.settings))
                                contextMenu {
                                    menu(getString(R.string.language)) {
                                        val group = ToggleGroup()
                                        Language.values().forEach { language ->
                                            radioMenuItem(language.toLocale().displayLanguage) {
                                                toggleGroup = group
                                                isSelected = language.fullCode ==
                                                    preferences.getString(Preferences.LANGUAGE)
                                                onAction {
                                                    preferences[Preferences.LANGUAGE] = language.fullCode
                                                    preferences.save()
                                                    TextDialog(this@PlanoApplication, this@stackPane)
                                                        .apply { setOnDialogClosed { Platform.exit() } }
                                                        .show()
                                                }
                                            }
                                        }
                                    }
                                    separatorMenuItem()
                                    (getString(R.string.about)) {
                                        onAction {
                                            AboutDialog(this@PlanoApplication, this@stackPane).show()
                                        }
                                    }
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
                            border(COLOR_BORDER)
                            paddingAll = 20
                            gap = 10
                            var row = 0

                            text(getString(R.string._desc)) {
                                wrappingWidth = 200.0
                            } row row++ col 0 colSpans 6

                            circle(radius = 4.0, fill = COLOR_YELLOW) row row col 0
                            label(getString(R.string.sheet_size)) row row col 1
                            sheetWidthField.apply {
                                text = preferences.getString(Preferences.SHEET_WIDTH)
                            }() row row col 2
                            label("x") row row col 3
                            sheetHeightField.apply {
                                text = preferences.getString(Preferences.SHEET_HEIGHT)
                            }() row row col 4
                            morePaperButton(this@PlanoApplication, sheetWidthField, sheetHeightField) row row++ col 5

                            circle(radius = 4.0, fill = COLOR_RED) row row col 0
                            label(getString(R.string.print_size)) row row col 1
                            printWidthField.apply {
                                text = preferences.getString(Preferences.PRINT_WIDTH)
                            }() row row col 2
                            label("x") row row col 3
                            printHeightField.apply {
                                text = preferences.getString(Preferences.PRINT_HEIGHT)
                            }() row row col 4
                            morePaperButton(this@PlanoApplication, printWidthField, printHeightField) row row++ col 5

                            label(getString(R.string.trim)) row row col 1
                            trimField.apply {
                                text = preferences.getString(Preferences.TRIM)
                            }() row row++ col 2

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
                                    preferences[Preferences.SHEET_WIDTH] = sheetWidthField.value
                                    preferences[Preferences.SHEET_HEIGHT] = sheetHeightField.value
                                    preferences[Preferences.PRINT_WIDTH] = printWidthField.value
                                    preferences[Preferences.PRINT_HEIGHT] = printHeightField.value
                                    preferences[Preferences.TRIM] = trimField.value
                                    preferences.save()

                                    outputPane.children += ktfx.layouts.pane {
                                        gridPane {
                                            paddingAll = 10
                                            gap = 10
                                            val rectangles = Plano.getPrintSizes(
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
                                            lateinit var moreButton: Button
                                            moreButton = moreButton {
                                                (getString(R.string.save)) {
                                                    onAction {
                                                        moreButton.isVisible = false
                                                        val file = ResultFile()
                                                        @Suppress("LABEL_NAME_CLASH") this@gridPane.snapshot {
                                                            ImageIO.write(it.image.toSwingImage(), "png", file)
                                                        }
                                                        GlobalScope.launch(Dispatchers.JavaFx) {
                                                            delay(500)
                                                            moreButton.isVisible = true
                                                            this@vbox.jfxSnackbar(
                                                                getString(R.string._save_desc).format(file.name),
                                                                DURATION_DEFAULT,
                                                                getString(R.string.open)
                                                            ) {
                                                                Desktop.getDesktop().open(file)
                                                            }
                                                        }
                                                    }
                                                }
                                                separatorMenuItem()
                                                (getString(R.string.remove)) { onAction { outputPane.children -= this@pane } }
                                            } row 2 col 1 colSpans 2
                                        }
                                    }
                                    outputPane.clearLayout()
                                    outputPane.requestLayout()
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
                                label(getString(R.string.no_content))
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