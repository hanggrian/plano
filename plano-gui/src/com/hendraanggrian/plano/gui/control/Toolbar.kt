package com.hendraanggrian.plano.gui.control

import com.jfoenix.effects.JFXDepthManager
import ktfx.jfoenix._JFXToolbar

class Toolbar : _JFXToolbar() {

    init {
        JFXDepthManager.setDepth(this, 0)
    }
}