package com.hendraanggrian.plano.dialog

import androidx.appcompat.app.AppCompatDialogFragment
import com.hendraanggrian.bundler.Extra
import com.hendraanggrian.plano.Resources
import java.util.ResourceBundle

open class DialogFragment : AppCompatDialogFragment(), Resources {

    @Extra lateinit var resources: Resources

    override val resourceBundle: ResourceBundle get() = resources.resourceBundle
}