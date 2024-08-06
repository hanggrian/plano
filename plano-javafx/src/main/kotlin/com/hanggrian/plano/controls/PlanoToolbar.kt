package com.hanggrian.plano.controls

import com.hanggrian.plano.Resources
import com.hanggrian.plano_javafx.BuildConfig
import com.hanggrian.plano_javafx.R
import javafx.beans.property.BooleanProperty
import javafx.scene.control.Button
import ktfx.jfoenix.controls.depth
import ktfx.jfoenix.layouts.KtfxJfxToolbar
import ktfx.jfoenix.layouts.leftItems
import ktfx.jfoenix.layouts.rightItems
import ktfx.layouts.imageView
import ktfx.layouts.label
import ktfx.layouts.region

class PlanoToolbar(
    resources: Resources,
    expandProperty: BooleanProperty,
    fillProperty: BooleanProperty,
    thickProperty: BooleanProperty,
) : KtfxJfxToolbar(),
    Resources by resources {
    val closeAllButton: Button
    val expandButton: Button
    val fillButton: Button
    val thickButton: Button

    init {
        depth = 0
        leftItems {
            imageView(R.image_ic_toolbar)
            region { prefWidth = 10.0 }
            label(BuildConfig.NAME) { id = R.style_label_title }
        }
        rightItems {
            closeAllButton =
                addChild(
                    RoundButton(resources, RoundButton.RADIUS_LARGE, R.string_close_all).apply {
                        id = R.style_btn_close
                    },
                )
            expandButton =
                addChild(
                    AdaptableRoundButton(
                        resources,
                        RoundButton.RADIUS_LARGE,
                        R.string_toggle_expand,
                        expandProperty,
                        R.style_btn_scale_shrink to R.style_btn_scale_expand,
                    ),
                )
            fillButton =
                addChild(
                    AdaptableRoundButton(
                        resources,
                        RoundButton.RADIUS_LARGE,
                        R.string_toggle_background,
                        fillProperty,
                        R.style_btn_background_unfill to R.style_btn_background_fill,
                    ),
                )
            thickButton =
                addChild(
                    AdaptableRoundButton(
                        resources,
                        RoundButton.RADIUS_LARGE,
                        R.string_toggle_border,
                        thickProperty,
                        R.style_btn_border_thin to R.style_btn_border_thick,
                    ),
                )
        }
    }
}
