package com.hendraanggrian.plano

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.google.android.material.snackbar.Snackbar
import com.hendraanggrian.defaults.BindDefault
import com.hendraanggrian.defaults.DefaultsSaver
import com.hendraanggrian.defaults.bindDefaults
import com.jakewharton.processphoenix.ProcessPhoenix
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ResourceBundle

class MainActivity : AppCompatActivity(), Resources {

    override lateinit var resourceBundle: ResourceBundle

    @BindDefault(R2.preference.language) var language: String = Language.EN_US.fullCode
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
                else -> adapter.add(
                    Plano.getTrimSizes(
                        mediaWidthText.value, mediaHeightText.value,
                        trimWidthText.value, trimHeightText.value, bleedText.value
                    )
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        menu[R.id.clear].title = getString(R2.string.clear)
        menu[R.id.language].title = getString(R2.string.language)
        menu[R.id.about].title = getString(R2.string.about)
        Language.values().map { it.toLocale().displayLanguage }.forEach {
            menu[R.id.language].subMenu
                .add(it)
                .setCheckable(true)
                .isChecked = it == language
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clear -> adapter.clear()
            R.id.about -> AboutDialogFragment().show(supportFragmentManager, null)
            else -> {
                language = item.title.toString()
                saver.save()
                ProcessPhoenix.triggerRebirth(this)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val TextView.value: Double get() = text?.toString()?.toDoubleOrNull() ?: 0.0
}