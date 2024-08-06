package com.hanggrian.plano.help

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hanggrian.plano.License
import com.hanggrian.plano.R
import com.hanggrian.plano.util.openUrl

class LicensesAdapter : RecyclerView.Adapter<LicensesAdapter.ViewHolder>() {
    private lateinit var context: Context
    private val licenses: List<License> =
        License.listAll(
            "Android Jetpack" to
                "https://android.googlesource.com/platform/frameworks/support/+/" +
                "androidx-master-dev/LICENSE.txt",
            "Android Material Components" to
                "https://github.com/material-components/material-components-android/blob/master/" +
                "LICENSE",
            "Process Phoenix" to
                "https://github.com/JakeWharton/ProcessPhoenix/blob/master/LICENSE.txt",
        )

    override fun getItemCount(): Int = licenses.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_license, parent, false),
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val license = licenses[position]
        holder.text.text = license.name
        holder.text.setOnClickListener { context.openUrl(license.url) }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView as TextView
    }
}
