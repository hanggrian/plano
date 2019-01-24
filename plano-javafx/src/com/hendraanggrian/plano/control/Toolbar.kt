package com.hendraanggrian.plano.control

import com.jfoenix.effects.JFXDepthManager
import ktfx.jfoenix._JFXToolbar

class Toolbar : _JFXToolbar() {

    init {
        JFXDepthManager.setDepth(this, 0)
    }
}