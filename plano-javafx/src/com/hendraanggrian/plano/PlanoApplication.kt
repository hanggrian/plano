package com.hendraanggrian.plano

import com.hendraanggrian.defaults.Defaults
import com.hendraanggrian.defaults.DefaultsDebugger
import com.hendraanggrian.defaults.PropertiesDefaults
import com.hendraanggrian.defaults.toDefaults
import com.hendraanggrian.plano.control.border
import com.hendraanggrian.plano.control.moreButton
import com.hendraanggrian.plano.control.morePaperButton
import com.hendraanggrian.plano.control.roundButton
import com.hendraanggrian.plano.control.sizeField
import com.hendraanggrian.plano.control.toolbar
import com.hendraanggrian.plano.dialog.AboutDialog
import com.hendraanggrian.plano.dialog.TextDialog
import com.jfoenix.controls.JFXButton
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
import javafx.scene.layout.FlowPane
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
import kotlinx.coroutines.withContext
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
import ktfx.doublePropertyOf
import ktfx.jfoenix.jfxSnackbar
import ktfx.layouts.anchorPane
import ktfx.layouts.borderPane
import ktfx.layouts.circle
import ktfx.layouts.contextMenu
import ktfx.layouts.flowPane
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
import org.apache.commons.lang3.SystemUtils
import java.awt.Desktop
import java.net.URI
import java.util.ResourceBundle
import javax.imageio.ImageIO

class PlanoApplication : Application(), Resources {

    companion object {
        const val DURATION_SHORT = 3000L
        const val DURATION_LONG = 6000L

        const val SCALE_SMALL = 2.0
        const val SCALE_BIG = 4.0

        val COLOR_YELLOW: Color = Color.web("#ffb300")
        val COLOR_RED: Color = Color.web("#f08077")
        val COLOR_BORDER: Color = Color.web("#c8c8c8")

        @JvmStatic
        fun main(args: Array<String>) = ktfx.launch<PlanoApplication>(*args)
    }

