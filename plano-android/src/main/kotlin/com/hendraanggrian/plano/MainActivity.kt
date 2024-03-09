package com.hendraanggrian.plano

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hendraanggrian.auto.bundler.BindState
import com.hendraanggrian.auto.bundler.restoreStates
import com.hendraanggrian.auto.bundler.saveStates
import com.hendraanggrian.auto.prefs.BindPreference
import com.hendraanggrian.auto.prefs.PreferencesSaver
import com.hendraanggrian.auto.prefs.android.bindPreferences
import com.hendraanggrian.plano.data.PlanoDatabase
import com.hendraanggrian.plano.data.saveRecentSizes
import com.hendraanggrian.plano.help.AboutDialogFragment
import com.hendraanggrian.plano.util.snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var db: PlanoDatabase
    private lateinit var viewModel: MainViewModel
    private lateinit var recyclerAdapter: MainAdapter
    private lateinit var saver: PreferencesSaver
    private lateinit var mediaPopupMenu: PopupMenu
    private lateinit var trimPopupMenu: PopupMenu
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            action.visibility = when {
                mediaWidthEdit.value > 0 && mediaHeightEdit.value > 0 &&
                    trimWidthEdit.value > 0 && trimHeightEdit.value > 0 -> View.VISIBLE

                else -> View.GONE
            }
        }
    }

    @JvmField @BindPreference("theme") var theme2 = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    @JvmField @BindPreference("is_fill") var isFill = false
    @JvmField @BindPreference("is_thick") var isThick = false
    @JvmField @BindPreference("media_width") var mediaWidth = 0f
    @JvmField @BindPreference("media_height") var mediaHeight = 0f
    @JvmField @BindPreference("trim_width") var trimWidth = 0f
    @JvmField @BindPreference("trim_height") var trimHeight = 0f
    @JvmField @BindPreference("gap_horizontal") var gapHorizontal = 0f
    @JvmField @BindPreference("gap_vertical") var gapVertical = 0f
    @JvmField @BindPreference("allow_flip_column") var allowFlipColumn = false
    @JvmField @BindPreference("allow_flip_row") var allowFlipRow = false

    @JvmField @BindState var recyclerItems: ArrayList<MediaSize>? = null

    lateinit var toolbar: Toolbar
    lateinit var appBar: AppBarLayout
    lateinit var mediaWidthEdit: EditText
    lateinit var mediaHeightEdit: EditText
    lateinit var trimWidthEdit: EditText
    lateinit var trimHeightEdit: EditText
    lateinit var gapHorizontalEdit: EditText
    lateinit var gapVerticalEdit: EditText
    lateinit var mediaMoreButton: ImageButton
    lateinit var trimMoreButton: ImageButton
    lateinit var allowFlipRightCheck: CheckBox
    lateinit var allowFlipBottomCheck: CheckBox
    lateinit var emptyText: TextView
    lateinit var recycler: RecyclerView
    lateinit var action: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        appBar = findViewById(R.id.appbar)
        mediaWidthEdit = findViewById(R.id.edit_media_width)
        mediaHeightEdit = findViewById(R.id.edit_media_height)
        trimWidthEdit = findViewById(R.id.edit_trim_width)
        trimHeightEdit = findViewById(R.id.edit_trim_height)
        gapHorizontalEdit = findViewById(R.id.edit_gap_horizontal)
        gapVerticalEdit = findViewById(R.id.edit_gap_vertical)
        mediaMoreButton = findViewById(R.id.button_media_more)
        trimMoreButton = findViewById(R.id.button_trim_more)
        allowFlipRightCheck = findViewById(R.id.check_allow_flip_right)
        allowFlipBottomCheck = findViewById(R.id.check_allow_flip_bottom)
        emptyText = findViewById(R.id.text_empty)
        recycler = findViewById(R.id.recycler)
        action = findViewById(R.id.action)

        setSupportActionBar(toolbar)
        toolbar.overflowIcon = ContextCompat.getDrawable(this, R.drawable.btn_overflow)

        db = PlanoDatabase.getInstance(this)
        saver = bindPreferences()
        viewModel = ViewModelProvider(this).get()
        viewModel.fillData.value = isFill
        viewModel.thickData.value = isThick

        mediaWidthEdit.addTextChangedListener(textWatcher)
        mediaHeightEdit.addTextChangedListener(textWatcher)
        trimWidthEdit.addTextChangedListener(textWatcher)
        trimHeightEdit.addTextChangedListener(textWatcher)
        gapHorizontalEdit.addTextChangedListener(textWatcher)
        gapVerticalEdit.addTextChangedListener(textWatcher)

        mediaPopupMenu =
            PopupMenu(this@MainActivity, mediaMoreButton).prepare(mediaWidthEdit, mediaHeightEdit)
        trimPopupMenu =
            PopupMenu(this@MainActivity, trimMoreButton).prepare(trimWidthEdit, trimHeightEdit)
        updatePaperSizes()

        mediaWidthEdit.setText(mediaWidth.clean())
        mediaHeightEdit.setText(mediaHeight.clean())
        trimWidthEdit.setText(trimWidth.clean())
        trimHeightEdit.setText(trimHeight.clean())
        gapHorizontalEdit.setText(gapHorizontal.clean())
        gapVerticalEdit.setText(gapVertical.clean())
        allowFlipRightCheck.isChecked = allowFlipColumn
        allowFlipBottomCheck.isChecked = allowFlipRow

        if (savedInstanceState != null) {
            restoreStates(savedInstanceState)
        } else {
            recyclerItems = arrayListOf()
        }
        recyclerAdapter = MainAdapter(viewModel, recyclerItems!!)
        recycler.adapter = recyclerAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        adjust()
        saver.save()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveStates(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        val closeAllItem = menu.findItem(R.id.item_close_all)
        val backgroundItem = menu.findItem(R.id.item_background)
        val borderItem = menu.findItem(R.id.item_border)

        viewModel.emptyData.observe(this) {
            emptyText.visibility = if (it) View.VISIBLE else View.GONE
            closeAllItem.isVisible = !it
            when {
                it -> {
                    appBar.setExpanded(true)
                    mediaWidthEdit.requestFocus()
                }

                else -> recycler.scrollToPosition(recyclerAdapter.size - 1)
            }
        }
        viewModel.fillData.observe(this) {
            isFill = it
            backgroundItem.setIcon(
                when {
                    isFill -> R.drawable.btn_background_unfill
                    else -> R.drawable.btn_background_fill
                }
            )
            recycler.adapter!!.notifyDataSetChanged()
        }
        viewModel.thickData.observe(this) {
            isThick = it
            borderItem.setIcon(
                when {
                    isThick -> R.drawable.btn_border_thin
                    else -> R.drawable.btn_border_thick
                }
            )
            recycler.adapter!!.notifyDataSetChanged()
        }

        menu.findItem(
            when (theme2) {
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> R.id.item_theme_system
                AppCompatDelegate.MODE_NIGHT_NO -> R.id.item_theme_light
                else -> R.id.item_theme_dark
            }
        ).isChecked = true
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_close_all -> {
                val temp = recyclerAdapter.toList()
                recyclerAdapter.deleteAll()
                recycler.snackbar(
                    getString(R.string._boxes_cleared),
                    getString(R.string.btn_undo)
                ) {
                    recyclerAdapter.putAll(temp)
                }
            }

            R.id.item_background -> viewModel.fillData.value = !isFill
            R.id.item_border -> viewModel.thickData.value = !isThick
            R.id.item_clear_recent_sizes -> {
                runBlocking(Dispatchers.IO) {
                    db.recentMedia().deleteAll()
                    db.recentTrim().deleteAll()
                }
                updatePaperSizes()
            }

            R.id.item_theme_system, R.id.item_theme_light, R.id.item_theme_dark -> {
                theme2 = when (item.itemId) {
                    R.id.item_theme_system -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    R.id.item_theme_light -> AppCompatDelegate.MODE_NIGHT_NO
                    else -> AppCompatDelegate.MODE_NIGHT_YES
                }
                saver.save()
                AppCompatDelegate.setDefaultNightMode(theme2)
            }

            R.id.item_about -> AboutDialogFragment().show(supportFragmentManager, null)
        }
        return super.onOptionsItemSelected(item)
    }

    fun moreSizes(view: View): Unit =
        (if (view == mediaMoreButton) mediaPopupMenu else trimPopupMenu).show()

    fun calculate(view: View) {
        adjust()
        getSystemService<InputMethodManager>()!!
            .hideSoftInputFromWindow(action.applicationWindowToken, 0)
        val mediaSize = MediaSize(mediaWidth, mediaHeight)
        mediaSize.populate(
            trimWidth,
            trimHeight,
            gapHorizontal,
            gapVertical,
            allowFlipColumn,
            allowFlipRow
        )
        recyclerAdapter.put(mediaSize)

        runBlocking {
            launch(Dispatchers.IO) {
                saveRecentSizes(
                    mediaWidth,
                    mediaHeight,
                    trimWidth,
                    trimHeight
                )
            }.join()
            updatePaperSizes()
        }
    }

    private fun adjust() {
        mediaWidth = mediaWidthEdit.value; mediaHeight = mediaHeightEdit.value
        trimWidth = trimWidthEdit.value; trimHeight = trimHeightEdit.value
        gapHorizontal = gapHorizontalEdit.value; gapVertical = gapVerticalEdit.value
        // gaplink
        allowFlipColumn = allowFlipRightCheck.isChecked; allowFlipRow =
            allowFlipBottomCheck.isChecked
    }

    private fun updatePaperSizes() {
        runBlocking {
            val history =
                withContext(Dispatchers.IO) { db.recentMedia().all() to db.recentTrim().all() }
            val (mediaSizes, trimSizes) = history
            mediaPopupMenu.updatePaperSizes { mediaSizes }
            trimPopupMenu.updatePaperSizes { trimSizes }
        }
    }

    private fun PopupMenu.updatePaperSizes(historyProvider: () -> Iterable<Size>) {
        menu.clear()
        // history
        historyProvider().reversed().forEach { menu.add(it.dimension) }
        // standard paper sizes
        menu.addSubMenu(getString(R.string.a_series)).run {
            StandardSize.SERIES_A.forEach { add(it.extendedTitle) }
        }
        menu.addSubMenu(getString(R.string.b_series)).run {
            StandardSize.SERIES_B.forEach { add(it.extendedTitle) }
        }
        menu.addSubMenu(getString(R.string.c_series)).run {
            StandardSize.SERIES_C.forEach { add(it.extendedTitle) }
        }
        menu.addSubMenu(getString(R.string.f_series)).run {
            StandardSize.SERIES_F.forEach { add(it.extendedTitle) }
        }
    }

    private fun PopupMenu.prepare(widthEdit: EditText, heightEdit: EditText): PopupMenu {
        // messy custom implementation
        setOnMenuItemClickListener { menu ->
            if (menu.title?.toString().orEmpty().none { it.isDigit() }) {
                return@setOnMenuItemClickListener false
            }
            val s = menu.title.toString()
            widthEdit.setText(s.substring(s.indexOf('\t') + 1, s.indexOf(" x ")))
            heightEdit.setText(s.substringAfter(" x "))
            true
        }
        return this
    }

    private val TextView.value: Float get() = text?.toString()?.toFloatOrNull() ?: 0f
}
