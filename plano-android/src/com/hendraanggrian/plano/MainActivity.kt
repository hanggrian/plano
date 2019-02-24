package com.hendraanggrian.plano

import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var menu: Menu
    private val adapter = MainAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        recyclerView.adapter = adapter
        fab.setOnClickListener {
            when {
                mediaWidthText.value <= 0 || mediaHeightText.value <= 0 ||
                    trimWidthText.value <= 0 || trimHeightText.value <= 0 ->
                    Snackbar.make(fab, "incomplete", Snackbar.LENGTH_SHORT).show()
                else -> {
                    adapter += Plano.getTrimSizes(
                        mediaWidthText.value, mediaHeightText.value,
                        trimWidthText.value, trimHeightText.value, bleedText.value
                    )
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        this.menu = menu
        return super.onCreateOptionsMenu(menu)
    }

    private val TextView.value: Double get() = text?.toString()?.toDoubleOrNull() ?: 0.0
}