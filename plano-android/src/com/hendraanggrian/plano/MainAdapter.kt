package com.hendraanggrian.plano

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.get
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.RecyclerView

class MainAdapter(private val viewModel: MainViewModel) :
    RecyclerView.Adapter<MainAdapter.ViewHolder>(),
    MutableList<MediaBox2> by arrayListOf() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_main, parent, false))
    }

    override fun getItemCount(): Int = size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mediaBox = get(position)
        if (holder.mediaContainer.isNotEmpty()) holder.mediaContainer.removeAllViews()
        holder.populate(mediaBox)
        holder.card.setOnCreateContextMenuListener { menu, v, _ ->
            val itemCount = ((((v as ViewGroup)[0] as ViewGroup)[1] as ViewGroup)[0] as ViewGroup).childCount
            menu.setHeaderTitle(context.resources.getQuantityString(R.plurals.items, itemCount, itemCount))
            menu.add(Menu.NONE, 0, 0, R.string.allow_flip).run {
                isCheckable = true
                isChecked = mediaBox.allowFlip
                setOnMenuItemClickListener {
                    mediaBox.allowFlip = !mediaBox.allowFlip
                    holder.populate(mediaBox)
                    false
                }
            }
            menu.add(Menu.NONE, 0, 0, R.string.rotate).setOnMenuItemClickListener {
                mediaBox.rotate()
                holder.populate(mediaBox)
                false
            }
            menu.add(Menu.FIRST, 1, 1, R.string.close).setOnMenuItemClickListener {
                delete(mediaBox)
                false
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                menu.setGroupDividerEnabled(true)
            }
        }
    }

    fun put(element: MediaBox2) {
        if (isEmpty()) {
            viewModel.emptyData.value = false
        }
        add(element)
        notifyItemInserted(size - 1)
    }

    fun putAll(elements: Collection<MediaBox2>) {
        if (isEmpty()) {
            viewModel.emptyData.value = false
        }
        val start = size + 1
        addAll(elements)
        notifyItemRangeInserted(start, size)
    }

    fun delete(element: MediaBox2) {
        val index = indexOf(element)
        remove(element)
        notifyItemRemoved(index)
        if (isEmpty()) {
            viewModel.emptyData.value = true
        }
    }

    fun deleteAll() {
        viewModel.emptyData.value = true
        val size = size
        clear()
        notifyItemRangeRemoved(0, size)
    }

    private fun ViewHolder.populate(mediaBox: MediaBox2) {
        mediaText.text = "${mediaBox.width.clean()} x ${mediaBox.height.clean()}"
        trimCountText.text = mediaBox.size.toString()
        trimText.text = "${(mediaBox.trimWidth + mediaBox.bleed * 2).clean()} x " +
            "${(mediaBox.trimHeight + mediaBox.bleed * 2).clean()}"

        if (isNotEmpty()) mediaContainer.removeAllViews()
        mediaContainer.addView(
            RelativeLayout(context).also { media ->
                ViewCompat.setBackground(media, ContextCompat.getDrawable(context, mediaBackground))
                media.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                media.post {
                    media.layoutParams.height = (media.width * mediaBox.height / mediaBox.width).toInt()
                    media.requestLayout()
                    media.post {
                        mediaBox.forEach { trimBox ->
                            media.addView(
                                View(context).also { trim ->
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
                                }
                            )
                        }
                    }
                }
            }
        )
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
        val card = itemView.findViewById<CardView>(R.id.card)!!
        val mediaContainer = itemView.findViewById<ViewGroup>(R.id.mediaContainer)!!
        val mediaText = itemView.findViewById<TextView>(R.id.mediaText)!!
        val trimCountText = itemView.findViewById<TextView>(R.id.trimCountText)!!
        val trimText = itemView.findViewById<TextView>(R.id.trimText)!!
    }
}
