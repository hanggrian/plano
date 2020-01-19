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
    expandProperty: BooleanProperty,
    fillProperty: BooleanProperty,
    thickProperty: BooleanProperty
) : KtfxJFXToolbar(), Resources by resources {

    val closeAllButton: Button
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
            closeAllButton = addChild(RoundButton(24, getString(R.string.close_all))) { id = R.style.btn_close }
            expandButton = addChild(
                AdaptableRoundButton(
                    24,
                    expandProperty,
                    getString(R.string.shrink) to getString(R.string.expand),
                    R.style.btn_scale_expand to R.style.btn_scale_shrink
                )
            )
            fillButton = addChild(
                AdaptableRoundButton(
                    24,
                    fillProperty,
                    getString(R.string.unfill_background) to getString(R.string.fill_background),
                    R.style.btn_background_fill to R.style.btn_background_unfill
                )
            )
            thickButton = addChild(
                AdaptableRoundButton(
                    24,
                    thickProperty,
                    getString(R.string.unthicken_border) to getString(R.string.thicken_border),
                    R.style.btn_border_thick to R.style.btn_border_thin
                )
            )
        }
    }
}
