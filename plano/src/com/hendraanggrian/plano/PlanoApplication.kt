package com.hendraanggrian.plano

import javafx.application.Application
import javafx.stage.Stage
import ktfx.jfoenix.jfxToolbar
import ktfx.launchApplication
import ktfx.layouts.label
import ktfx.layouts.scene
import ktfx.layouts.separator
import ktfx.layouts.vbox

class PlanoApplication : Application() {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) = launchApplication<PlanoApplication>(*args)
    }

    override fun start(stage: Stage) {
        stage.scene = scene {
            vbox {
                jfxToolbar {
                    leftItems {
                        label("Plano")
                    }
                    rightItems {
                    }
                }
                separator()

            }
        }
        stage.show()
    }
}