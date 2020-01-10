package com.hendraanggrian.plano

import com.hendraanggrian.plano.controls.DoubleField
import com.hendraanggrian.plano.controls.MediaPane
import com.hendraanggrian.plano.controls.MoreButton
import com.hendraanggrian.plano.controls.MorePaperButton
import com.hendraanggrian.plano.controls.PlanoToolbar
import com.hendraanggrian.plano.controls.SimpleRoundButton
import com.hendraanggrian.plano.controls.TrimPane
import com.hendraanggrian.plano.dialogs.AboutDialog
import com.hendraanggrian.plano.dialogs.TextDialog
import com.hendraanggrian.prefs.BindPref
import com.hendraanggrian.prefs.Prefs
import com.hendraanggrian.prefs.PrefsSaver
import com.hendraanggrian.prefs.jvm.setDebug
import com.hendraanggrian.prefs.jvm.userRoot
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXCheckBox
import java.awt.Desktop
import java.net.URI
import java.util.ResourceBundle
import javafx.application.Application
import javafx.geometry.HPos
import javafx.scene.control.Button
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.ScrollPane
import javafx.scene.control.ToggleGroup
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCombination
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.stage.Stage
import javax.imageio.ImageIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ktfx.booleanBindingOf
import ktfx.booleanPropertyOf
import ktfx.collections.emptyBinding
import ktfx.controls.gap
import ktfx.controls.paddingAll
import ktfx.controls.setMinSize
import ktfx.controlsfx.isOsMac
import ktfx.coroutines.onAction
import ktfx.doublePropertyOf
import ktfx.eq
import ktfx.inputs.plus
import ktfx.jfoenix.controls.jfxSnackbar
import ktfx.launchApplication
import ktfx.layouts.addChild
import ktfx.layouts.anchorPane
import ktfx.layouts.borderPane
import ktfx.layouts.checkMenuItem
import ktfx.layouts.circle
import ktfx.layouts.flowPane
import ktfx.layouts.gridPane
import ktfx.layouts.hbox
import ktfx.layouts.label
import ktfx.layouts.menu
import ktfx.layouts.menuBar
import ktfx.layouts.menuItem
import ktfx.layouts.radioMenuItem
import ktfx.layouts.rowConstraints
import ktfx.layouts.scene
import ktfx.layouts.scrollPane
import ktfx.layouts.separatorMenuItem
import ktfx.layouts.stackPane
import ktfx.layouts.tooltip
import ktfx.layouts.vbox
import ktfx.listeners.listener
import ktfx.listeners.onHiding
import ktfx.listeners.snapshot
import ktfx.minus
import ktfx.runLater
import ktfx.util.toSwingImage
import org.apache.commons.lang3.SystemUtils

class PlanoApp : Application(), Resources {

    companion object {
        const val DURATION_SHORT = 3000L
        const val DURATION_LONG = 6000L

        const val SCALE_SMALL = 2.0
        const val SCALE_BIG = 4.0

        // Material Orange 500
        val COLOR_YELLOW = Color.web("#ffb300")!!
        val COLOR_YELLOW_LIGHT = Color.web("#ffe54c")!!
        // Material Red 500
        val COLOR_RED = Color.web("#f44336")!!
        val COLOR_RED_LIGHT = Color.web("#ff7961")!!

        @JvmStatic fun main(args: Array<String>) = launchApplication<PlanoApp>(*args)

        fun getStyle(styleId: String): String = PlanoApp::class.java.getResource(styleId).toExternalForm()
    }

