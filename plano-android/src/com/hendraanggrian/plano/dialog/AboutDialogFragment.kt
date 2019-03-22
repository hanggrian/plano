package com.hendraanggrian.plano.dialog

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.hendraanggrian.bundler.Bundler
import com.hendraanggrian.plano.BuildConfig
import com.hendraanggrian.plano.R2

class AboutDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Bundler.bindExtras(this)
        return AlertDialog.Builder(context!!)
            .setTitle("${BuildConfig.NAME} ${BuildConfig.VERSION}")
            .setMessage(getString(R2.string._about))
            .setPositiveButton(getString(R2.string.btn_close)) { _, _ -> }
            .setNegativeButton(getString(R2.string.btn_homepage)) { _, _ ->
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.HOMEPAGE)))
            }
            .create()
    }
}