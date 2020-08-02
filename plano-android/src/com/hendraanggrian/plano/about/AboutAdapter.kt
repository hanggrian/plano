package com.hendraanggrian.plano.about

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hendraanggrian.plano.BuildConfig
import com.hendraanggrian.plano.R
import com.hendraanggrian.plano.util.openUrl

class AboutAdapter : RecyclerView.Adapter<AboutAdapter.ViewHolder>() {
    private lateinit var context: Context

    override fun getItemCount(): Int = 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_about, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (position) {
            0 -> {
                holder.image.setImageResource(R.drawable.ic_about1)
                holder.text.text = context.getString(R.string.about_option1).format(BuildConfig.VERSION_NAME)
                holder.itemView.setOnClickListener {
                    context.openUrl("${BuildConfig.WEB}/releases")
                }
            }
            1 -> {
                holder.image.setImageResource(R.drawable.ic_about2)
                holder.text.setText(R.string.about_option2)
                holder.itemView.setOnClickListener {
                    context.openUrl(BuildConfig.WEB)
                }
            }
            2 -> {
                holder.image.setImageResource(R.drawable.ic_about3)
                holder.text.setText(R.string.about_option3)
                holder.itemView.setOnClickListener {
                    context.startActivity(Intent(context, LicensesActivity::class.java))
                }
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
        val text: TextView = itemView.findViewById(R.id.text)
    }
}