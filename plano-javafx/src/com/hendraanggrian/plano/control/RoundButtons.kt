package com.hendraanggrian.plano.control

import com.hendraanggrian.plano.PlanoApp.Companion.BUTTON_OPACITY
import com.hendraanggrian.plano.R
import com.hendraanggrian.plano.Resources
import com.jfoenix.controls.JFXButton
import javafx.beans.binding.Bindings.`when`
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableValue
import javafx.geometry.Side
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import javafx.scene.shape.Circle
import ktfx.bindings.buildBinding
import ktfx.bindings.otherwise
import ktfx.bindings.then
import ktfx.coroutines.onAction
import ktfx.finalStringPropertyOf
import ktfx.layouts.LayoutDslMarker
import ktfx.layouts.MenuItemManager
import ktfx.layouts._ContextMenu
import ktfx.layouts.contextMenu
import ktfx.layouts.menu
import ktfx.layouts.menuItem
import ktfx.layouts.separatorMenuItem
import ktfx.layouts.tooltip

@Suppress("LeakingThis")
sealed class AbstractRoundButton(
    radius: Number,
    textBinding: ObservableValue<String>
) : JFXButton() {
    init {
        val r = radius.toDouble()
        shape = Circle(r)
        setMinSize(2 * r, 2 * r)
        setMaxSize(2 * r, 2 * r)

        tooltip {
            textProperty().bind(textBinding)
        }
    }
}

open class SimpleRoundButton(
    radius: Number,
    text: String,
    graphicUrl: String
) : AbstractRoundButton(radius, finalStringPropertyOf(text)) {
    init {
        graphic = ImageView(graphicUrl)
    }
}

open class RoundButton(
    radius: Number,
    textBinding: ObservableValue<String>,
    graphicUrl: String
) : AbstractRoundButton(radius, textBinding) {
    constructor(
        radius: Number,
        text: String,
        graphicUrl: String
    ) : this(radius, finalStringPropertyOf(text), graphicUrl)

    init {
        graphicProperty().bind(buildBinding(hoverProperty()) {
            ImageView(graphicUrl).also { if (!isHover) it.opacity = BUTTON_OPACITY }
        })
    }
}

open class AdaptableRoundButton(
    radius: Number,
    dependency: ObservableBooleanValue,
    text1: String,
    text2: String,
    graphicUrl1: String,
    graphicUrl2: String
) : AbstractRoundButton(radius, `when`(dependency) then text1 otherwise text2) {
    init {
        graphicProperty().bind(buildBinding(dependency, hoverProperty()) {
            ImageView(
                when {
                    dependency.value -> graphicUrl1
                    else -> graphicUrl2
                }
            ).also { if (!isHover) it.opacity = BUTTON_OPACITY }
        })
    }
}

open class InfoButton(
    resources: Resources,
    container: StackPane,
    titleId: String,
    contentId: String
) : RoundButton(16, resources.getString(R.string.info), R.image.menu_info) {
    init {
        onAction {
            TextDialog(resources, container, titleId, contentId).show()
        }
    }
}

open class MoreButton(
    resources: Resources,
    init: ((@LayoutDslMarker _ContextMenu).() -> Unit)? = null
) : RoundButton(16, resources.getString(R.string.more), R.image.menu_more) {
    init {
        val contextMenu = contextMenu(init)
        onAction {
            if (!contextMenu.isShowing) {
                contextMenu.show(this@MoreButton, Side.RIGHT, 0.0, 0.0)
            }
        }
    }
}

open class MorePaperButton(
    resources: Resources,
    widthField: TextField,
    heightField: TextField
) : MoreButton(resources, {
    menu(resources.getString(R.string.a_series)) {
        paperMenuItem(118.9, 84.1, widthField, heightField, "A0")
        paperMenuItem(84.1, 59.4, widthField, heightField, "A1")
        paperMenuItem(59.4, 42, widthField, heightField, "A2")
        paperMenuItem(42, 29.7, widthField, heightField, "A3")
        paperMenuItem(29.7, 21, widthField, heightField, "A4")
        paperMenuItem(21, 14.8, widthField, heightField, "A5")
        paperMenuItem(14.8, 10.5, widthField, heightField, "A6")
        paperMenuItem(10.5, 7.4, widthField, heightField, "A7")
        paperMenuItem(7.4, 5.2, widthField, heightField, "A8")
        paperMenuItem(5.2, 3.7, widthField, heightField, "A9")
        paperMenuItem(3.7, 2.6, widthField, heightField, "A10")
    }
    menu(resources.getString(R.string.b_series)) {
        paperMenuItem(141.4, 100, widthField, heightField, "B0")
        paperMenuItem(100, 70.7, widthField, heightField, "B1")
        paperMenuItem(70.7, 50, widthField, heightField, "B2")
        paperMenuItem(50, 35.3, widthField, heightField, "B3")
        paperMenuItem(35.3, 25, widthField, heightField, "B4")
        paperMenuItem(25, 17.6, widthField, heightField, "B5")
        paperMenuItem(17.6, 12.5, widthField, heightField, "B6")
        paperMenuItem(12.5, 8.8, widthField, heightField, "B7")
        paperMenuItem(8.8, 6.2, widthField, heightField, "B8")
        paperMenuItem(6.2, 4.4, widthField, heightField, "B9")
        paperMenuItem(4.4, 3.1, widthField, heightField, "B10")
    }
    separatorMenuItem()
    paperMenuItem(61, 86, widthField, heightField)
    paperMenuItem(61, 92, widthField, heightField)
    paperMenuItem(65, 90, widthField, heightField)
    paperMenuItem(65, 100, widthField, heightField)
    paperMenuItem(79, 109, widthField, heightField)
    separatorMenuItem()
    paperMenuItem(70, 108, widthField, heightField)
    paperMenuItem(86, 106, widthField, heightField)
})

private fun MenuItemManager.paperMenuItem(
    width: Number,
    height: Number,
    widthField: TextField,
    heightField: TextField,
    name: String? = null
) {
    val defaultTitle = "$width x $height"
    menuItem(name?.let { "$it\t $defaultTitle" } ?: defaultTitle) {
        onAction {
            widthField.text = width.toString()
            heightField.text = height.toString()
        }
    }
}