    private val scale = doublePropertyOf(SCALE_SMALL).apply {
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
        }
    }

    private val mediaWidthField = sizeField { onAction { sendButton.fire() } }
    private val mediaHeightField = sizeField { onAction { sendButton.fire() } }
    private val trimWidthField = sizeField { onAction { sendButton.fire() } }
    private val trimHeightField = sizeField { onAction { sendButton.fire() } }
    private val bleedField = sizeField { onAction { sendButton.fire() } }

    private lateinit var clearButton: Button
    private lateinit var fullscreenButton: Button
    private lateinit var settingsButton: Button
    private lateinit var rootPane: Pane
    private lateinit var sendButton: Button
    private lateinit var outputPane: FlowPane

    private lateinit var defaults: PropertiesDefaults
    override lateinit var resourceBundle: ResourceBundle

    override fun init() {
        Plano.DEBUG = BuildConfig.DEBUG
        if (BuildConfig.DEBUG) {
            Defaults.setDebugger(DefaultsDebugger.Default)
        }
        defaults = PreferencesFile().toDefaults()
        resourceBundle = Language
            .ofCode(defaults.getOrDefault(Preferences.LANGUAGE, Language.ENGLISH.code))
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
                    toolbar {
                        leftItems {
                            imageView(R.image.ic_launcher)
                            region { prefWidth = 12.0 }
                            label(BuildConfig.NAME) { styleClass.addAll("display2", "dark") }
                        }
                        rightItems {
                            clearButton = roundButton(24.0, R.image.btn_clear) {
                                tooltip(getString(R.string.clear))
                                onAction {
                                    val children = outputPane.children.toList()
                                    outputPane.children.clear()
                                    rootPane.jfxSnackbar(
                                        getString(R.string._boxes_cleared),
                                        DURATION_SHORT,
                                        getString(R.string.btn_undo)
                                    ) {
                                        outputPane.children += children
                                    }
                                    mediaWidthField.requestFocus()
                                    fullscreenButton.isDisable = true
                                    delay(DURATION_SHORT)
                                    fullscreenButton.isDisable = false
                                }
                                runLater { disableProperty().bind(outputPane.children.isEmptyBinding) }
                            }
                            fullscreenButton = roundButton(24.0, R.image.btn_fullscreen) {
                                tooltip(getString(R.string.toggle_scale))
                                graphicProperty().bind(
                                    Bindings.`when`(scale eq SCALE_SMALL)
                                        then ImageView(R.image.btn_fullscreen)
                                        otherwise ImageView((R.image.btn_fullscreen_exit))
                                )
                                onAction {
                                    scale.value = when (scale.value) {
                                        SCALE_SMALL -> SCALE_BIG
                                        else -> SCALE_SMALL
                                    }
                                }
                            }
                            settingsButton = roundButton(24.0, R.image.btn_settings) {
                                tooltip(getString(R.string.settings))
                                contextMenu {
                                    menu(getString(R.string.language)) {
                                        val group = ToggleGroup()
                                        Language.values().forEach { language ->
                                            radioMenuItem(language.toLocale().displayLanguage) {
                                                toggleGroup = group
                                                isSelected =
                                                    language.code == defaults.getOrDefault(
                                                        Preferences.LANGUAGE,
                                                        Language.ENGLISH.code
                                                    )
                                                onAction {
                                                    defaults {
                                                        this[Preferences.LANGUAGE] = language.code
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
                                    separatorMenuItem()
                                    (getString(R.string.check_for_update)) {
                                        onAction {
                                            val release = withContext(Dispatchers.IO) {
                                                GitHubApi.getLatestRelease()
                                            }
                                            when {
                                                release.isNewerThan(BuildConfig.VERSION) ->
                                                    rootPane.jfxSnackbar(
                                                        getString(R.string._update_available)
                                                            .format(BuildConfig.VERSION),
                                                        DURATION_LONG,
                                                        getString(R.string.btn_download)
                                                    ) {
                                                        Desktop.getDesktop()
                                                            .browse(URI(release.assets.first {
                                                                when {
                                                                    SystemUtils.IS_OS_MAC ->
                                                                        it.name.endsWith("dmg")
                                                                    SystemUtils.IS_OS_WINDOWS ->
                                                                        it.name.endsWith("exe")
                                                                    else -> it.name.endsWith("jar")
                                                                }
                                                            }.downloadUrl))
                                                    }
                                                else -> rootPane.jfxSnackbar(
                                                    getString(R.string._update_unavailable),
                                                    DURATION_LONG
                                                )
                                            }
                                        }
                                    }
                                    (getString(R.string.about)) {
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
                    }
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
                            label(getString(R.string.media_box)) row row col 1
                            mediaWidthField.apply {
                                text = defaults[Preferences.MEDIA_WIDTH]
                            }.add() row row col 2
                            label("x") row row col 3
                            mediaHeightField.apply {
                                text = defaults[Preferences.MEDIA_HEIGHT]
                            }.add() row row col 4
                            morePaperButton(
                                this@PlanoApplication,
                                mediaWidthField,
                                mediaHeightField
                            ) row row++ col 5

                            circle(radius = 4.0, fill = COLOR_RED) row row col 0
                            label(getString(R.string.trim_box)) row row col 1
                            trimWidthField.apply {
                                text = defaults[Preferences.TRIM_WIDTH]
                            }.add() row row col 2
                            label("x") row row col 3
                            trimHeightField.apply {
                                text = defaults[Preferences.TRIM_HEIGHT]
                            }.add() row row col 4
                            morePaperButton(
                                this@PlanoApplication,
                                trimWidthField,
                                trimHeightField
                            ) row row++ col 5

                            label(getString(R.string.bleed)) row row col 1
                            bleedField.apply {
                                text = defaults[Preferences.BLEED]
                            }.add() row row++ col 2

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
                                        this[Preferences.MEDIA_WIDTH] =
                                            mediaWidthField.value.toString()
                                        this[Preferences.MEDIA_HEIGHT] =
                                            mediaHeightField.value.toString()
                                        this[Preferences.TRIM_WIDTH] =
                                            trimWidthField.value.toString()
                                        this[Preferences.TRIM_HEIGHT] =
                                            trimHeightField.value.toString()
                                        when {
                                            bleedField.value > 0 -> this[Preferences.BLEED] =
                                                bleedField.value.toString()
                                            else -> this -= Preferences.BLEED
                                        }
                                    }

                                    outputPane.children.add(0, ktfx.layouts.pane {
                                        gridPane {
                                            paddingAll = 10
                                            gap = 10
                                            val size = Plano.calculate(
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

                                                size.trimSizes.forEach { size ->
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
                                                "${size.trimSizes.size}pcs " { styleClass += "bold" }
                                                "${trimWidthField.text}x${trimHeightField.text}"()
                                            } row 1 col 2
                                            lateinit var moreButton: Button
                                            moreButton = moreButton {
                                                getString(R.string.save)(ImageView(R.image.menu_save)) {
                                                    onAction {
                                                        moreButton.isVisible = false
                                                        val file = ResultFile()
                                                        @Suppress("LABEL_NAME_CLASH")
                                                        this@gridPane.snapshot {
                                                            ImageIO.write(
                                                                it.image.toSwingImage(), "png", file
                                                            )
                                                        }
                                                        GlobalScope.launch(Dispatchers.JavaFx) {
                                                            delay(500)
                                                            moreButton.isVisible = true
                                                            rootPane.jfxSnackbar(
                                                                getString(R.string._save_desc)
                                                                    .format(file.name),
                                                                DURATION_SHORT,
                                                                getString(R.string.btn_open)
                                                            ) {
                                                                Desktop.getDesktop().open(file)
                                                            }
                                                        }
                                                    }
                                                }
                                                getString(R.string.delete)(ImageView(R.image.menu_delete)) {
                                                    onAction {
                                                        outputPane.children -= this@pane
                                                    }
                                                }
                                            } row 2 col 1 colSpans 2
                                        }
                                    })
                                }
                            } row row col 0 colSpans 6 halign HPos.RIGHT
                        }
                        anchorPane {
                            scrollPane {
                                isFitToWidth = true
                                hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                                outputPane = flowPane {
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

        mediaWidthField.requestFocus()
    }
}