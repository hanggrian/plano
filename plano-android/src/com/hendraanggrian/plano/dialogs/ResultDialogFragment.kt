package com.hendraanggrian.plano.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hendraanggrian.bundler.Bundler
import com.hendraanggrian.bundler.Extra
import com.hendraanggrian.plano.MediaBox
import com.hendraanggrian.plano.R

class ResultDialogFragment : BottomSheetDialogFragment() {

    @Extra @JvmField var mediaBox: MediaBox? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Bundler.bindExtras(this)
        return BottomSheetDialog(context!!).apply {
            val view = layoutInflater.inflate(R.layout.dialog_result, null)
            setContentView(view)
            val root = view.findViewById<ViewGroup>(R.id.root)
            // root.addView()
        }
    }
}
