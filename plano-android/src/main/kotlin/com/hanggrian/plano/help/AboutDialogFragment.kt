package com.hanggrian.plano.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hanggrian.plano.BuildConfig
import com.hanggrian.plano.MainActivity
import com.hanggrian.plano.R

class AboutDialogFragment : BottomSheetDialogFragment() {
    private lateinit var versionText: TextView
    private lateinit var aboutRecycler: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = inflater.inflate(R.layout.fragment_about, container, false)
        versionText = view.findViewById(R.id.text_version)
        versionText.text = "${BuildConfig.NAME} ${BuildConfig.VERSION_NAME}"
        aboutRecycler = view.findViewById(R.id.recycler_about)
        aboutRecycler.adapter =
            AboutAdapter {
                dismiss()
                (activity as MainActivity).action
            }
        return view
    }
}
