package com.hendraanggrian.plano

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import com.hendraanggrian.bundler.Bundler
import com.hendraanggrian.defaults.BindDefault
import com.hendraanggrian.defaults.DefaultsSaver
import com.hendraanggrian.defaults.SharedPreferencesDefaults
import com.hendraanggrian.defaults.bindDefaults
import com.hendraanggrian.defaults.toDefaults
import com.hendraanggrian.plano.dialog.AboutDialogFragment
import com.jakewharton.processphoenix.ProcessPhoenix
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ResourceBundle

class MainActivity : AppCompatActivity(), Resources {

    override lateinit var resourceBundle: ResourceBundle

    @BindDefault @JvmField var language: String = Language.EN_US.fullCode

    private lateinit var clearMenu: MenuItem
    private lateinit var defaults: SharedPreferencesDefaults
    private lateinit var saver: DefaultsSaver

    private val emptyObservable = ObservableBoolean()
    private val adapter = MainAdapter(emptyObservable)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        defaults = toDefaults()
        saver = defaults.bindDefaults(this)
        resourceBundle = Language.ofFullCode(language).toResourcesBundle()

        mediaBoxText.text = getString(R2.string.media_box)
        trimBoxText.text = getString(R2.string.trim_box)
        bleedBoxText.text = getString(R2.string.bleed)
        emptyText.text = getString(R2.string.no_content)

        defaults[R2.preference.media_width]?.let { mediaWidthText.setText(it) }
        defaults[R2.preference.media_height]?.let { mediaHeightText.setText(it) }
        defaults[R2.preference.trim_width]?.let { trimWidthText.setText(it) }
        defaults[R2.preference.trim_height]?.let { trimHeightText.setText(it) }
        defaults[R2.preference.bleed]?.let { bleedText.setText(it) }

        emptyObservable.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable, propertyId: Int) {
                when {
                    (sender as ObservableBoolean).get() -> {
                        emptyText.visibility = View.VISIBLE
                        clearMenu.isVisible = false
                        appBar.setExpanded(true)
                        mediaWidthText.requestFocus()
                    }
                    else -> {
                        emptyText.visibility = View.GONE
                        clearMenu.isVisible = true
                        recyclerView.scrollToPosition(adapter.size - 1)
                    }
                }
            }
        })

        recyclerView.adapter = adapter
        fab.setOnClickListener {
            when {
                mediaWidthText.value <= 0 || mediaHeightText.value <= 0 ||
                    trimWidthText.value <= 0 || trimHeightText.value <= 0 ->
                    recyclerView.snackbar(resourceBundle.getString(R2.string._incomplete))
                else -> {
                    defaults {
                        it[R2.preference.media_width] = mediaWidthText.text.toString()
                        it[R2.preference.media_height] = mediaHeightText.text.toString()
                        it[R2.preference.trim_width] = trimWidthText.text.toString()
                        it[R2.preference.trim_height] = trimHeightText.text.toString()
                        when {
                            bleedText.value > 0 -> it[R2.preference.bleed] =
                                bleedText.value.toString()
                            else -> it -= R2.preference.bleed
                        }
                    }

                    getSystemService<InputMethodManager>()!!
                        .hideSoftInputFromWindow(fab.applicationWindowToken, 0)
                    adapter.put(
                        Plano.calculate(
                            mediaWidthText.value, mediaHeightText.value,
                            trimWidthText.value, trimHeightText.value, bleedText.value
                        )
                    )
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        clearMenu = menu.findItem(R.id.clear).apply {
            title = getString(R2.string.clear)
        }
        menu.findItem(R.id.language).title = getString(R2.string.language)
        menu.findItem(R.id.checkForUpdate).title = getString(R2.string.check_for_update)
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
                val temp = adapter.toList()
                adapter.removeAll()
                recyclerView.snackbar(
                    getString(R2.string._boxes_cleared),
                    getString(R2.string.btn_undo)
                ) {
                    adapter.putAll(temp)
                }
            }
            R.id.language -> {
            }
            R.id.checkForUpdate -> GlobalScope.launch(Dispatchers.Main) {
                val release = withContext(Dispatchers.IO) {
                    GitHubApi.getLatestRelease()
                }
                when {
                    release.isNewerThan(BuildConfig.VERSION) -> recyclerView.longSnackbar(
                        getString(R2.string._update_available).format(BuildConfig.VERSION),
                        getString(R2.string.btn_download)
                    ) {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(release.assets.first {
                                    it.name.endsWith("apk")
                                }.downloadUrl)
                            )
                        )
                    }
                    else -> recyclerView.longSnackbar(getString(R2.string._update_unavailable))
                }
            }
            R.id.about -> AboutDialogFragment()
                .also {
                    it.arguments = Bundler.wrapExtras(AboutDialogFragment::class.java, this)
                }
                .show(supportFragmentManager, null)
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