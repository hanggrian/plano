package com.hendraanggrian.plano

import com.hendraanggrian.auto.prefs.BindPreference
import com.hendraanggrian.auto.prefs.PreferencesLogger
import com.hendraanggrian.auto.prefs.PreferencesSaver
import com.hendraanggrian.auto.prefs.Prefs
import com.hendraanggrian.auto.prefs.jvm.userRoot
import com.hendraanggrian.plano.controls.FloatField
import com.hendraanggrian.plano.controls.PlanoToolbar
import com.hendraanggrian.plano.controls.ResultPane
import com.hendraanggrian.plano.controls.RoundButton
import com.hendraanggrian.plano.controls.RoundMorePaperButton
import com.hendraanggrian.plano.data.RecentMediaSize
import com.hendraanggrian.plano.data.RecentMediaSizes
import com.hendraanggrian.plano.data.RecentTrimSize
import com.hendraanggrian.plano.data.RecentTrimSizes
import com.hendraanggrian.plano.data.saveRecentSizes
import com.hendraanggrian.plano.dialogs.TextDialog
import com.hendraanggrian.plano.help.AboutDialog
import com.hendraanggrian.plano.help.LicensesDialog
import com.hendraanggrian.plano.util.THEME_DARK
import com.hendraanggrian.plano.util.THEME_LIGHT
import com.hendraanggrian.plano.util.THEME_SYSTEM
import com.hendraanggrian.plano.util.getResource
import com.hendraanggrian.plano.util.isDarkTheme
import com.jfoenix.controls.JFXCheckBox
import javafx.application.Application
import javafx.application.Platform
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
import ktfx.bindings.asAny
import ktfx.bindings.booleanBindingOf
import ktfx.bindings.eq
import ktfx.bindings.isEmpty
import ktfx.bindings.minus
import ktfx.booleanPropertyOf
import ktfx.controls.H_RIGHT
import ktfx.controls.insetsOf
import ktfx.controls.rowConstraints
import ktfx.coroutines.onAction
import ktfx.doublePropertyOf
import ktfx.inputs.plus
import ktfx.jfoenix.controls.jfxSnackbar
import ktfx.jfoenix.layouts.jfxToggleNode
import ktfx.launchApplication
import ktfx.layouts.anchorPane
import ktfx.layouts.borderPane
import ktfx.layouts.checkMenuItem
import ktfx.layouts.flowPane
import ktfx.layouts.gridPane
import ktfx.layouts.hbox
import ktfx.layouts.label
import ktfx.layouts.menu
import ktfx.layouts.menuBar
import ktfx.layouts.menuItem
import ktfx.layouts.radioMenuItem
import ktfx.layouts.scene
import ktfx.layouts.scrollPane
import ktfx.layouts.separatorMenuItem
import ktfx.layouts.stackPane
import ktfx.layouts.styledCircle
import ktfx.layouts.tooltip
import ktfx.layouts.vbox
import ktfx.listeners.listener
import ktfx.runLater
import ktfx.time.seconds
import ktfx.windows.minSize
import org.apache.commons.lang3.SystemUtils
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.ResourceBundle

class PlanoApp : Application(), Resources {

