package com.hendraanggrian.plano

import com.hendraanggrian.defaults.Defaults
import com.hendraanggrian.defaults.DefaultsDebugger
import com.hendraanggrian.defaults.PropertiesFileDefaults
import com.hendraanggrian.defaults.toDefaults
import com.hendraanggrian.plano.control.DoubleField
import com.hendraanggrian.plano.control.Toolbar
import com.hendraanggrian.plano.control.border
import com.hendraanggrian.plano.control.moreButton
import com.hendraanggrian.plano.control.morePaperButton
import com.hendraanggrian.plano.control.roundButton
import com.hendraanggrian.plano.dialog.AboutDialog
import com.hendraanggrian.plano.dialog.TextDialog
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
        fun main(args: Array<String>) = ktfx.launchApplication<PlanoApplication>(*args)
    }

    private val scale = double(SCALE_SMALL).apply {
        listener { _, _, newValue ->
            outputPane.children.forEach {
                val pane = it as Pane
                val gridPane = pane.children[0] as GridPane
                val anchorPane = gridPane.children[0] as AnchorPane
                anchorPane.children.forEachIndexed { index, node ->
                    when (index) {
                        0 -> {
                            node as Pane
                            pane.prefWidth = gridPane.prefWidth
                            pane.prefHeight = gridPane.prefHeight
                        }
                        else -> @Suppress("UNCHECKED_CAST") {
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

    private val mediaWidthField = DoubleField().apply { onAction { sendButton.fire() } }
    private val mediaHeightField = DoubleField().apply { onAction { sendButton.fire() } }
    private val trimWidthField = DoubleField().apply { onAction { sendButton.fire() } }
    private val trimHeightField = DoubleField().apply { onAction { sendButton.fire() } }
    private val bleedField = DoubleField().apply { onAction { sendButton.fire() } }

    private lateinit var refreshButton: Button
    private lateinit var fullscreenButton: Button
    private lateinit var settingsButton: Button
    private lateinit var rootPane: Pane
    private lateinit var sendButton: Button
    private lateinit var outputPane: JFXMasonryPane

    private lateinit var defaults: PropertiesFileDefaults
    override lateinit var resources: ResourceBundle

    override fun init() {
        if (BuildConfig.DEBUG) {
            Defaults.setDebugger(DefaultsDebugger.Default)
        }
        defaults = PreferencesFile().toDefaults()
        resources = Language
            .ofFullCode(defaults.getOrDefault(R2.preference.language, Language.EN_US.fullCode))
            .toResourcesBundle()
    }

    override fun start(stage: Stage) {
        stage.title = BuildConfig.NAME
        stage.setMinSize(750.0, 500.0)
        stage.scene = scene {
            stylesheets.addAll(
                PlanoApplication::class.java.getResource(R.style.plano).toExternalForm(),
                PlanoApplication::class.java.getResource(R.style.plano_font).toExternalForm()
            )
            rootPane = stackPane {
                vbox {
                    Toolbar().apply {
                        leftItems {
                            imageView(R.image.ic_launcher)
                            region { prefWidth = 12.0 }
                            label(BuildConfig.NAME) { styleClass.addAll("display2", "dark") }
                        }
                        rightItems {
                            refreshButton = roundButton(24.0, R.image.btn_refresh) {
                                tooltip(getString(R2.string.clear))
                                onAction {
                                    val children = outputPane.children.toList()
                                    outputPane.children.clear()
                                    rootPane.jfxSnackbar(
                                        getString(R2.string.boxes_cleared),
                                        DURATION_DEFAULT,
                                        getString(R2.string.undo)
                                    ) {
                                        outputPane.children += children
                                    }
                                    fullscreenButton.isDisable = true
                                    delay(DURATION_DEFAULT)
                                    fullscreenButton.isDisable = false
                                }
                                runLater { disableProperty().bind(outputPane.children.isEmptyBinding) }
                            }
                            fullscreenButton = roundButton(24.0, R.image.btn_fullscreen) {
                                tooltip(getString(R2.string.toggle_scale))
                                graphicProperty().bind(
                                    Bindings.`when`(scale eq SCALE_SMALL)
                                        then ImageView(R.image.btn_fullscreen)
                                        otherwise ImageView((R.image.btn_fullscreen_exit))
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
                            settingsButton = roundButton(24.0, R.image.btn_settings) {
                                tooltip(getString(R2.string.settings))
                                contextMenu {
                                    menu(getString(R2.string.language)) {
                                        val group = ToggleGroup()
                                        Language.values().forEach { language ->
                                            radioMenuItem(language.toLocale().displayLanguage) {
                                                toggleGroup = group
                                                isSelected =
                                                    language.fullCode == defaults.getOrDefault(
                                                        R2.preference.language,
                                                        Language.EN_US.fullCode
                                                    )
                                                onAction {
                                                    defaults {
                                                        this[R2.preference.language] =
                                                            language.fullCode
                                                    }
                                                    TextDialog(
                                                        this@PlanoApplication,
                                                        this@stackPane
                                                    ).apply { setOnDialogClosed { Platform.exit() } }
                                                        .show()
                                                }
                                            }
                                        }
                                    }
                                    (getString(R2.string.about)) {
                                        onAction {
                                            AboutDialog(
                                                this@PlanoApplication,
                                                this@stackPane
                                            ).show()
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

                            text(getString(R2.string._desc)) {
                                wrappingWidth = 200.0
                            } row row++ col 0 colSpans 6

                            circle(radius = 4.0, fill = COLOR_YELLOW) row row col 0
                            label(getString(R2.string.media_box)) row row col 1
                            mediaWidthField.apply {
                                text = defaults[R2.preference.media_width]
                            }() row row col 2
                            label("x") row row col 3
                            mediaHeightField.apply {
                                text = defaults[R2.preference.media_height]
                            }() row row col 4
                            morePaperButton(
                                this@PlanoApplication,
                                mediaWidthField,
                                mediaHeightField
                            ) row row++ col 5

                            circle(radius = 4.0, fill = COLOR_RED) row row col 0
                            label(getString(R2.string.trim_box)) row row col 1
                            trimWidthField.apply {
                                text = defaults[R2.preference.trim_width]
                            }() row row col 2
                            label("x") row row col 3
                            trimHeightField.apply {
                                text = defaults[R2.preference.trim_height]
                            }() row row col 4
                            morePaperButton(
                                this@PlanoApplication,
                                trimWidthField,
                                trimHeightField
                            ) row row++ col 5

                            label(getString(R2.string.bleed)) row row col 1
                            bleedField.apply {
                                text = defaults[R2.preference.bleed]
                            }() row row++ col 2

                            row++
                            row++
                            sendButton = roundButton(24.0, R.image.btn_send) {
                                styleClass += "raised"
                                buttonType = JFXButton.ButtonType.RAISED
                                disableProperty().bind(buildBooleanBinding(
                                    mediaWidthField.textProperty(),
                                    mediaHeightField.textProperty(),
                                    trimWidthField.textProperty(),
                                    trimHeightField.textProperty()
                                ) {
                                    when {
                                        mediaWidthField.value <= 0.0 || mediaHeightField.value <= 0.0 -> true
                                        trimWidthField.value <= 0.0 || trimHeightField.value <= 0.0 -> true
                                        else -> false
                                    }
                                })
                                onAction {
                                    defaults {
                                        this[R2.preference.media_width] =
                                            mediaWidthField.value.toString()
                                        this[R2.preference.media_height] =

                                            mediaHeightField.value.toString()
                                        this[R2.preference.trim_width] =
                                            trimWidthField.value.toString()
                                        this[R2.preference.trim_height] =
                                            trimHeightField.value.toString()
                                        if (bleedField.value > 0) {
                                            this[R2.preference.bleed] = bleedField.value.toString()
                                        }
                                    }

                                    outputPane.children += ktfx.layouts.pane {
                                        gridPane {
                                            paddingAll = 10
                                            gap = 10
                                            val printSizes = Plano.getTrimSizes(
                                                mediaWidthField.value,
                                                mediaHeightField.value,
                                                trimWidthField.value,
                                                trimHeightField.value,
                                                bleedField.value
                                            )
                                            anchorPane {
                                                pane {
                                                    border(COLOR_YELLOW, 3)
                                                    prefWidthProperty().bind(mediaWidthField.value * scale)
                                                    prefHeightProperty().bind(mediaHeightField.value * scale)
                                                }

                                                printSizes.forEach { size ->
                                                    pane {
                                                        prefWidthProperty().bind(size.width * scale)
                                                        prefHeightProperty().bind(size.height * scale)
                                                        border(COLOR_RED, 3)
                                                        userData = size.x to size.y
                                                    } anchorLeft size.x * scale.value anchorTop size.y * scale.value
                                                }
                                            } row 0 rowSpans 3 col 0
                                            circle(radius = 4.0, fill = COLOR_YELLOW) row 0 col 1
                                            textFlow {
                                                "${mediaWidthField.text}x${mediaHeightField.text}"()
                                            } row 0 col 2
                                            circle(radius = 4.0, fill = COLOR_RED) row 1 col 1
                                            textFlow {
                                                "${printSizes.size}pcs " { styleClass += "bold" }
                                                "${trimWidthField.text}x${trimHeightField.text}"()
                                            } row 1 col 2
                                            lateinit var moreButton: Button
                                            moreButton = moreButton {
                                                (getString(R2.string.save)) {
                                                    onAction {
                                                        moreButton.isVisible = false
                                                        val file = ResultFile()
                                                        @Suppress("LABEL_NAME_CLASH")
                                                        this@gridPane.snapshot {
                                                            ImageIO.write(
                                                                it.image.toSwingImage(),
                                                                "png",
                                                                file
                                                            )
                                                        }
                                                        GlobalScope.launch(Dispatchers.JavaFx) {
                                                            delay(500)
                                                            moreButton.isVisible = true
                                                            this@vbox.jfxSnackbar(
                                                                getString(R2.string._save_desc)
                                                                    .format(file.name),
                                                                DURATION_DEFAULT,
                                                                getString(R2.string.open)
                                                            ) {
                                                                Desktop.getDesktop().open(file)
                                                            }
                                                        }
                                                    }
                                                }
                                                separatorMenuItem()
                                                (getString(R2.string.remove)) {
                                                    onAction {
                                                        outputPane.children -= this@pane
                                                    }
                                                }
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
                                label(getString(R2.string.no_content))
                                visibleProperty().bind(outputPane.children.isEmptyBinding)
                                managedProperty().bind(outputPane.children.isEmptyBinding)
                            } anchorAll 0
                        } hpriority Priority.ALWAYS
                    } vpriority Priority.ALWAYS
                }
            }
        }
        stage.show()

        mediaWidthField.requestFocus()
    }
}