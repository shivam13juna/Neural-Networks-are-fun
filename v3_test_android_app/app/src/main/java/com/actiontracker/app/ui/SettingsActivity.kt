package com.actiontracker.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.actiontracker.app.R
import com.actiontracker.app.ui.WallpaperActivity
import com.actiontracker.app.ui.ThemeSelectionActivity
import com.google.android.material.appbar.MaterialToolbar

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initialize toolbar as the ActionBar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_settings)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_view)  // replace with a home icon as desired
        toolbar.setNavigationOnClickListener { finish() }

        // Button to open theme selection screen
        val changePaletteButton = findViewById<Button>(R.id.btn_change_color_palette)
        changePaletteButton.setOnClickListener {
            startActivity(Intent(this, ThemeSelectionActivity::class.java))
        }

        // Button to open wallpaper screen
        val setWallpaperButton = findViewById<Button>(R.id.btn_set_wallpaper)
        setWallpaperButton.setOnClickListener {
            startActivity(Intent(this, WallpaperActivity::class.java))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
