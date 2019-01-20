package com.hendraanggrian.plano.gui

import javafx.application.Application
import javafx.scene.control.TextField
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.Priority
import javafx.stage.Stage
import ktfx.controls.gap
import ktfx.controls.paddingAll
import ktfx.jfoenix.jfxRadioButton
import ktfx.layouts.gridPane
import ktfx.layouts.hbox
import ktfx.layouts.label
import ktfx.layouts.scene
import ktfx.layouts.scrollPane

class PlanoApplication : Application() {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) = ktfx.launch<PlanoApplication>(*args)
    }

    lateinit var sourceWidthField: TextField
    lateinit var sourceHeightField: TextField
    lateinit var targetWidthField: TextField
    lateinit var targetHeightField: TextField

    override fun start(primaryStage: Stage) {

        primaryStage.scene = scene {
            hbox {
                gridPane {
                    paddingAll = 10
                    gap = 10
                    var row = 0
                    val radioGroup = ToggleGroup()
                    jfxRadioButton("Calculate print size from plano size") {
                        toggleGroup = radioGroup
                    } row row++ col 0 colSpans 3
                    jfxRadioButton("Calculate print size from plano size") {
                        toggleGroup = radioGroup
                    } row row++ col 0 colSpans 3
                    label("Plano size") row row++ col 0 colSpans 3
                    sourceWidthField = doubleField() row row col 0
                    label("x") row row col 1
                    sourceHeightField = doubleField() row row++ col 2
                    label("Print size") row row++ col 0 colSpans 3
                    targetWidthField = doubleField() row row col 0
                    label("x") row row col 1
                    targetHeightField = doubleField() row row++ col 2
                    label("Piece size") row row++ col 0 colSpans 3
                    doubleField() row row col 0
                    label("x") row row col 1
                    doubleField() row row col 2
                }
                scrollPane {

                } hpriority Priority.ALWAYS
            }
        }
        primaryStage.show()
    }
}