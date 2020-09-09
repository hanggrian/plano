package com.hendraanggrian.plano

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.view.children
import androidx.core.view.doOnLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.lifecycle.observe
import com.hendraanggrian.plano.help.AboutDialogFragment
import com.hendraanggrian.plano.util.snackbar
import com.hendraanggrian.prefy.BindPreference
import com.hendraanggrian.prefy.PreferencesSaver
import com.hendraanggrian.prefy.Prefy
import com.hendraanggrian.prefy.android.bind
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: MainAdapter
    private lateinit var saver: PreferencesSaver
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            fab.visibility = when {
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
    @JvmField @BindPreference("bleed") var bleed = 0f
    @JvmField @BindPreference("allow_flip_column") var allowFlipColumn = false
    @JvmField @BindPreference("allow_flip_row") var allowFlipRow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        saver = Prefy.bind(this)
        viewModel = ViewModelProvider(this).get()
        viewModel.fillData.value = isFill
        viewModel.thickData.value = isThick

        mediaWidthEdit.addTextChangedListener(textWatcher); mediaHeightEdit.addTextChangedListener(textWatcher)
        trimWidthEdit.addTextChangedListener(textWatcher); trimHeightEdit.addTextChangedListener(textWatcher)
        bleedEdit.addTextChangedListener(textWatcher)

        toolbar.overflowIcon = ContextCompat.getDrawable(this, R.drawable.btn_overflow)
        mediaToolbar.bindPaperSizes(); trimToolbar.bindPaperSizes()
        mediaText.doOnLayout {
            trimText.width = mediaText.width
            bleedText.width = mediaText.width
        }

        mediaWidthEdit.setText(mediaWidth.clean()); mediaHeightEdit.setText(mediaHeight.clean())
        trimWidthEdit.setText(trimWidth.clean()); trimHeightEdit.setText(trimHeight.clean())
        bleedEdit.setText(bleed.clean())
        allowFlipColumnCheck.isChecked = allowFlipColumn; allowFlipRowCheck.isChecked = allowFlipRow

        adapter = MainAdapter(viewModel)
        recycler.adapter = adapter

        fab.setOnClickListener {
            adjust()
            getSystemService<InputMethodManager>()!!.hideSoftInputFromWindow(fab.applicationWindowToken, 0)
            val mediaBox = MediaBox2(mediaWidth.toDouble(), mediaHeight.toDouble())
            mediaBox.populate(
                trimWidth.toDouble(),
                trimHeight.toDouble(),
                bleed.toDouble(),
                allowFlipColumn,
                allowFlipRow
            )
            adapter.put(mediaBox)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        adjust()
        saver.save()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        val closeAllItem = menu.findItem(R.id.closeAllItem)
        val backgroundItem = menu.findItem(R.id.backgroundItem)
        val borderItem = menu.findItem(R.id.borderItem)

        viewModel.emptyData.observe(this) {
            emptyText.visibility = if (it) View.VISIBLE else View.GONE
            closeAllItem.isVisible = !it
            when {
                it -> {
                    appBar.setExpanded(true)
                    mediaWidthEdit.requestFocus()
                }
                else -> recycler.scrollToPosition(adapter.size - 1)
            }
        }
        viewModel.fillData.observe(this) {
            isFill = it
            backgroundItem.setIcon(if (isFill) R.drawable.btn_background_unfill else R.drawable.btn_background_fill)
            recycler.adapter!!.notifyDataSetChanged()
        }
        viewModel.thickData.observe(this) {
            isThick = it
            borderItem.setIcon(if (isThick) R.drawable.btn_border_thin else R.drawable.btn_border_thick)
            recycler.adapter!!.notifyDataSetChanged()
        }

        menu.findItem(
            when (theme2) {
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> R.id.themeSystemItem
                AppCompatDelegate.MODE_NIGHT_NO -> R.id.themeLightItem
                else -> R.id.themeDarkItem
            }
        ).isChecked = true
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.closeAllItem -> {
                val temp = adapter.toList()
                adapter.deleteAll()
                recycler.snackbar(getString(R.string._boxes_cleared), getString(R.string.btn_undo)) {
                    adapter.putAll(temp)
                }
            }
            R.id.backgroundItem -> viewModel.fillData.value = !isFill
            R.id.borderItem -> viewModel.thickData.value = !isThick
            R.id.themeSystemItem, R.id.themeLightItem, R.id.themeDarkItem -> {
                theme2 = when (item.itemId) {
                    R.id.themeSystemItem -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    R.id.themeLightItem -> AppCompatDelegate.MODE_NIGHT_NO
                    else -> AppCompatDelegate.MODE_NIGHT_YES
                }
                saver.save()
                AppCompatDelegate.setDefaultNightMode(theme2)
            }
            R.id.aboutItem -> AboutDialogFragment().show(supportFragmentManager, null)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun adjust() {
        mediaWidth = mediaWidthEdit.value; mediaHeight = mediaHeightEdit.value
        trimWidth = trimWidthEdit.value; trimHeight = trimHeightEdit.value
        bleed = bleedEdit.value
        allowFlipColumn = allowFlipColumnCheck.isChecked; allowFlipRow = allowFlipRowCheck.isChecked
    }

    private fun Toolbar.bindPaperSizes() {
        menu.addSubMenu(getString(R.string.a_series)).run { PaperSize.SERIES_A.forEach { add(it.extendedTitle) } }
        menu.addSubMenu(getString(R.string.b_series)).run { PaperSize.SERIES_B.forEach { add(it.extendedTitle) } }
        menu.addSubMenu(getString(R.string.c_series)).run { PaperSize.SERIES_C.forEach { add(it.extendedTitle) } }
        menu.addSubMenu(getString(R.string.f_series)).run { PaperSize.SERIES_F.forEach { add(it.extendedTitle) } }
        setOnMenuItemClickListener { menu ->
            if (menu.title.none { it.isDigit() }) {
                return@setOnMenuItemClickListener false
            }
            val s = menu.title.toString()
            (children.first() as ViewGroup).children.filterIsInstance<EditText>().forEachIndexed { index, t ->
                t.setText(
                    when (index) {
                        0 -> s.substring(s.indexOf('\t') + 1, s.indexOf(" x "))
                        else -> s.substringAfter(" x ")
                    }
                )
            }
            true
        }
    }

    private val TextView.value: Float get() = text?.toString()?.toFloatOrNull() ?: 0f
}
