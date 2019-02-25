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

class MainActivity : AppCompatActivity() {

    @BindDefault(R2.preference.language) lateinit var language: String
    private lateinit var menu: Menu
    private lateinit var adapter: MainAdapter
    private lateinit var saver: DefaultsSaver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        saver = bindDefaults()

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
        Language.values().forEach {
            val item = menu[R.id.language].subMenu
                .add(it.toLocale().displayLanguage)
                .setCheckable(true)
            if (item.title == language) {
                item.isChecked = true
            }
        }
        this.menu = menu
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