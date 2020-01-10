package com.hendraanggrian.plano.controls

import com.hendraanggrian.plano.BuildConfig
import com.hendraanggrian.plano.R
import com.hendraanggrian.plano.Resources
import javafx.beans.property.BooleanProperty
import javafx.scene.control.Button
import ktfx.jfoenix.controls.depth
import ktfx.jfoenix.layouts.KtfxJFXToolbar
import ktfx.jfoenix.layouts.leftItems
import ktfx.jfoenix.layouts.rightItems
import ktfx.layouts.addChild
import ktfx.layouts.imageView
import ktfx.layouts.label
import ktfx.layouts.region
import ktfx.text.pt

class PlanoToolbar(
    resources: Resources,
    expandedProperty: BooleanProperty,
    filledProperty: BooleanProperty,
    thickProperty: BooleanProperty
) : KtfxJFXToolbar(), Resources by resources {

    val clearButton: Button
    val expandButton: Button
    val fillButton: Button
    val thickButton: Button

    init {
        depth = 0
        leftItems {
            imageView(R.image.ic_launcher)
            region { prefWidth = 10.0 }
            label(BuildConfig.NAME) { font = 24.pt }
        }
        rightItems {
            clearButton = addChild(SimpleRoundButton(24, getString(R.string.clear))) { id = "btn-clear" }
            expandButton = addChild(
                AdaptableRoundButton(
                    24,
                    expandedProperty,
                    getString(R.string.shrink) to getString(R.string.expand),
                    "btn-scale-expand" to "btn-scale-shrink"
                )
            )
            fillButton = addChild(
                AdaptableRoundButton(
                    24,
                    filledProperty,
                    getString(R.string.unfill_background) to getString(R.string.fill_background),
                    "btn-background-fill" to "btn-background-unfill"
                )
            )
            thickButton = addChild(
                AdaptableRoundButton(
                    24,
                    thickProperty,
                    getString(R.string.unthicken_border) to getString(R.string.thicken_border),
                    "btn-border-thick" to "btn-border-thin"
                )
            )
        }
    }
}
