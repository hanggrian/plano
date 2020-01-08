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
import ktfx.layouts.imageView
import ktfx.layouts.label
import ktfx.layouts.region

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
            region { prefWidth = 12.0 }
            label(BuildConfig.NAME) { styleClass.addAll("display2", "dark") }
        }
        rightItems {
            clearButton = addChild(RoundButton(24, getString(R.string.clear), R.image.btn_clear))
            expandButton = addChild(
                AdaptableRoundButton(
                    24,
                    expandedProperty,
                    getString(R.string.shrink),
                    getString(R.string.expand),
                    R.image.btn_scale_expand,
                    R.image.btn_scale_shrink
                )
            )
            fillButton = addChild(
                AdaptableRoundButton(
                    24,
                    filledProperty,
                    getString(R.string.unfill_background),
                    getString(R.string.fill_background),
                    R.image.btn_background_fill,
                    R.image.btn_background_unfill
                )
            )
            thickButton = addChild(
                AdaptableRoundButton(
                    24,
                    thickProperty,
                    getString(R.string.unthicken_border),
                    getString(R.string.thicken_border),
                    R.image.btn_border_thick,
                    R.image.btn_border_thin
                )
            )
        }
    }
}
