package com.hendraanggrian.plano.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hendraanggrian.plano.BuildConfig
import com.hendraanggrian.plano.MainActivity
import com.hendraanggrian.plano.R
import kotlinx.android.synthetic.main.activity_main.*

class AboutDialogFragment : BottomSheetDialogFragment() {
    private lateinit var aboutToolbar: Toolbar
    private lateinit var aboutRecycler: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_about, container, false)
        aboutToolbar = view.findViewById(R.id.aboutToolbar)
        aboutToolbar.title = "${BuildConfig.NAME} ${BuildConfig.VERSION_NAME}"
        aboutRecycler = view.findViewById(R.id.aboutRecycler)
        aboutRecycler.setHasFixedSize(true)
        aboutRecycler.adapter = AboutAdapter {
            dismiss()
            (activity as MainActivity).fab
        }
        return view
    }
}
