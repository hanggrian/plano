package com.hanggrian.plano

import com.hanggrian.plano.controls.FloatField
import com.hanggrian.plano.controls.PlanoToolbar
import com.hanggrian.plano.controls.ResultPane
import com.hanggrian.plano.controls.RoundButton
import com.hanggrian.plano.controls.RoundMorePaperButton
import com.hanggrian.plano.data.RecentMediaSize
import com.hanggrian.plano.data.RecentMediaSizes
import com.hanggrian.plano.data.RecentTrimSize
import com.hanggrian.plano.data.RecentTrimSizes
import com.hanggrian.plano.data.saveRecentSizes
import com.hanggrian.plano.help.AboutDialog
import com.hanggrian.plano.help.LicensesDialog
import com.hanggrian.plano.prefs.ALLOW_FLIP_COLUMN
import com.hanggrian.plano.prefs.ALLOW_FLIP_ROW
import com.hanggrian.plano.prefs.GAP_HORIZONTAL
import com.hanggrian.plano.prefs.GAP_LINK
import com.hanggrian.plano.prefs.GAP_VERTICAL
import com.hanggrian.plano.prefs.IS_EXPAND
import com.hanggrian.plano.prefs.IS_FILL
import com.hanggrian.plano.prefs.IS_THICK
import com.hanggrian.plano.prefs.LANGUAGE
import com.hanggrian.plano.prefs.MEDIA_HEIGHT
import com.hanggrian.plano.prefs.MEDIA_WIDTH
import com.hanggrian.plano.prefs.THEME
import com.hanggrian.plano.prefs.TRIM_HEIGHT
import com.hanggrian.plano.prefs.TRIM_WIDTH
import com.hanggrian.plano.util.THEME_DARK
import com.hanggrian.plano.util.THEME_LIGHT
import com.hanggrian.plano.util.THEME_SYSTEM
import com.hanggrian.plano.util.getResource
import com.hanggrian.plano.util.isDarkTheme
import com.hanggrian.plano_javafx.BuildConfig
import com.hanggrian.plano_javafx.R
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
import kotlinx.coroutines.withContext
import ktfx.bindings.bindingBy
import ktfx.bindings.booleanBindingOf
import ktfx.bindings.emptyBinding
import ktfx.bindings.eq
import ktfx.bindings.minus
import ktfx.booleanPropertyOf
import ktfx.controls.H_RIGHT
import ktfx.controls.insetsOf
import ktfx.controls.rowConstraints
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.doublePropertyOf
import ktfx.inputs.plus
import ktfx.jfoenix.controls.jfxSnackbar
import ktfx.jfoenix.controls.show
import ktfx.jfoenix.dialogs.showSingle
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
import ktfx.runLater
import ktfx.time.s
import ktfx.windows.minSize
import org.apache.commons.lang3.SystemUtils
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.util.ResourceBundle
import java.util.prefs.Preferences

