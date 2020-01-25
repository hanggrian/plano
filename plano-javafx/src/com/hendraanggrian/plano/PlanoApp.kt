package com.hendraanggrian.plano

import com.hendraanggrian.plano.controls.DoubleField
import com.hendraanggrian.plano.controls.PlanoToolbar
import com.hendraanggrian.plano.controls.ResultPane
import com.hendraanggrian.plano.controls.RoundButton
import com.hendraanggrian.plano.controls.RoundMenuPaperButton
import com.hendraanggrian.plano.dialogs.AboutDialog
import com.hendraanggrian.plano.dialogs.TextDialog
import com.hendraanggrian.plano.util.THEME_DARK
import com.hendraanggrian.plano.util.THEME_LIGHT
import com.hendraanggrian.plano.util.THEME_SYSTEM
import com.hendraanggrian.plano.util.getResource
import com.hendraanggrian.plano.util.isDarkTheme
import com.hendraanggrian.prefs.BindPref
import com.hendraanggrian.prefs.Prefs
import com.hendraanggrian.prefs.PrefsSaver
import com.hendraanggrian.prefs.jvm.setDebug
import com.hendraanggrian.prefs.jvm.userRoot
import com.jfoenix.controls.JFXCheckBox
import java.util.ResourceBundle
import javafx.application.Application
import javafx.geometry.HPos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.ScrollPane
import javafx.scene.control.ToggleGroup
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCombination
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
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
import ktfx.minus
import ktfx.runLater

class PlanoApp : Application(), Resources {

    companion object {
        const val DURATION_SHORT = 3000L
        const val DURATION_LONG = 6000L
        const val SCALE_SMALL = 2.0
        const val SCALE_BIG = 4.0

        @JvmStatic fun main(args: Array<String>) = launchApplication<PlanoApp>(*args)
    }

