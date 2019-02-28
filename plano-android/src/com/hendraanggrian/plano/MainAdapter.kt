package com.hendraanggrian.plano

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView

class MainAdapter : RecyclerView.Adapter<MainAdapter.ViewHolder>(),
    MutableList<MediaSize> by arrayListOf() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_main, parent, false))
    }

    override fun getItemCount(): Int = size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val size = get(position)
        holder.card.addView(RelativeLayout(context).also { media ->
            ViewCompat.setBackground(media, ContextCompat.getDrawable(context, R.drawable.bg_media))
            media.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            media.post {
                media.layoutParams.height = (media.width * size.height / size.width).toInt()
                media.requestLayout()
                size.trimSizes.forEach { trimSize ->
                    media.addView(View(context).also { trim ->
                        ViewCompat.setBackground(
                            trim,
                            ContextCompat.getDrawable(context, R.drawable.bg_trim)
                        )
                        trim.layoutParams =
                            RelativeLayout.LayoutParams(
                                (trimSize.width).toInt(),
                                (trimSize.height).toInt()
                            ).apply {
                                addRule(RelativeLayout.ALIGN_PARENT_TOP or RelativeLayout.ALIGN_PARENT_LEFT)
                                leftMargin = trimSize.x.toInt()
                                topMargin = trimSize.y.toInt()
                            }
                    })
                }
            }
        })
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: ViewGroup = itemView.findViewById(R.id.card)
    }
}