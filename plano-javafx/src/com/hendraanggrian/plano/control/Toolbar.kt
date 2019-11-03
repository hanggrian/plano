package com.hendraanggrian.plano.control

import com.jfoenix.effects.JFXDepthManager
import ktfx.jfoenix.layouts.KtfxJFXToolbar

class Toolbar : KtfxJFXToolbar() {

    init {
        JFXDepthManager.setDepth(this, 0)
    }
}