class PlanoApp :
    Application(),
    Resources {
    private val scaleProperty = doublePropertyOf(SCALE_SMALL)
    private val expandProperty =
        booleanPropertyOf().apply {
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
                            else -> {
                                val (x, y) = node.userData as Pair<Double, Double>
                                AnchorPane.setLeftAnchor(node, x * scaleProperty.value)
                                AnchorPane.setTopAnchor(node, y * scaleProperty.value)
                            }
                        }
                    }
                }
            }
        }
    private val fillProperty =
        booleanPropertyOf().apply {
            listener { _, _, newValue -> fillMenu.isSelected = newValue }
        }
    private val thickProperty =
        booleanPropertyOf().apply {
            listener { _, _, newValue -> thickMenu.isSelected = newValue }
        }

    private val mediaWidthField = FloatField().apply { onAction { calculateButton.fire() } }
    private val mediaHeightField = FloatField().apply { onAction { calculateButton.fire() } }
    private val trimWidthField = FloatField().apply { onAction { calculateButton.fire() } }
    private val trimHeightField = FloatField().apply { onAction { calculateButton.fire() } }
    private val gapHorizontalField = FloatField().apply { onAction { calculateButton.fire() } }
    private val gapVerticalField = FloatField().apply { onAction { calculateButton.fire() } }
    private val gapLinkToggle =
        jfxToggleNode {
            idProperty().bind(
                this@jfxToggleNode
                    .selectedProperty()
                    .bindingBy { if (it) R.style_btn_link_on else R.style_btn_link_off },
            )
            selectedProperty().listener {
                when {
                    isSelected ->
                        gapHorizontalField
                            .textProperty()
                            .bindBidirectional(gapVerticalField.textProperty())
                    else ->
                        gapHorizontalField
                            .textProperty()
                            .unbindBidirectional(gapVerticalField.textProperty())
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

    private lateinit var prefs: Preferences
    override lateinit var resourceBundle: ResourceBundle

    override fun init() {
        if (BuildConfig2.DEBUG) {
            logger.info("DEBUG mode on")
        }

        prefs = Preferences.userRoot().node(BuildConfig.GROUP.replace('.', '/'))
        resourceBundle =
            Language
                .ofCode(prefs.get(LANGUAGE, Language.ENGLISH.code))
                .toResourcesBundle()

        Database.connect("jdbc:sqlite:/${SystemUtils.USER_HOME}/.plano.db", "org.sqlite.JDBC")
        transaction { SchemaUtils.create(RecentMediaSizes, RecentTrimSizes) }
    }

    override fun start(stage: Stage) {
        stage.title = BuildConfig.NAME
        stage.minSize = 750 to 500
        stage.scene {
            stylesheets.addAll(getResource(R.style_plano), getResource(R.style_plano_fonts))
            if (isDarkTheme(prefs.get(THEME, THEME_SYSTEM))) {
                stylesheets += getResource(R.style_plano_dark)
            }
            rootPane =
                stackPane {
                    vbox {
                        menuBar {
                            isUseSystemMenuBar = SystemUtils.IS_OS_MAC_OSX
                            if (!isUseSystemMenuBar) {
                                "File" {
                                    menuItem(getString(R.string_quit)) {
                                        accelerator = KeyCombination.SHORTCUT_DOWN + KeyCode.Q
                                        onAction { Platform.exit() }
                                    }
                                }
                            }
                            "Edit" {
                                menuItem(getString(R.string_close_all)) {
                                    onAction { closeAll() }
                                    runLater {
                                        disableProperty().bind(outputPane.children.emptyBinding)
                                    }
                                }
                                menuItem(getString(R.string_clear_recent_sizes)) {
                                    onAction {
                                        transaction {
                                            RecentMediaSizes.deleteAll()
                                            RecentTrimSizes.deleteAll()
                                        }
                                    }
                                }
                                separatorMenuItem()
                                menu(getString(R.string_preferences)) {
                                    menu(getString(R.string_theme)) {
                                        val theme = prefs.get(THEME, THEME_SYSTEM)
                                        val themeGroup = ToggleGroup()
                                        radioMenuItem(getString(R.string_system_default)) {
                                            toggleGroup = themeGroup
                                            isSelected = theme == THEME_SYSTEM
                                            onAction { setTheme(this@scene, THEME_SYSTEM) }
                                        }
                                        radioMenuItem(getString(R.string_light)) {
                                            toggleGroup = themeGroup
                                            isSelected = theme == THEME_LIGHT
                                            onAction { setTheme(this@scene, THEME_LIGHT) }
                                        }
                                        radioMenuItem(getString(R.string_dark)) {
                                            toggleGroup = themeGroup
                                            isSelected = theme == THEME_DARK
                                            onAction { setTheme(this@scene, THEME_DARK) }
                                        }
                                    }
                                    menu(getString(R.string_language)) {
                                        val group = ToggleGroup()
                                        Language.entries.forEach { lang ->
                                            radioMenuItem(lang.toLocale().displayLanguage) {
                                                toggleGroup = group
                                                isSelected =
                                                    lang.code ==
                                                    prefs.get(LANGUAGE, Language.ENGLISH.code)
                                                onAction {
                                                    prefs.put(LANGUAGE, lang.code)
                                                    PleaseRestartDialog(this@PlanoApp).show()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            "View" {
                                expandMenu =
                                    checkMenuItem(getString(R.string_toggle_expand)) {
                                        accelerator = KeyCombination.SHORTCUT_DOWN + KeyCode.DIGIT1
                                        onAction { toggleExpand() }
                                    }
                                fillMenu =
                                    checkMenuItem(getString(R.string_toggle_background)) {
                                        accelerator = KeyCombination.SHORTCUT_DOWN + KeyCode.DIGIT2
                                        onAction { toggleFill() }
                                    }
                                thickMenu =
                                    checkMenuItem(getString(R.string_toggle_border)) {
                                        accelerator = KeyCombination.SHORTCUT_DOWN + KeyCode.DIGIT3
                                        onAction { toggleThick() }
                                    }
                                separatorMenuItem()
                                menuItem(getString(R.string_reset)) {
                                    onAction {
                                        scaleProperty.value = SCALE_SMALL
                                        fillProperty.value = false
                                        thickProperty.value = false
                                    }
                                }
                            }
                            "Window" {
                                menuItem(getString(R.string_minimize)) {
                                    accelerator = KeyCombination.SHORTCUT_DOWN + KeyCode.M
                                    onAction { stage.isIconified = true }
                                }
                                menuItem(getString(R.string_zoom)) {
                                    onAction { stage.isMaximized = true }
                                }
                            }
                            "Help" {
                                menuItem(getString(R.string_check_for_update)) {
                                    onAction { checkForUpdate() }
                                }
                                menuItem(getString(R.string_view_on_github)) {
                                    onAction { hostServices.showDocument(BuildConfig.WEB) }
                                }
                                menuItem(getString(R.string_open_source_licenses)) {
                                    onAction { LicensesDialog(this@PlanoApp).showSingle() }
                                }
                                separatorMenuItem()
                                menuItem(getString(R.string_about)) {
                                    onAction { AboutDialog(this@PlanoApp).showSingle() }
                                }
                            }
                        }
                        addChild(
                            PlanoToolbar(
                                this@PlanoApp,
                                expandProperty,
                                fillProperty,
                                thickProperty,
                            ).apply {
                                closeAllButton.onAction { closeAll() }
                                closeAllButton.runLater {
                                    disableProperty().bind(outputPane.children.emptyBinding)
                                }
                                expandButton.onAction { toggleExpand() }
                                fillButton.onAction { toggleFill() }
                                thickButton.onAction { toggleThick() }
                            },
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

                                label(getString(R.string__desc)) {
                                    isWrapText = true
                                    maxWidth = 260.0
                                }.grid(row++, 0 to 7)

                                styledCircle(radius = 6.0, id = R.style_circle_amber) {
                                    tooltip(getString(R.string__media_size))
                                }.grid(row, 0)
                                label(getString(R.string_media_size)) {
                                    tooltip(getString(R.string__media_size))
                                }.grid(row, 1 to 2)
                                addChild(
                                    mediaWidthField.apply {
                                        tooltip(getString(R.string__media_size))
                                        value = prefs.getFloat(MEDIA_WIDTH, 0f)
                                    },
                                ).grid(row, 3)
                                label("\u00D7") { tooltip(getString(R.string__media_size)) }
                                    .grid(row, 4)
                                addChild(
                                    mediaHeightField.apply {
                                        tooltip(getString(R.string__media_size))
                                        value = prefs.getFloat(MEDIA_HEIGHT, 0f)
                                    },
                                ).grid(row, 5)
                                addChild(
                                    RoundMorePaperButton(
                                        this@PlanoApp,
                                        mediaWidthField,
                                        mediaHeightField,
                                    ) {
                                        RecentMediaSize.all()
                                    },
                                ).grid(row++, 6)

                                styledCircle(radius = 6.0, id = R.style_circle_red) {
                                    tooltip(getString(R.string__trim_size))
                                }.grid(row, 0)
                                label(getString(R.string_trim_size)) {
                                    tooltip(getString(R.string__trim_size))
                                }.grid(row, 1 to 2)
                                addChild(
                                    trimWidthField.apply {
                                        tooltip(getString(R.string__trim_size))
                                        value = prefs.getFloat(TRIM_WIDTH, 0f)
                                    },
                                ).grid(row, 3)
                                label("\u00D7") { tooltip(getString(R.string__trim_size)) }
                                    .grid(row, 4)
                                addChild(
                                    trimHeightField.apply {
                                        tooltip(getString(R.string__trim_size))
                                        value = prefs.getFloat(TRIM_HEIGHT, 0f)
                                    },
                                ).grid(row, 5)
                                addChild(
                                    RoundMorePaperButton(
                                        this@PlanoApp,
                                        trimWidthField,
                                        trimHeightField,
                                    ) {
                                        RecentTrimSize.all()
                                    },
                                ).grid(row++, 6)

                                label(getString(R.string_gap)) { tooltip(getString(R.string__gap)) }
                                    .grid(row, 1)
                                label("\u2194︎") { tooltip(getString(R.string__gap)) }
                                    .grid(row, 2)
                                    .halign(H_RIGHT)
                                addChild(
                                    gapHorizontalField.apply {
                                        tooltip(getString(R.string__gap))
                                        value = prefs.getFloat(GAP_HORIZONTAL, 0f)
                                    },
                                ).grid(row, 3)
                                label("\u2195") { tooltip(getString(R.string__gap)) }
                                    .grid(row, 4)
                                addChild(
                                    gapVerticalField.apply {
                                        tooltip(getString(R.string__gap))
                                        value = prefs.getFloat(GAP_VERTICAL, 0f)
                                    },
                                ).grid(row, 5)
                                addChild(
                                    gapLinkToggle.apply {
                                        tooltip(getString(R.string__gap))
                                        isSelected = prefs.getBoolean(GAP_LINK, false)
                                    },
                                ).grid(row++, 6)

                                label(getString(R.string_allow_flip)) {
                                    tooltip(getString(R.string__allow_flip))
                                }.grid(row, 1 to 2)
                                addChild(
                                    allowFlipRightCheck.apply {
                                        text = getString(R.string_right)
                                        tooltip(getString(R.string__allow_flip))
                                        isSelected = prefs.getBoolean(ALLOW_FLIP_COLUMN, false)
                                    },
                                ).grid(row++, 3 to 4)
                                addChild(
                                    allowFlipBottomCheck.apply {
                                        text = getString(R.string_bottom)
                                        tooltip(getString(R.string__allow_flip))
                                        isSelected = prefs.getBoolean(ALLOW_FLIP_ROW, false)
                                    },
                                ).grid(row++, 3 to 4)

                                calculateButton =
                                    addChild(
                                        RoundButton(
                                            this@PlanoApp,
                                            RoundButton.RADIUS_IC,
                                            R.string_calculate,
                                        ).apply {
                                            id = R.style_ic_calculate
                                            disableProperty().bind(
                                                booleanBindingOf(
                                                    mediaWidthField.textProperty(),
                                                    mediaHeightField.textProperty(),
                                                    trimWidthField.textProperty(),
                                                    trimHeightField.textProperty(),
                                                ) {
                                                    when {
                                                        mediaWidthField.value <= 0.0 ||
                                                            mediaHeightField.value <= 0.0 -> true
                                                        trimWidthField.value <= 0.0 ||
                                                            trimHeightField.value <= 0.0 -> true
                                                        else -> false
                                                    }
                                                },
                                            )
                                            onAction {
                                                stop()
                                                outputPane.children.add(
                                                    0,
                                                    ktfx.layouts.pane {
                                                        addChild(
                                                            ResultPane(
                                                                this@PlanoApp,
                                                                mediaWidthField.value,
                                                                mediaHeightField.value,
                                                                trimWidthField.value,
                                                                trimHeightField.value,
                                                                gapHorizontalField.value,
                                                                gapVerticalField.value,
                                                                allowFlipRightCheck.isSelected,
                                                                allowFlipBottomCheck.isSelected,
                                                                scaleProperty,
                                                                fillProperty,
                                                                thickProperty,
                                                            ),
                                                        )
                                                    },
                                                )
                                                saveRecentSizes(
                                                    mediaWidthField.value,
                                                    mediaHeightField.value,
                                                    trimWidthField.value,
                                                    trimHeightField.value,
                                                )
                                            }
                                        },
                                    ).grid(row, 0 to 7)
                                        .halign(H_RIGHT)

                                // avoid left pane being pushed out when right pane has a lot of
                                // contents
                                runLater { minWidth = width }
                            }
                            anchorPane {
                                scrollPane {
                                    isFitToWidth = true
                                    hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                                    outputPane =
                                        flowPane {
                                            padding = insetsOf(10)
                                            // minus vertical scrollbar width
                                            prefWidthProperty()
                                                .bind(this@scrollPane.widthProperty() - 10)
                                        }
                                }.anchor(0)
                                borderPane {
                                    label(getString(R.string__no_content))
                                    visibleProperty().bind(outputPane.children.emptyBinding)
                                    managedProperty().bind(outputPane.children.emptyBinding)
                                }.anchor(0)
                            }.hgrow()
                        }.vgrow()
                    }
                }
        }
        stage.show()

        if (prefs.getBoolean(IS_EXPAND, false)) toggleExpand()
        if (prefs.getBoolean(IS_FILL, false)) toggleFill()
        if (prefs.getBoolean(IS_THICK, false)) toggleThick()
        mediaWidthField.requestFocus()
    }

    override fun stop() {
        prefs.putBoolean(IS_EXPAND, expandProperty.value)
        prefs.putBoolean(IS_FILL, fillProperty.value)
        prefs.putBoolean(IS_THICK, thickProperty.value)
        prefs.putFloat(MEDIA_WIDTH, mediaWidthField.value)
        prefs.putFloat(MEDIA_HEIGHT, mediaHeightField.value)
        prefs.putFloat(TRIM_WIDTH, trimWidthField.value)
        prefs.putFloat(TRIM_HEIGHT, trimHeightField.value)
        prefs.putFloat(GAP_HORIZONTAL, gapHorizontalField.value)
        prefs.putFloat(GAP_VERTICAL, gapVerticalField.value)
        prefs.putBoolean(GAP_LINK, gapLinkToggle.isSelected)
        prefs.putBoolean(ALLOW_FLIP_COLUMN, allowFlipRightCheck.isSelected)
        prefs.putBoolean(ALLOW_FLIP_ROW, allowFlipBottomCheck.isSelected)
    }

    fun closeAll() {
        val children = outputPane.children.toList()
        outputPane.children.clear()
        rootPane.jfxSnackbar.show(
            getString(R.string__boxes_cleared),
            DURATION_SHORT,
            getString(R.string_btn_undo),
        ) {
            outputPane.children += children
        }
        mediaWidthField.requestFocus()
    }

    private fun setTheme(scene: Scene, theme: String) {
        prefs.put(THEME, theme)
        val darkTheme = getResource(R.style_plano_dark)
        when {
            isDarkTheme(theme) ->
                if (darkTheme !in scene.stylesheets) {
                    scene.stylesheets += darkTheme
                }
            else -> scene.stylesheets -= darkTheme
        }
    }

    private fun toggleExpand() {
        scaleProperty.value =
            when (scaleProperty.value) {
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
            release.isNewerThan(BuildConfig.VERSION) ->
                rootPane.jfxSnackbar.show(
                    getString(R.string__update_available).format(release.name),
                    DURATION_LONG,
                    getString(R.string_btn_download),
                ) { hostServices.showDocument(release.htmlUrl) }
            else ->
                rootPane.jfxSnackbar.show(getString(R.string__update_unavailable), DURATION_LONG)
        }
    }

    companion object {
        val DURATION_SHORT = 3.s
        val DURATION_LONG = 6.s
        const val SCALE_SMALL = 2.0
        const val SCALE_BIG = 4.0

        private val logger = LoggerFactory.getLogger(PlanoApp::class.java)

        @JvmStatic
        fun main(args: Array<String>): Unit = launchApplication<PlanoApp>(*args)
    }
}
