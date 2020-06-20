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
            imageView(R.image.ic_toolbar)
            region { prefWidth = 10.0 }
            label(BuildConfig.NAME) { font = 26.pt }
        }
        rightItems {
            closeAllButton = addChild(
                RoundButton(resources, RoundButton.RADIUS_LARGE, R.string.close_all).apply {
                    id = R.style.btn_close
                }
            )
            expandButton = addChild(
                AdaptableRoundButton(
                    resources,
                    RoundButton.RADIUS_LARGE,
                    R.string.toggle_expand,
                    expandProperty,
                    R.style.btn_scale_shrink to R.style.btn_scale_expand
                )
            )
            fillButton = addChild(
                AdaptableRoundButton(
                    resources,
                    RoundButton.RADIUS_LARGE,
                    R.string.toggle_background,
                    fillProperty,
                    R.style.btn_background_unfill to R.style.btn_background_fill
                )
            )
            thickButton = addChild(
                AdaptableRoundButton(
                    resources,
                    RoundButton.RADIUS_LARGE,
                    R.string.toggle_border,
                    thickProperty,
                    R.style.btn_border_thin to R.style.btn_border_thick
                )
            )
        }
    }
}
