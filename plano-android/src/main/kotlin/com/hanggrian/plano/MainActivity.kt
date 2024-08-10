package com.hanggrian.plano

import android.content.SharedPreferences
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
import androidx.core.content.edit
import androidx.core.content.getSystemService
import androidx.core.view.MenuCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hanggrian.plano.data.PlanoDatabase
import com.hanggrian.plano.data.saveRecentSizes
import com.hanggrian.plano.help.AboutDialogFragment
import com.hanggrian.plano.prefs.ALLOW_FLIP_COLUMN
import com.hanggrian.plano.prefs.ALLOW_FLIP_ROW
import com.hanggrian.plano.prefs.GAP_HORIZONTAL
import com.hanggrian.plano.prefs.GAP_VERTICAL
import com.hanggrian.plano.prefs.IS_FILL
import com.hanggrian.plano.prefs.IS_THICK
import com.hanggrian.plano.prefs.MEDIA_HEIGHT
import com.hanggrian.plano.prefs.MEDIA_WIDTH
import com.hanggrian.plano.prefs.THEME
import com.hanggrian.plano.prefs.TRIM_HEIGHT
import com.hanggrian.plano.prefs.TRIM_WIDTH
import com.hanggrian.plano.util.snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var db: PlanoDatabase
    private lateinit var viewModel: MainViewModel
    private lateinit var recyclerAdapter: MainAdapter
    private lateinit var prefs: SharedPreferences
    private lateinit var mediaPopupMenu: PopupMenu
    private lateinit var trimPopupMenu: PopupMenu
    private val textWatcher =
        object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                action.visibility =
                    when {
                        mediaWidthEdit.value > 0 &&
                            mediaHeightEdit.value > 0 &&
                            trimWidthEdit.value > 0 &&
                            trimHeightEdit.value > 0 -> View.VISIBLE
                        else -> View.GONE
                    }
            }
        }

    var recyclerItems: ArrayList<MediaSize>? = null

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
        toolbar.overflowIcon = ContextCompat.getDrawable(this, R.drawable.ic_overflow)

        db = PlanoDatabase.getInstance(this)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        viewModel = ViewModelProvider(this).get()
        viewModel.fillData.value = prefs.getBoolean(IS_FILL, false)
        viewModel.thickData.value = prefs.getBoolean(IS_THICK, false)

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
        MenuCompat.setGroupDividerEnabled(mediaPopupMenu.menu, true)
        MenuCompat.setGroupDividerEnabled(trimPopupMenu.menu, true)
        updatePaperSizes()

        mediaWidthEdit.setText(prefs.getFloat(MEDIA_WIDTH, 0f).clean())
        mediaHeightEdit.setText(prefs.getFloat(MEDIA_HEIGHT, 0f).clean())
        trimWidthEdit.setText(prefs.getFloat(TRIM_WIDTH, 0f).clean())
        trimHeightEdit.setText(prefs.getFloat(TRIM_HEIGHT, 0f).clean())
        gapHorizontalEdit.setText(prefs.getFloat(GAP_HORIZONTAL, 0f).clean())
        gapVerticalEdit.setText(prefs.getFloat(GAP_VERTICAL, 0f).clean())
        allowFlipRightCheck.isChecked = prefs.getBoolean(ALLOW_FLIP_COLUMN, false)
        allowFlipBottomCheck.isChecked = prefs.getBoolean(ALLOW_FLIP_ROW, false)

        recyclerItems =
            (savedInstanceState?.getSerializable(ITEMS) as? ArrayList<MediaSize>)
                ?: arrayListOf()
        recyclerAdapter = MainAdapter(viewModel, recyclerItems!!)
        recycler.adapter = recyclerAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        prefs.edit {
            putFloat(MEDIA_WIDTH, mediaWidthEdit.value)
            putFloat(MEDIA_HEIGHT, mediaHeightEdit.value)
            putFloat(TRIM_WIDTH, trimWidthEdit.value)
            putFloat(TRIM_HEIGHT, trimHeightEdit.value)
            putFloat(GAP_HORIZONTAL, gapHorizontalEdit.value)
            putFloat(GAP_VERTICAL, gapVerticalEdit.value)
            putBoolean(ALLOW_FLIP_COLUMN, allowFlipRightCheck.isChecked)
            putBoolean(ALLOW_FLIP_ROW, allowFlipBottomCheck.isChecked)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(ITEMS, recyclerItems)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        recyclerItems = savedInstanceState.getSerializable(ITEMS) as ArrayList<MediaSize>
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        MenuCompat.setGroupDividerEnabled(menu, true)

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
            prefs.edit { putBoolean(IS_FILL, it) }
            backgroundItem.setIcon(
                when {
                    it -> R.drawable.ic_background_unfill
                    else -> R.drawable.ic_background_fill
                },
            )
            recycler.adapter!!.notifyDataSetChanged()
        }
        viewModel.thickData.observe(this) {
            prefs.edit { putBoolean(IS_THICK, it) }
            borderItem.setIcon(
                when {
                    it -> R.drawable.ic_border_thin
                    else -> R.drawable.ic_border_thick
                },
            )
            recycler.adapter!!.notifyDataSetChanged()
        }

        menu
            .findItem(
                when (prefs.getInt(THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)) {
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> R.id.item_theme_system
                    AppCompatDelegate.MODE_NIGHT_NO -> R.id.item_theme_light
                    else -> R.id.item_theme_dark
                },
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
                    getString(R.string.btn_undo),
                ) {
                    recyclerAdapter.putAll(temp)
                }
            }
            R.id.item_background ->
                viewModel.fillData.value = !prefs.getBoolean(IS_FILL, false)
            R.id.item_border ->
                viewModel.thickData.value = !prefs.getBoolean(IS_THICK, false)
            R.id.item_clear_recent_sizes -> {
                runBlocking(Dispatchers.IO) {
                    db.recentMedia().deleteAll()
                    db.recentTrim().deleteAll()
                }
                updatePaperSizes()
            }
            R.id.item_theme_system, R.id.item_theme_light, R.id.item_theme_dark -> {
                val theme =
                    when (item.itemId) {
                        R.id.item_theme_system -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                        R.id.item_theme_light -> AppCompatDelegate.MODE_NIGHT_NO
                        else -> AppCompatDelegate.MODE_NIGHT_YES
                    }
                prefs.edit { putInt(THEME, theme) }
                AppCompatDelegate.setDefaultNightMode(theme)
            }
            R.id.item_about -> AboutDialogFragment().show(supportFragmentManager, null)
        }
        return super.onOptionsItemSelected(item)
    }

    fun moreSizes(view: View): Unit =
        (if (view == mediaMoreButton) mediaPopupMenu else trimPopupMenu).show()

    fun calculate(view: View) {
        getSystemService<InputMethodManager>()!!
            .hideSoftInputFromWindow(action.applicationWindowToken, 0)
        val mediaSize = MediaSize(mediaWidthEdit.value, mediaHeightEdit.value)
        mediaSize.populate(
            trimWidthEdit.value,
            trimHeightEdit.value,
            gapHorizontalEdit.value,
            gapVerticalEdit.value,
            allowFlipRightCheck.isChecked,
            allowFlipBottomCheck.isChecked,
        )
        recyclerAdapter.put(mediaSize)

        runBlocking {
            launch(Dispatchers.IO) {
                saveRecentSizes(
                    mediaWidthEdit.value,
                    mediaHeightEdit.value,
                    trimWidthEdit.value,
                    trimHeightEdit.value,
                )
            }.join()
            updatePaperSizes()
        }
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
        historyProvider().reversed().forEach { menu.add(Menu.FIRST, 0, 0, it.dimension) }
        // standard paper sizes
        menu.addSubMenu(Menu.NONE, 0, 0, getString(R.string.a_series)).run {
            StandardSize.Companion.SERIES_A.forEach { add(it.extendedTitle) }
        }
        menu.addSubMenu(Menu.NONE, 0, 0, getString(R.string.b_series)).run {
            StandardSize.Companion.SERIES_B.forEach { add(it.extendedTitle) }
        }
        menu.addSubMenu(Menu.NONE, 0, 0, getString(R.string.c_series)).run {
            StandardSize.Companion.SERIES_C.forEach { add(it.extendedTitle) }
        }
        menu.addSubMenu(Menu.NONE, 0, 0, getString(R.string.f_series)).run {
            StandardSize.Companion.SERIES_F.forEach { add(it.extendedTitle) }
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

    private companion object {
        const val ITEMS = "items"
    }
}
