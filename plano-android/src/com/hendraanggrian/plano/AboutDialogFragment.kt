package com.hendraanggrian.plano

import android.app.Dialog
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.DialogFragment
import com.hendraanggrian.bundler.Bundler
import com.hendraanggrian.bundler.Extra

class AboutDialogFragment : DialogFragment() {

    @Extra lateinit var resources: Resources // need extra key or else will crash

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Bundler.bindExtras(this)
        return AlertDialog.Builder(context!!)
            .setTitle("${BuildConfig.NAME} ${BuildConfig.VERSION}")
            .setView(TextView(context).apply {
                TextViewCompat.setTextAppearance(
                    this,
                    R.style.TextAppearance_AppCompat_Widget_ActionBar_Subtitle
                )
                text = buildSpannedString {
                    appendln(this@AboutDialogFragment.resources.getString(R2.string._about1))
                    append(this@AboutDialogFragment.resources.getString(R2.string._about2_1))
                    inSpans(URLSpan(BuildConfig.WEBSITE)) {
                        append(this@AboutDialogFragment.resources.getString(R2.string._about2_2))
                    }
                    append(this@AboutDialogFragment.resources.getString(R2.string._about2_3))
                }
                movementMethod = LinkMovementMethod.getInstance()
                setPadding(
                    resources.getDimensionPixelSize(R.dimen.large),
                    resources.getDimensionPixelSize(R.dimen.medium),
                    resources.getDimensionPixelSize(R.dimen.large),
                    resources.getDimensionPixelSize(R.dimen.medium)
                )
            })
            .setPositiveButton(android.R.string.ok) { _, _ -> }
            .create()
    }
}