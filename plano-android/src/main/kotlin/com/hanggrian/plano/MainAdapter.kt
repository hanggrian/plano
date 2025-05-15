package com.hanggrian.plano

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import androidx.core.view.MenuCompat
import androidx.core.view.isNotEmpty
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import java.io.FileOutputStream

class MainAdapter(private val viewModel: MainViewModel, private val items: MutableList<MediaSize>) :
    RecyclerView.Adapter<MainAdapter.ViewHolder>(),
    MutableList<MediaSize> by items {
    private lateinit var activity: AppCompatActivity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        activity = parent.context as MainActivity
        return ViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_main, parent, false))
    }

    override fun getItemCount(): Int = size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mediaBox = get(position)
        if (holder.mediaContainer.isNotEmpty()) holder.mediaContainer.removeAllViews()
        holder.populate(mediaBox)
        holder.card.setOnCreateContextMenuListener { menu, _, _ ->
            MenuCompat.setGroupDividerEnabled(menu, true)

            menu.add(Menu.FIRST, 0, 0, R.string.rotate).setOnMenuItemClickListener {
                mediaBox.rotate()
                holder.populate(mediaBox)
                false
            }
            menu.add(Menu.FIRST, 0, 0, R.string.allow_flip_right).run {
                isCheckable = true
                isChecked = mediaBox.isAllowFlipRight
                setOnMenuItemClickListener {
                    mediaBox.isAllowFlipRight = !mediaBox.isAllowFlipRight
                    holder.populate(mediaBox)
                    false
                }
            }
            menu.add(Menu.FIRST, 0, 0, R.string.allow_flip_bottom).run {
                isCheckable = true
                isChecked = mediaBox.isAllowFlipBottom
                setOnMenuItemClickListener {
                    mediaBox.isAllowFlipBottom = !mediaBox.isAllowFlipBottom
                    holder.populate(mediaBox)
                    false
                }
            }

            menu.add(Menu.NONE, 0, 0, R.string.view_sizes).setOnMenuItemClickListener {
                SizesDialogFragment()
                    .apply {
                        arguments =
                            Bundle().apply {
                                putFloat(SizesDialogFragment.MAIN_WIDTH, mediaBox.mainWidth)
                                putFloat(SizesDialogFragment.MAIN_HEIGHT, mediaBox.mainHeight)
                                putFloat(
                                    SizesDialogFragment.REMAINING_WIDTH,
                                    mediaBox.remainingWidth,
                                )
                                putFloat(
                                    SizesDialogFragment.REMAINING_HEIGHT,
                                    mediaBox.remainingHeight,
                                )
                            }
                    }.show(
                        activity.supportFragmentManager,
                        SizesDialogFragment.TAG,
                    )
                false
            }
            menu.add(Menu.NONE, 0, 0, R.string.save_image).setOnMenuItemClickListener {
                val bitmap =
                    createBitmap(
                        holder.card.width,
                        holder.card.height,
                        Bitmap.Config.ARGB_8888,
                    )
                holder.card.draw(Canvas(bitmap))
                val file = ResultFile(ResultFile.DEVICE_DOCUMENTS)
                FileOutputStream(file)
                    .use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }

                Snackbar
                    .make(
                        holder.card,
                        activity.getString(R.string._save_image).format(file.name),
                        Snackbar.LENGTH_SHORT,
                    ).setAction(R.string.btn_show_image) {
                        activity.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                FileProvider
                                    .getUriForFile(
                                        activity,
                                        "${BuildConfig.APPLICATION_ID}.provider",
                                        file,
                                    ),
                            ).apply {
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            },
                        )
                    }.show()
                false
            }

            menu.add(Menu.FIRST, 0, 0, R.string.close).setOnMenuItemClickListener {
                delete(mediaBox)
                false
            }
        }
    }

    fun put(element: MediaSize) {
        if (isEmpty()) {
            viewModel.emptyData.value = false
        }
        add(element)
        notifyItemInserted(size - 1)
    }

    fun putAll(elements: Collection<MediaSize>) {
        if (isEmpty()) {
            viewModel.emptyData.value = false
        }
        val start = size + 1
        addAll(elements)
        notifyItemRangeInserted(start, size)
    }

    fun delete(element: MediaSize) {
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

    private fun ViewHolder.populate(mediaBox: MediaSize) {
        mediaText.text = "${mediaBox.width.clean()} \u00D7 ${mediaBox.height.clean()}"
        trimText.text = "${mediaBox.trimWidth.clean()} \u00D7 ${mediaBox.trimHeight.clean()}"
        countText.text = "${mediaBox.size} pcs"
        coverageText.text = "${mediaBox.coverage.clean()}%"

        if (isNotEmpty()) mediaContainer.removeAllViews()
        mediaContainer.addView(
            RelativeLayout(activity).also { media ->
                media.background = AppCompatResources.getDrawable(activity, mediaBackground)
                media.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                media.post {
                    media.layoutParams.height =
                        (media.width * mediaBox.height / mediaBox.width).toInt()
                    media.requestLayout()
                    media.post {
                        mediaBox.forEach { trimBox ->
                            media.addView(
                                View(activity).also { trim ->
                                    trim.background =
                                        AppCompatResources.getDrawable(activity, trimBackground)
                                    val widthRatio = media.width / mediaBox.width
                                    val heightRatio = media.height / mediaBox.height
                                    trim.layoutParams =
                                        RelativeLayout
                                            .LayoutParams(
                                                (trimBox.width * widthRatio).toInt(),
                                                (trimBox.height * heightRatio).toInt(),
                                            ).apply {
                                                addRule(RelativeLayout.ALIGN_PARENT_TOP)
                                                addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                                                leftMargin = (trimBox.x * widthRatio).toInt()
                                                topMargin = (trimBox.y * heightRatio).toInt()
                                            }
                                },
                            )
                        }
                    }
                }
            },
        )
    }

    private val mediaBackground: Int
        get() =
            when {
                viewModel.fillData.value!! && viewModel.thickData.value!! ->
                    R.drawable.bg_media_fill_thick
                !viewModel.fillData.value!! && viewModel.thickData.value!! ->
                    R.drawable.bg_media_unfill_thick
                viewModel.fillData.value!! && !viewModel.thickData.value!! ->
                    R.drawable.bg_media_fill_thin
                else -> R.drawable.bg_media_unfill_thin
            }

    private val trimBackground: Int
        get() =
            when {
                viewModel.fillData.value!! && viewModel.thickData.value!! ->
                    R.drawable.bg_trim_fill_thick
                !viewModel.fillData.value!! && viewModel.thickData.value!! ->
                    R.drawable.bg_trim_unfill_thick
                viewModel.fillData.value!! && !viewModel.thickData.value!! ->
                    R.drawable.bg_trim_fill_thin
                else -> R.drawable.bg_trim_unfill_thin
            }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: CardView = itemView.findViewById(R.id.card)
        val mediaContainer: ViewGroup = itemView.findViewById(R.id.container_media)
        val mediaText: TextView = itemView.findViewById(R.id.text_media)
        val trimText: TextView = itemView.findViewById(R.id.text_trim)
        val countText: TextView = itemView.findViewById(R.id.text_count)
        val coverageText: TextView = itemView.findViewById(R.id.text_coverage)
    }

    class SizesDialogFragment : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val layout = layoutInflater.inflate(R.layout.fragment_sizes, null)
            val bundle = requireArguments()

            layout
                .findViewById<TextView>(R.id.text_main_width1)
                .text = "${getString(R.string.main_width)}:"
            layout
                .findViewById<TextView>(R.id.text_main_width2)
                .text = bundle.getFloat(MAIN_WIDTH).clean()
            layout
                .findViewById<TextView>(R.id.text_main_height1)
                .text = "${getString(R.string.main_height)}:"
            layout
                .findViewById<TextView>(R.id.text_main_height2)
                .text = bundle.getFloat(MAIN_HEIGHT).clean()
            layout
                .findViewById<TextView>(R.id.text_remaining_width1)
                .text = "${getString(R.string.remaining_width)}:"
            layout
                .findViewById<TextView>(R.id.text_remaining_width2)
                .text = bundle.getFloat(REMAINING_WIDTH).clean()
            layout
                .findViewById<TextView>(R.id.text_remaining_height1)
                .text = "${getString(R.string.remaining_height)}:"
            layout
                .findViewById<TextView>(R.id.text_remaining_height2)
                .text = bundle.getFloat(REMAINING_HEIGHT).clean()

            return AlertDialog
                .Builder(requireContext())
                .setView(layout)
                .setTitle(R.string.sizes)
                .setNegativeButton(android.R.string.ok) { _, _ -> }
                .create()
        }

        companion object {
            const val TAG = "SizesDialogFragment"

            const val MAIN_WIDTH = "media_width"
            const val MAIN_HEIGHT = "media_height"
            const val REMAINING_WIDTH = "remaining_width"
            const val REMAINING_HEIGHT = "remaining_height"
        }
    }
}
