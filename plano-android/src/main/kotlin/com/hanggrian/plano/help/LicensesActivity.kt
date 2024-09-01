package com.hanggrian.plano.help

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.hanggrian.plano.R

class LicensesActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_licenses)
        toolbar = findViewById(R.id.toolbar)
        recycler = findViewById(R.id.recycler)

        setSupportActionBar(toolbar)
        recycler.adapter = LicensesAdapter()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