    private val scaleProperty = doublePropertyOf(SCALE_SMALL)
    private val expandProperty = booleanPropertyOf().apply {
        bind(scaleProperty eq SCALE_BIG)
        listener { _, _, newValue ->
            expandMenu.isSelected = newValue
            outputPane.children.forEach {
                val pane = it as Pane
                val resultPane = pane.children.first() as ResultPane
                val anchorPane = resultPane.children.last() as AnchorPane
                anchorPane.children.forEachIndexed { index, node ->
                    when (index) {
                        0 -> {
                            pane.prefWidth = resultPane.prefWidth
                            pane.prefHeight = resultPane.prefHeight
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
    private val fillProperty = booleanPropertyOf().apply {
        listener { _, _, newValue -> fillMenu.isSelected = newValue }
    }
    private val thickProperty = booleanPropertyOf().apply {
        listener { _, _, newValue -> thickMenu.isSelected = newValue }
    }

    val mediaWidthField = DoubleField().apply { onAction { calculateButton.fire() } }
    private val mediaHeightField = DoubleField().apply { onAction { calculateButton.fire() } }
    private val trimWidthField = DoubleField().apply { onAction { calculateButton.fire() } }
    private val trimHeightField = DoubleField().apply { onAction { calculateButton.fire() } }
    private val bleedField = DoubleField().apply { onAction { calculateButton.fire() } }
    private val allowFlipCheck = JFXCheckBox()

    private lateinit var expandMenu: CheckMenuItem
    private lateinit var fillMenu: CheckMenuItem
    private lateinit var thickMenu: CheckMenuItem
    private lateinit var calculateButton: Button
    lateinit var rootPane: StackPane
    lateinit var outputPane: FlowPane

    private lateinit var saver: PrefsSaver
    override lateinit var resourceBundle: ResourceBundle

    @JvmField @BindPref("theme") var theme = THEME_SYSTEM
    @JvmField @BindPref("language") var language = Language.ENGLISH.code
    @JvmField @BindPref("is_expand") var isExpand = false
    @JvmField @BindPref("is_fill") var isFill = false
    @JvmField @BindPref("is_thick") var isThick = false
    @JvmField @BindPref("media_width") var mediaWidth = 0.0
    @JvmField @BindPref("media_height") var mediaHeight = 0.0
    @JvmField @BindPref("trim_width") var trimWidth = 0.0
    @JvmField @BindPref("trim_height") var trimHeight = 0.0
    @JvmField @BindPref("bleed") var bleed = 0.0
    @JvmField @BindPref("allow_flip") var allowFlip = false

    override fun init() {
        MediaBox.DEBUG = BuildConfig.DEBUG
        Prefs.setDebug(BuildConfig.DEBUG)
        saver = Prefs.userRoot().node(BuildConfig.GROUP.replace('.', '/')).bind(this)
        resourceBundle = Language.ofCode(language).toResourcesBundle()
    }

    override fun start(stage: Stage) {
        stage.title = BuildConfig.NAME
        stage.setMinSize(750.0, 500.0)
        stage.onHiding {
            isExpand = expandProperty.value
            isFill = fillProperty.value
            isThick = thickProperty.value
            saver.save()
        }
        stage.scene {
            stylesheets.addAll(getResource(R.style._plano), getResource(R.style._plano_font))
            if (isDarkTheme(theme)) stylesheets += getResource(R.style._plano_dark)
            rootPane = stackPane {
                vbox {
                    menuBar {
                        isUseSystemMenuBar = isOsMac()
                        "File" {
                            menu(getString(R.string.theme)) {
                                val themeGroup = ToggleGroup()
                                radioMenuItem(getString(R.string.system_default)) {
                                    toggleGroup = themeGroup
                                    isSelected = theme == THEME_SYSTEM
                                    onAction { setTheme(this@scene, THEME_SYSTEM) }
                                }
                                separatorMenuItem()
                                radioMenuItem(getString(R.string.light)) {
                                    toggleGroup = themeGroup
                                    isSelected = theme == THEME_LIGHT
                                    onAction { setTheme(this@scene, THEME_LIGHT) }
                                }
                                radioMenuItem(getString(R.string.dark)) {
                                    toggleGroup = themeGroup
                                    isSelected = theme == THEME_DARK
                                    onAction { setTheme(this@scene, THEME_DARK) }
                                }
                            }
                            menu(getString(R.string.language)) {
                                val group = ToggleGroup()
                                Language.values().forEachIndexed { index, lang ->
                                    if (index == 1) separatorMenuItem()
                                    radioMenuItem(lang.toLocale().displayLanguage) {
                                        toggleGroup = group
                                        isSelected = lang.code == language
                                        onAction {
                                            language = lang.code
                                            TextDialog(
                                                this@PlanoApp,
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
                            menuItem(getString(R.string.close_all)) {
                                onAction { closeAll() }
                                runLater { disableProperty().bind(outputPane.children.emptyBinding) }
                            }
                        }
                        "View" {
                            expandMenu = checkMenuItem(getString(R.string.expand)) {
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
                                    fillProperty.value = false
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
                                onAction { AboutDialog(this@PlanoApp).show() }
                            }
                        }
                    }
                    addChild(PlanoToolbar(this@PlanoApp, expandProperty, fillProperty, thickProperty)) {
                        closeAllButton.onAction { closeAll() }
                        closeAllButton.runLater { disableProperty().bind(outputPane.children.emptyBinding) }
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

                            circle(radius = 6.0) {
                                tooltip(getString(R.string._media_box))
                                id = R.style.circle_amber
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
                            addChild(
                                RoundMenuPaperButton(this@PlanoApp, mediaWidthField, mediaHeightField)
                            ) row row++ col 5

                            circle(radius = 6.0) {
                                tooltip(getString(R.string._trim_box))
                                id = R.style.circle_red
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
                            addChild(
                                RoundMenuPaperButton(this@PlanoApp, trimWidthField, trimHeightField)
                            ) row row++ col 5

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

                            calculateButton = addChild(RoundButton(24, getString(R.string.calculate))) {
                                id = R.style.btn_calculate
                                disableProperty().bind(booleanBindingOf(
                                    mediaWidthField.textProperty(), mediaHeightField.textProperty(),
                                    trimWidthField.textProperty(), trimHeightField.textProperty()
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
                                        addChild(
                                            ResultPane(
                                                this@PlanoApp,
                                                mediaWidth, mediaHeight,
                                                trimWidth, trimHeight,
                                                bleed, allowFlip,
                                                scaleProperty, fillProperty, thickProperty
                                            )
                                        )
                                    })
                                }
                            } row row col (0 to 6) halign HPos.RIGHT

                            // avoid left pane being pushed out when right pane has a lot of contents
                            runLater { minWidth = width }
                        }
                        anchorPane {
                            scrollPane {
                                isFitToWidth = true
                                hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                                outputPane = flowPane {
                                    paddingAll = 10.0
                                    // minus vertical scrollbar width
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

        if (isExpand) toggleExpand()
        if (isFill) toggleFill()
        if (isThick) toggleThick()
        mediaWidthField.requestFocus()
    }

    fun closeAll() {
        val children = outputPane.children.toList()
        outputPane.children.clear()
        rootPane.jfxSnackbar(getString(R.string._boxes_cleared), DURATION_SHORT, getString(R.string.btn_undo)) {
            outputPane.children += children
        }
        mediaWidthField.requestFocus()
    }

    private fun setTheme(scene: Scene, theme: String) {
        this@PlanoApp.theme = theme
        saver.saveAsync()
        val darkTheme = getResource(R.style._plano_dark)
        when {
            isDarkTheme(theme) -> if (darkTheme !in scene.stylesheets) scene.stylesheets += darkTheme
            else -> scene.stylesheets -= darkTheme
        }
    }

    private fun toggleExpand() {
        scaleProperty.value = when (scaleProperty.value) {
            SCALE_SMALL -> SCALE_BIG
            else -> SCALE_SMALL
        }
    }

    private fun toggleFill() {
        fillProperty.value = !fillProperty.value
    }

    private fun toggleThick() {
        thickProperty.value = !thickProperty.value
    }

    private suspend fun checkForUpdate() {
        val release = withContext(Dispatchers.IO) { GitHubApi.getRelease("jar") }
        when {
            release.isNewerThan(BuildConfig.VERSION) -> rootPane.jfxSnackbar(
                getString(R.string._update_available).format(BuildConfig.VERSION),
                DURATION_LONG,
                getString(R.string.btn_download)
            ) { hostServices.showDocument(release.htmlUrl) }
            else -> rootPane.jfxSnackbar(getString(R.string._update_unavailable), DURATION_LONG)
        }
    }
}