    private val scaleProperty = doublePropertyOf(SCALE_SMALL)
    private val expandedProperty = booleanPropertyOf().apply {
        bind(scaleProperty eq SCALE_BIG)
        listener { _, _, newValue ->
            expandedMenu.isSelected = newValue
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
                            AnchorPane.setLeftAnchor(node, x * scaleProperty.value)
                            AnchorPane.setTopAnchor(node, y * scaleProperty.value)
                        }
                    }
                }
            }
        }
    }
    private val filledProperty = booleanPropertyOf().apply {
        listener { _, _, newValue ->
            fillMenu.isSelected = newValue
        }
    }
    private val thickProperty = booleanPropertyOf().apply {
        listener { _, _, newValue ->
            thickMenu.isSelected = newValue
        }
    }

    private val mediaWidthField = DoubleField().apply { onAction { calculateButton.fire() } }
    private val mediaHeightField = DoubleField().apply { onAction { calculateButton.fire() } }
    private val trimWidthField = DoubleField().apply { onAction { calculateButton.fire() } }
    private val trimHeightField = DoubleField().apply { onAction { calculateButton.fire() } }
    private val bleedField = DoubleField().apply { onAction { calculateButton.fire() } }
    private val allowFlipCheck = JFXCheckBox()

    private lateinit var expandedMenu: CheckMenuItem
    private lateinit var fillMenu: CheckMenuItem
    private lateinit var thickMenu: CheckMenuItem
    private lateinit var rootPane: StackPane
    private lateinit var calculateButton: Button
    private lateinit var outputPane: FlowPane

    private lateinit var saver: PrefsSaver
    override lateinit var resourceBundle: ResourceBundle

    @JvmField @BindPref("dark_mode") var darkMode = false
    @JvmField @BindPref("language") var language = Language.ENGLISH.code
    @JvmField @BindPref("is_expanded") var isExpanded = false
    @JvmField @BindPref("is_filled") var isFilled = false
    @JvmField @BindPref("is_thick") var isThick = false
    @JvmField @BindPref("media_width") var mediaWidth = 0.0
    @JvmField @BindPref("media_height") var mediaHeight = 0.0
    @JvmField @BindPref("trim_width") var trimWidth = 0.0
    @JvmField @BindPref("trim_height") var trimHeight = 0.0
    @JvmField @BindPref("bleed") var bleed = 0.0
    @JvmField @BindPref("allow_flip") var allowFlip = false

    override fun init() {
        Plano.DEBUG = BuildConfig.DEBUG
        Prefs.setDebug(BuildConfig.DEBUG)
        saver = Prefs.userRoot().node(BuildConfig.GROUP.replace('.', '/')).bind(this)
        resourceBundle = Language.ofCode(language).toResourcesBundle()
    }

    override fun start(stage: Stage) {
        stage.title = BuildConfig.NAME
        stage.setMinSize(750.0, 500.0)
        stage.onHiding {
            isExpanded = expandedProperty.value
            isFilled = filledProperty.value
            isThick = thickProperty.value
            saver.save()
        }
        stage.scene(fill = Color.TRANSPARENT) {
            stylesheets.addAll(getStyle(R.style.plano), getStyle(R.style.plano_font))
            if (darkMode) {
                stylesheets += getStyle(R.style.plano_dark)
            }
            rootPane = stackPane {
                vbox {
                    menuBar {
                        isUseSystemMenuBar = isOsMac()
                        "File" {
                            checkMenuItem(getString(R.string.dark_mode)) {
                                isSelected = darkMode
                                onAction {
                                    darkMode = !darkMode
                                    saver.saveAsync()
                                    when {
                                        darkMode -> this@scene.stylesheets += getStyle(R.style.plano_dark)
                                        else -> this@scene.stylesheets -= getStyle(R.style.plano_dark)
                                    }
                                }
                            }
                            menu(getString(R.string.language)) {
                                val group = ToggleGroup()
                                Language.values().forEach { lang ->
                                    radioMenuItem(lang.toLocale().displayLanguage) {
                                        toggleGroup = group
                                        isSelected = lang.code == language
                                        onAction {
                                            language = lang.code
                                            TextDialog(
                                                this@PlanoApp,
                                                rootPane,
                                                R.string.please_restart,
                                                R.string._please_restart
                                            ).apply { setOnDialogClosed { stage.close() } }.show()
                                        }
                                    }
                                }
                            }
                            separatorMenuItem()
                            menuItem(getString(R.string.check_for_update)) {
                                onAction(Dispatchers.JavaFx) { checkForUpdate() }
                            }
                        }
                        "Edit" {
                            menuItem(getString(R.string.clear)) {
                                onAction { clear() }
                                runLater { disableProperty().bind(outputPane.children.emptyBinding) }
                            }
                        }
                        "View" {
                            expandedMenu = checkMenuItem(getString(R.string.expand)) {
                                accelerator = KeyCombination.SHORTCUT_DOWN + KeyCode.DIGIT1
                                onAction { toggleExpand() }
                            }
                            fillMenu = checkMenuItem(getString(R.string.fill_background)) {
                                accelerator = KeyCombination.SHORTCUT_DOWN + KeyCode.DIGIT2
                                onAction { toggleFill() }
                            }
                            thickMenu = checkMenuItem(getString(R.string.thicken_border)) {
                                accelerator = KeyCombination.SHORTCUT_DOWN + KeyCode.DIGIT3
                                onAction { toggleThick() }
                            }
                            separatorMenuItem()
                            menuItem(getString(R.string.reset)) {
                                onAction {
                                    scaleProperty.value = SCALE_SMALL
                                    filledProperty.value = false
                                    thickProperty.value = false
                                }
                            }
                        }
                        "Window" {
                            menuItem(getString(R.string.minimize)) {
                                accelerator = KeyCombination.SHORTCUT_DOWN + KeyCode.M
                                onAction { stage.isIconified = true }
                            }
                            menuItem(getString(R.string.zoom)) {
                                onAction { stage.isMaximized = true }
                            }
                        }
                        "Help" {
                            menuItem(getString(R.string.about)) {
                                onAction { AboutDialog(this@PlanoApp, rootPane).show() }
                            }
                        }
                    }
                    addChild(PlanoToolbar(this@PlanoApp, expandedProperty, filledProperty, thickProperty)) {
                        clearButton.onAction { clear() }
                        clearButton.runLater { disableProperty().bind(outputPane.children.emptyBinding) }
                        expandButton.onAction { toggleExpand() }
                        fillButton.onAction { toggleFill() }
                        thickButton.onAction { toggleThick() }
                    }
                    hbox {
                        gridPane {
                            paddingAll = 20.0
                            gap = 10.0
                            var row = 0

                            rowConstraints {
                                constraints() // description text
                                repeat(4) { constraints { prefHeight = 32.0 } } // input form
                            }

                            label(getString(R.string._desc)) {
                                isWrapText = true
                                maxWidth = 250.0
                            } row row++ col (0 to 6)

                            circle(radius = 4.0, fill = COLOR_YELLOW) {
                                tooltip(getString(R.string._media_box))
                            } row row col 0
                            label(getString(R.string.media_box)) {
                                tooltip(getString(R.string._media_box))
                            } row row col 1
                            addChild(mediaWidthField) {
                                tooltip(getString(R.string._media_box))
                                value = mediaWidth
                            } row row col 2
                            label("x") {
                                tooltip(getString(R.string._media_box))
                            } row row col 3
                            addChild(mediaHeightField) {
                                tooltip(getString(R.string._media_box))
                                value = mediaHeight
                            } row row col 4
                            addChild(MorePaperButton(this@PlanoApp, mediaWidthField, mediaHeightField)) row row++ col 5

                            circle(radius = 4.0, fill = COLOR_RED) {
                                tooltip(getString(R.string._trim_box))
                            } row row col 0
                            label(getString(R.string.trim_box)) {
                                tooltip(getString(R.string._trim_box))
                            } row row col 1
                            addChild(trimWidthField) {
                                tooltip(getString(R.string._trim_box))
                                value = trimWidth
                            } row row col 2
                            label("x") {
                                tooltip(getString(R.string._trim_box))
                            } row row col 3
                            addChild(trimHeightField) {
                                tooltip(getString(R.string._trim_box))
                                value = trimHeight
                            } row row col 4
                            addChild(MorePaperButton(this@PlanoApp, trimWidthField, trimHeightField)) row row++ col 5

                            label(getString(R.string.bleed)) {
                                tooltip(getString(R.string._bleed))
                            } row row col 1
                            addChild(bleedField) {
                                tooltip(getString(R.string._bleed))
                                value = bleed
                            } row row++ col 2

                            label(getString(R.string.allow_flip)) {
                                tooltip(getString(R.string._allow_flip))
                            } row row col 1
                            addChild(allowFlipCheck) {
                                tooltip(getString(R.string._allow_flip))
                                isSelected = allowFlip
                            } row row++ col 2

                            calculateButton = addChild(SimpleRoundButton(24, getString(R.string.calculate))) {
                                id = "btn-calculate"
                                buttonType = JFXButton.ButtonType.RAISED
                                disableProperty().bind(booleanBindingOf(
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
                                    mediaWidth = mediaWidthField.value
                                    mediaHeight = mediaHeightField.value
                                    trimWidth = trimWidthField.value
                                    trimHeight = trimHeightField.value
                                    bleed = bleedField.value
                                    allowFlip = allowFlipCheck.isSelected

                                    outputPane.children.add(0, ktfx.layouts.pane {
                                        gridPane {
                                            paddingAll = 10.0
                                            gap = 10.0
                                            val size = Plano.calculate(
                                                mediaWidth, mediaHeight,
                                                trimWidth, trimHeight,
                                                bleed, allowFlip
                                            )
                                            anchorPane {
                                                addChild(MediaPane(size, scaleProperty, filledProperty, thickProperty))
                                                size.trimSizes.forEach {
                                                    addChild(TrimPane(it, scaleProperty, filledProperty, thickProperty))
                                                }
                                            } row (0 to 3) col 0
                                            circle(radius = 4.0, fill = COLOR_YELLOW) row 0 col 1
                                            label("${mediaWidth}x$mediaHeight") row 0 col 2
                                            circle(radius = 4.0, fill = COLOR_RED) row 1 col 1
                                            label("${size.trimSizes.size}pcs ${trimWidth + bleed * 2}x${trimHeight + bleed * 2}") row 1 col 2
                                            lateinit var moreButton: Button
                                            moreButton = addChild(
                                                MoreButton(this@PlanoApp) {
                                                    menuItem(getString(R.string.delete)) {
                                                        onAction { outputPane.children -= this@pane }
                                                    }
                                                    menuItem(getString(R.string.save)) {
                                                        onAction {
                                                            moreButton.isVisible = false
                                                            val file = ResultFile()
                                                            @Suppress("LABEL_NAME_CLASH")
                                                            this@gridPane.snapshot {
                                                                ImageIO.write(it.image.toSwingImage(), "png", file)
                                                            }
                                                            GlobalScope.launch(Dispatchers.JavaFx) {
                                                                delay(500)
                                                                moreButton.isVisible = true
                                                                rootPane.jfxSnackbar(
                                                                    getString(R.string._save)
                                                                        .format(file.name),
                                                                    DURATION_SHORT,
                                                                    getString(R.string.btn_show_file)
                                                                ) {
                                                                    Desktop.getDesktop()
                                                                        .open(file.parentFile)
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            ) row 2 col (1 to 2)
                                        }
                                    })
                                }
                            } row row col (0 to 6) halign HPos.RIGHT
                        }
                        anchorPane {
                            scrollPane {
                                isFitToWidth = true
                                hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                                outputPane = flowPane {
                                    paddingAll = 10.0
                                    prefWidthProperty().bind(this@scrollPane.widthProperty() - 10)
                                }
                            } anchorAll 0.0
                            borderPane {
                                label(getString(R.string.no_content))
                                visibleProperty().bind(outputPane.children.emptyBinding)
                                managedProperty().bind(outputPane.children.emptyBinding)
                            } anchorAll 0.0
                        } hgrow true
                    } vgrow true
                }
            }
        }
        stage.show()

        if (isExpanded) toggleExpand()
        if (isFilled) toggleFill()
        if (isThick) toggleThick()
        mediaWidthField.requestFocus()
    }

    private fun clear() {
        val children = outputPane.children.toList()
        outputPane.children.clear()
        rootPane.jfxSnackbar(getString(R.string._boxes_cleared), DURATION_SHORT, getString(R.string.btn_undo)) {
            outputPane.children += children
        }
        mediaWidthField.requestFocus()
    }

    private fun toggleExpand() {
        scaleProperty.value = when (scaleProperty.value) {
            SCALE_SMALL -> SCALE_BIG
            else -> SCALE_SMALL
        }
    }

    private fun toggleFill() {
        filledProperty.value = !filledProperty.value
    }

    private fun toggleThick() {
        thickProperty.value = !thickProperty.value
    }

    private suspend fun checkForUpdate() {
        val release = withContext(Dispatchers.IO) { GitHubApi.getRelease(".jar") }
        when {
            release.isNewerThan(BuildConfig.VERSION) -> rootPane.jfxSnackbar(
                getString(R.string._update_available).format(BuildConfig.VERSION),
                DURATION_LONG,
                getString(R.string.btn_download)
            ) {
                Desktop.getDesktop().browse(URI(release.assets.first {
                    when {
                        SystemUtils.IS_OS_MAC -> it.name.endsWith("dmg")
                        SystemUtils.IS_OS_WINDOWS -> it.name.endsWith("exe")
                        else -> it.name.endsWith("jar")
                    }
                }.downloadUrl))
            }
            else -> rootPane.jfxSnackbar(getString(R.string._update_unavailable), DURATION_LONG)
        }
    }
}