    companion object {
        val DURATION_SHORT = 3.seconds
        val DURATION_LONG = 6.seconds
        const val SCALE_SMALL = 2.0
        const val SCALE_BIG = 4.0

        @JvmStatic
        fun main(args: Array<String>) = launchApplication<PlanoApp>(*args)
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

    private val mediaWidthField = FloatField().apply { onAction { calculateButton.fire() } }
    private val mediaHeightField = FloatField().apply { onAction { calculateButton.fire() } }
    private val trimWidthField = FloatField().apply { onAction { calculateButton.fire() } }
    private val trimHeightField = FloatField().apply { onAction { calculateButton.fire() } }
    private val gapHorizontalField = FloatField().apply { onAction { calculateButton.fire() } }
    private val gapVerticalField = FloatField().apply { onAction { calculateButton.fire() } }
    private val gapLinkToggle = jfxToggleNode {
        idProperty().bind(
            this@jfxToggleNode.selectedProperty().asAny { if (it) R.style.menu_link_on else R.style.menu_link_off }
        )
        selectedProperty().listener {
            when {
                isSelected -> gapHorizontalField.textProperty().bindBidirectional(gapVerticalField.textProperty())
                else -> gapHorizontalField.textProperty().unbindBidirectional(gapVerticalField.textProperty())
            }
        }
    }
    private val allowFlipRightCheck = JFXCheckBox()
    private val allowFlipBottomCheck = JFXCheckBox()

    private lateinit var expandMenu: CheckMenuItem
    private lateinit var fillMenu: CheckMenuItem
    private lateinit var thickMenu: CheckMenuItem
    private lateinit var calculateButton: Button
    lateinit var rootPane: StackPane
    lateinit var outputPane: FlowPane

    private lateinit var saver: PreferencesSaver
    override lateinit var resourceBundle: ResourceBundle

    @JvmField @BindPreference("language") var language = Language.ENGLISH.code
    @JvmField @BindPreference("theme") var theme = THEME_SYSTEM
    @JvmField @BindPreference("is_expand") var isExpand = false
    @JvmField @BindPreference("is_fill") var isFill = false
    @JvmField @BindPreference("is_thick") var isThick = false
    @JvmField @BindPreference("media_width") var mediaWidth = 0f
    @JvmField @BindPreference("media_height") var mediaHeight = 0f
    @JvmField @BindPreference("trim_width") var trimWidth = 0f
    @JvmField @BindPreference("trim_height") var trimHeight = 0f
    @JvmField @BindPreference("gap_horizontal") var gapHorizontal = 0f
    @JvmField @BindPreference("gap_vertical") var gapVertical = 0f
    @JvmField @BindPreference("gap_link") var gapLink = false
    @JvmField @BindPreference("allow_flip_column") var allowFlipColumn = false
    @JvmField @BindPreference("allow_flip_row") var allowFlipRow = false

    override fun init() {
        Plano.setDebug(BuildConfig.DEBUG)
        if (BuildConfig.DEBUG) Prefs.setLogger(PreferencesLogger.System)

        saver = Prefs.bind(Prefs.userRoot(BuildConfig.GROUP.replace('.', '/')), this)
        resourceBundle = Language.ofCode(language).toResourcesBundle()

        Database.connect("jdbc:sqlite:/${SystemUtils.USER_HOME}/.plano.db", "org.sqlite.JDBC")
        transaction { SchemaUtils.create(RecentMediaSizes, RecentTrimSizes) }
    }

    override fun start(stage: Stage) {
        stage.title = BuildConfig.NAME
        stage.minSize = 750 to 500
        stage.scene {
            stylesheets.addAll(getResource(R.style._plano), getResource(R.style._plano_font))
            if (isDarkTheme(theme)) stylesheets += getResource(R.style._plano_dark)
            rootPane = stackPane {
                vbox {
                    menuBar {
                        isUseSystemMenuBar = SystemUtils.IS_OS_MAC_OSX
                        if (!isUseSystemMenuBar) {
                            "File" {
                                menuItem(getString(R.string.quit)) {
                                    accelerator = KeyCombination.SHORTCUT_DOWN + KeyCode.Q
                                    onAction { Platform.exit() }
                                }
                            }
                        }
                        "Edit" {
                            menuItem(getString(R.string.close_all)) {
                                onAction { closeAll() }
                                runLater { disableProperty().bind(outputPane.children.isEmpty) }
                            }
                            menuItem(getString(R.string.clear_recent_sizes)) {
                                onAction {
                                    transaction {
                                        RecentMediaSizes.deleteAll()
                                        RecentTrimSizes.deleteAll()
                                    }
                                }
                            }
                            separatorMenuItem()
                            menu(getString(R.string.preferences)) {
                                menu(getString(R.string.theme)) {
                                    val themeGroup = ToggleGroup()
                                    radioMenuItem(getString(R.string.system_default)) {
                                        toggleGroup = themeGroup
                                        isSelected = theme == THEME_SYSTEM
                                        onAction { setTheme(this@scene, THEME_SYSTEM) }
                                    }
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
                                    Language.values().forEach { lang ->
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
                            }
                        }
                        "View" {
                            expandMenu = checkMenuItem(getString(R.string.toggle_expand)) {
                                accelerator = KeyCombination.SHORTCUT_DOWN + KeyCode.DIGIT1
                                onAction { toggleExpand() }
                            }
                            fillMenu = checkMenuItem(getString(R.string.toggle_background)) {
                                accelerator = KeyCombination.SHORTCUT_DOWN + KeyCode.DIGIT2
                                onAction { toggleFill() }
                            }
                            thickMenu = checkMenuItem(getString(R.string.toggle_border)) {
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
                            menuItem(getString(R.string.check_for_update)) {
                                onAction(Dispatchers.JavaFx) { checkForUpdate() }
                            }
                            menuItem(getString(R.string.view_on_github)) {
                                onAction { hostServices.showDocument(BuildConfig.WEB) }
                            }
                            menuItem(getString(R.string.open_source_licenses)) {
                                onAction { LicensesDialog(this@PlanoApp).show() }
                            }
                            separatorMenuItem()
                            menuItem(getString(R.string.about)) {
                                onAction { AboutDialog(this@PlanoApp).show() }
                            }
                        }
                    }
                    addChild(
                        PlanoToolbar(this@PlanoApp, expandProperty, fillProperty, thickProperty).apply {
                            closeAllButton.onAction { closeAll() }
                            closeAllButton.runLater { disableProperty().bind(outputPane.children.isEmpty) }
                            expandButton.onAction { toggleExpand() }
                            fillButton.onAction { toggleFill() }
                            thickButton.onAction { toggleThick() }
                        }
                    )
                    hbox {
                        gridPane {
                            padding = insetsOf(20)
                            hgap = 10.0
                            vgap = 10.0
                            var row = 0

                            rowConstraints {
                                append() // description text
                                repeat(4) { append { prefHeight = 32.0 } } // input form
                            }

                            label(getString(R.string._desc)) {
                                isWrapText = true
                                maxWidth = 250.0
                            }.grid(row++, 0 to 7)

                            styledCircle(radius = 6.0, id = R.style.circle_amber) {
                                tooltip(getString(R.string._media_size))
                            }.grid(row, 0)
                            label(getString(R.string.media_size)) { tooltip(getString(R.string._media_size)) }
                                .grid(row, 1 to 2)
                            addChild(
                                mediaWidthField.apply {
                                    tooltip(getString(R.string._media_size))
                                    value = mediaWidth
                                }
                            ).grid(row, 3)
                            label("×") { tooltip(getString(R.string._media_size)) }.grid(row, 4)
                            addChild(
                                mediaHeightField.apply {
                                    tooltip(getString(R.string._media_size))
                                    value = mediaHeight
                                }
                            ).grid(row, 5)
                            addChild(
                                RoundMorePaperButton(this@PlanoApp, mediaWidthField, mediaHeightField) {
                                    RecentMediaSize.all()
                                }
                            ).grid(row++, 6)

                            styledCircle(radius = 6.0, id = R.style.circle_red) {
                                tooltip(getString(R.string._trim_size))
                            }.grid(row, 0)
                            label(getString(R.string.trim_size)) { tooltip(getString(R.string._trim_size)) }
                                .grid(row, 1 to 2)
                            addChild(
                                trimWidthField.apply {
                                    tooltip(getString(R.string._trim_size))
                                    value = trimWidth
                                }
                            ).grid(row, 3)
                            label("×") { tooltip(getString(R.string._trim_size)) }.grid(row, 4)
                            addChild(
                                trimHeightField.apply {
                                    tooltip(getString(R.string._trim_size))
                                    value = trimHeight
                                }
                            ).grid(row, 5)
                            addChild(
                                RoundMorePaperButton(this@PlanoApp, trimWidthField, trimHeightField) {
                                    RecentTrimSize.all()
                                }
                            ).grid(row++, 6)

                            label(getString(R.string.gap)) { tooltip(getString(R.string._gap)) }.grid(row, 1)
                            label("↔︎︎") { tooltip(getString(R.string._gap)) }.grid(row, 2).halign(H_RIGHT)
                            addChild(
                                gapHorizontalField.apply {
                                    tooltip(getString(R.string._gap))
                                    value = gapHorizontal
                                }
                            ).grid(row, 3)
                            label("↕︎") { tooltip(getString(R.string._gap)) }.grid(row, 4)
                            addChild(
                                gapVerticalField.apply {
                                    tooltip(getString(R.string._gap))
                                    value = gapVertical
                                }
                            ).grid(row, 5)
                            addChild(
                                gapLinkToggle.apply {
                                    tooltip(getString(R.string._gap))
                                    isSelected = gapLink
                                }
                            ).grid(row++, 6)

                            label(getString(R.string.allow_flip)) { tooltip(getString(R.string._allow_flip)) }
                                .grid(row, 1 to 2)
                            addChild(
                                allowFlipRightCheck.apply {
                                    text = getString(R.string.right)
                                    tooltip(getString(R.string._allow_flip))
                                    isSelected = allowFlipColumn
                                }
                            ).grid(row++, 3 to 4)
                            addChild(
                                allowFlipBottomCheck.apply {
                                    text = getString(R.string.bottom)
                                    tooltip(getString(R.string._allow_flip))
                                    isSelected = allowFlipRow
                                }
                            ).grid(row++, 3 to 4)

                            calculateButton = addChild(
                                RoundButton(this@PlanoApp, RoundButton.RADIUS_LARGE, R.string.calculate).apply {
                                    id = R.style.btn_calculate
                                    disableProperty().bind(
                                        booleanBindingOf(
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
                                        }
                                    )
                                    onAction {
                                        stop()
                                        outputPane.children.add(
                                            0,
                                            ktfx.layouts.pane {
                                                addChild(
                                                    ResultPane(
                                                        this@PlanoApp,
                                                        mediaWidthField.value, mediaHeightField.value,
                                                        trimWidthField.value, trimHeightField.value,
                                                        gapHorizontalField.value, gapVerticalField.value,
                                                        allowFlipRightCheck.isSelected, allowFlipBottomCheck.isSelected,
                                                        scaleProperty, fillProperty, thickProperty
                                                    )
                                                )
                                            }
                                        )
                                        saveRecentSizes(
                                            mediaWidthField.value, mediaHeightField.value,
                                            trimWidthField.value, trimHeightField.value
                                        )
                                    }
                                }
                            ).grid(row, 0 to 7).halign(H_RIGHT)

                            // avoid left pane being pushed out when right pane has a lot of contents
                            runLater { minWidth = width }
                        }
                        anchorPane {
                            scrollPane {
                                isFitToWidth = true
                                hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                                outputPane = flowPane {
                                    padding = insetsOf(10)
                                    // minus vertical scrollbar width
                                    prefWidthProperty().bind(this@scrollPane.widthProperty() - 10)
                                }
                            }.anchor(0)
                            borderPane {
                                label(getString(R.string.no_content))
                                visibleProperty().bind(outputPane.children.isEmpty)
                                managedProperty().bind(outputPane.children.isEmpty)
                            }.anchor(0)
                        }.hgrow()
                    }.vgrow()
                }
            }
        }
        stage.show()

        if (isExpand) toggleExpand()
        if (isFill) toggleFill()
        if (isThick) toggleThick()
        mediaWidthField.requestFocus()
    }

    override fun stop() {
        isExpand = expandProperty.value; isFill = fillProperty.value; isThick = thickProperty.value
        mediaWidth = mediaWidthField.value; mediaHeight = mediaHeightField.value
        trimWidth = trimWidthField.value; trimHeight = trimHeightField.value
        gapHorizontal = gapHorizontalField.value; gapVertical = gapVerticalField.value
        gapLink = gapLinkToggle.isSelected
        allowFlipColumn = allowFlipRightCheck.isSelected; allowFlipRow = allowFlipBottomCheck.isSelected
        saver.save()
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
        saver.save()
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
                getString(R.string._update_available).format(release.name),
                DURATION_LONG,
                getString(R.string.btn_download)
            ) { hostServices.showDocument(release.htmlUrl) }
            else -> rootPane.jfxSnackbar(getString(R.string._update_unavailable), DURATION_LONG)
        }
    }
}
