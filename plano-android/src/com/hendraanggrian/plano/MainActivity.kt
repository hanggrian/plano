package com.hendraanggrian.plano

import android.content.Intent
import android.net.Uri
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
import com.hendraanggrian.bundler.Bundler
import com.hendraanggrian.plano.controls.longSnackbar
import com.hendraanggrian.plano.controls.snackbar
import com.hendraanggrian.plano.dialogs.AboutDialogFragment
import com.hendraanggrian.plano.util.clean
import com.hendraanggrian.prefs.BindPref
import com.hendraanggrian.prefs.Prefs
import com.hendraanggrian.prefs.android.bind
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private var clearItem: MenuItem? = null
    private lateinit var adapter: MainAdapter
    private lateinit var saver: Prefs.Saver
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            viewModel.validData.value = mediaWidthEdit.value <= 0 || mediaHeightEdit.value <= 0 ||
                trimWidthEdit.value <= 0 || trimHeightEdit.value <= 0
        }
    }

    @JvmField @BindPref("theme") var theme2 = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    @JvmField @BindPref("media_width") var mediaWidth = 0f
    @JvmField @BindPref("media_height") var mediaHeight = 0f
    @JvmField @BindPref("trim_width") var trimWidth = 0f
    @JvmField @BindPref("trim_height") var trimHeight = 0f
    @JvmField @BindPref("bleed") var bleed = 0f
    @JvmField @BindPref("allow_flip") var allowFlip = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        saver = Prefs.bind(this)

        viewModel = ViewModelProvider(this).get()
        viewModel.validData.observe(this) { isValid ->
            when {
                isValid && !fab.isShown -> fab.show()
                !isValid && fab.isShown -> fab.hide()
            }
        }
        viewModel.emptyData.observe(this) { isEmpty ->
            emptyText.visibility = if (isEmpty) View.VISIBLE else View.GONE
            clearItem?.isVisible = !isEmpty
            when {
                isEmpty -> {
                    appBar.setExpanded(true)
                    mediaWidthEdit.requestFocus()
                }
                else -> recyclerView.scrollToPosition(adapter.size - 1)
            }
        }

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

        adapter = MainAdapter(viewModel.emptyData)
        recyclerView.adapter = adapter
        fab.setOnClickListener {
            mediaWidth = mediaWidthEdit.value
            mediaHeight = mediaHeightEdit.value
            trimWidth = trimWidthEdit.value
            trimHeight = trimHeightEdit.value
            bleed = bleedEdit.value
            saver.save()

            getSystemService<InputMethodManager>()!!.hideSoftInputFromWindow(fab.applicationWindowToken, 0)
            adapter.put(MediaBox(mediaWidth.toDouble(), mediaHeight.toDouble()).apply {
                populate(trimWidth, trimHeight, bleed.toDouble(), allowFlip)
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        clearItem = menu.findItem(R.id.closeAllItem)
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
                adapter.removeAll()
                recyclerView.snackbar(getString(R.string._boxes_cleared), getString(R.string.btn_undo)) {
                    adapter.putAll(temp)
                }
            }
            R.id.checkForUpdateItem -> GlobalScope.launch(Dispatchers.Main) {
                val release = withContext(Dispatchers.IO) { GitHubApi.getRelease(".apk") }
                when {
                    release.isNewerThan(BuildConfig.VERSION_NAME) -> recyclerView.longSnackbar(
                        getString(R.string._update_available).format(BuildConfig.VERSION_NAME),
                        getString(R.string.btn_download)
                    ) {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(release.assets.first { it.name.endsWith("apk") }.downloadUrl)
                            )
                        )
                    }
                    else -> recyclerView.longSnackbar(getString(R.string._update_unavailable))
                }
            }
            R.id.themeSystemItem, R.id.themeLightItem, R.id.themeDarkItem -> {
                theme2 = when (item.itemId) {
                    R.id.themeSystemItem -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    R.id.themeLightItem -> AppCompatDelegate.MODE_NIGHT_NO
                    else -> AppCompatDelegate.MODE_NIGHT_YES
                }
                saver.save()
                AppCompatDelegate.setDefaultNightMode(theme2)
            }
            R.id.aboutItem -> AboutDialogFragment()
                .also { it.arguments = Bundler.wrapExtras(AboutDialogFragment::class.java, this) }
                .show(supportFragmentManager, null)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun Toolbar.bindPaperSizes() {
        menu.addSubMenu(getString(R.string.a_series)).apply { PaperSize.SERIES_A.forEach { add(it.title) } }
        menu.addSubMenu(getString(R.string.b_series)).apply { PaperSize.SERIES_B.forEach { add(it.title) } }
        menu.addSubMenu(getString(R.string.c_series)).apply { PaperSize.SERIES_C.forEach { add(it.title) } }
        menu.addSubMenu(getString(R.string.f_series)).apply { PaperSize.SERIES_F.forEach { add(it.title) } }
        setOnMenuItemClickListener { menu ->
            if (menu.title.none { it.isDigit() }) return@setOnMenuItemClickListener false
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
