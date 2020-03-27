package com.hendraanggrian.plano

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.RecyclerView
import com.hendraanggrian.plano.dialogs.ResultDialogFragment

class MainAdapter(private val viewModel: MainViewModel) : RecyclerView.Adapter<MainAdapter.ViewHolder>(),
    MutableList<MediaBox> by arrayListOf() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_main, parent, false))
    }

    override fun getItemCount(): Int = size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mediaBox = get(position)
        if (holder.card.isNotEmpty()) holder.card.removeAllViews()
        holder.card.addView(RelativeLayout(context).also { media ->
            ViewCompat.setBackground(media, ContextCompat.getDrawable(context, mediaBackground))
            media.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            media.post {
                media.layoutParams.height = (media.width * mediaBox.height / mediaBox.width).toInt()
                media.requestLayout()
                media.post {
                    mediaBox.forEach { trimBox ->
                        media.addView(View(context).also { trim ->
                            ViewCompat.setBackground(trim, ContextCompat.getDrawable(context, trimBackground))
                            val widthRatio = media.width / mediaBox.width
                            val heightRatio = media.height / mediaBox.height
                            trim.layoutParams = RelativeLayout.LayoutParams(
                                (trimBox.width * widthRatio).toInt(),
                                (trimBox.height * heightRatio).toInt()
                            ).apply {
                                addRule(RelativeLayout.ALIGN_PARENT_TOP)
                                addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                                leftMargin = (trimBox.x * widthRatio).toInt()
                                topMargin = (trimBox.y * heightRatio).toInt()
                            }
                        })
                    }
                }
            }
        })
        holder.card.setOnClickListener {
            ResultDialogFragment()
                .also { it.arguments = bundleOf("mediaBox" to mediaBox) }
                .show((context as AppCompatActivity).supportFragmentManager, null)
        }
    }

    fun put(element: MediaBox) {
        if (isEmpty()) viewModel.emptyData.value = false
        add(element)
        notifyItemInserted(size - 1)
    }

    fun putAll(elements: Collection<MediaBox>) {
        if (isEmpty()) viewModel.emptyData.value = false
        val start = size + 1
        addAll(elements)
        notifyItemRangeInserted(start, size)
    }

    fun removeAll() {
        viewModel.emptyData.value = true
        val size = size
        clear()
        notifyItemRangeRemoved(0, size)
    }

    private val mediaBackground: Int
        get() = when {
            viewModel.fillData.value!! && viewModel.thickData.value!! -> R.drawable.bg_media_fill_thick
            !viewModel.fillData.value!! && viewModel.thickData.value!! -> R.drawable.bg_media_unfill_thick
            viewModel.fillData.value!! && !viewModel.thickData.value!! -> R.drawable.bg_media_fill_thin
            else -> R.drawable.bg_media_unfill_thin
        }

    private val trimBackground: Int
        get() = when {
            viewModel.fillData.value!! && viewModel.thickData.value!! -> R.drawable.bg_trim_fill_thick
            !viewModel.fillData.value!! && viewModel.thickData.value!! -> R.drawable.bg_trim_unfill_thick
            viewModel.fillData.value!! && !viewModel.thickData.value!! -> R.drawable.bg_trim_fill_thin
            else -> R.drawable.bg_trim_unfill_thin
        }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: ViewGroup = itemView.findViewById(R.id.card)
    }
}
