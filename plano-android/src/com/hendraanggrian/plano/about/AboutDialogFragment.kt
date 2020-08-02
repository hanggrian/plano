package com.hendraanggrian.plano.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hendraanggrian.plano.R

class AboutDialogFragment : BottomSheetDialogFragment() {
    private lateinit var aboutText: TextView
    private lateinit var aboutRecycler: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_about, container, false)
        aboutText = view.findViewById(R.id.aboutText)
        aboutText.setText(R.string._about)
        aboutRecycler = view.findViewById(R.id.aboutRecycler)
        aboutRecycler.setHasFixedSize(true)
        aboutRecycler.adapter = AboutAdapter()
        return view
    }
}