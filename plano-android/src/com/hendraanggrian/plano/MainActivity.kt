package com.hendraanggrian.plano

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.hendraanggrian.bundler.Bundler
import com.hendraanggrian.defaults.BindDefault
import com.hendraanggrian.defaults.DefaultsSaver
import com.hendraanggrian.defaults.bindDefaults
import com.jakewharton.processphoenix.ProcessPhoenix
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.ResourceBundle

class MainActivity : AppCompatActivity(), Resources {

    override lateinit var resourceBundle: ResourceBundle

    @BindDefault(R2.preference.language) @JvmField var language: String = Language.EN_US.fullCode
    private lateinit var menu: Menu
    private lateinit var adapter: MainAdapter
    private lateinit var saver: DefaultsSaver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        saver = bindDefaults()
        resourceBundle = Language.ofFullCode(language).toResourcesBundle()

        mediaBoxText.text = getString(R2.string.media_box)
        trimBoxText.text = getString(R2.string.trim_box)
        bleedBoxText.text = getString(R2.string.bleed)

        adapter = MainAdapter()
        recyclerView.adapter = adapter
        fab.setOnClickListener {
            when {
                mediaWidthText.value <= 0 || mediaHeightText.value <= 0 ||
                    trimWidthText.value <= 0 || trimHeightText.value <= 0 ->
                    Snackbar.make(fab, "incomplete", Snackbar.LENGTH_SHORT).show()
                else -> {
                    adapter.add(
                        0, Plano.calculate(
                            mediaWidthText.value, mediaHeightText.value,
                            trimWidthText.value, trimHeightText.value, bleedText.value
                        )
                    )
                    adapter.notifyDataSetChanged()
                    menu.findItem(R.id.clear).run { if (!isVisible) isVisible = true }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        this.menu = menu
        menu.findItem(R.id.clear).title = getString(R2.string.clear)
        menu.findItem(R.id.language).title = getString(R2.string.language)
        menu.findItem(R.id.about).title = getString(R2.string.about)
        Language.values().map { it.toLocale().displayLanguage }.forEach {
            menu.findItem(R.id.language).subMenu
                .add(it)
                .setCheckable(true)
                .isChecked = it == Language.ofFullCode(language).toLocale().displayLanguage
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clear -> {
                val size = adapter.size
                adapter.clear()
                adapter.notifyItemRangeRemoved(0, size)
                item.isVisible = false
                mediaWidthText.requestFocus()
                appBar.setExpanded(true)
            }
            R.id.about -> AboutDialogFragment()
                .also {
                    it.arguments = Bundler.wrapExtras(AboutDialogFragment::class.java, this)
                }
                .show(supportFragmentManager, null)
            R.id.language -> {
            }
            else -> {
                language = Language.ofDisplay(item.title.toString()).fullCode
                saver.save()
                GlobalScope.launch {
                    delay(500)
                    ProcessPhoenix.triggerRebirth(this@MainActivity)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val TextView.value: Double get() = text?.toString()?.toDoubleOrNull() ?: 0.0
}