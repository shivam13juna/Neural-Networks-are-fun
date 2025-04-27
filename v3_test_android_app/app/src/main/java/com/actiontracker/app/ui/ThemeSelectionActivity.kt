package com.actiontracker.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.actiontracker.app.R
import com.actiontracker.app.util.ThemeHelper

class ThemeSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme_selection)

        // Toolbar back
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.choose_app_theme)

        val radioGroup = findViewById<RadioGroup>(R.id.theme_radio_group)
        val applyButton = findViewById<Button>(R.id.btn_apply_theme)

        // Set current theme selected
        val currentTheme = ThemeHelper.getCurrentTheme(this)
        val selectedId = when (currentTheme) {
            "purple" -> R.id.radio_purple
            "blue" -> R.id.radio_blue
            "red" -> R.id.radio_red
            "green" -> R.id.radio_green
            "orange" -> R.id.radio_orange
            else -> R.id.radio_purple
        }
        radioGroup.check(selectedId)

        applyButton.setOnClickListener {
            val checkedId = radioGroup.checkedRadioButtonId
            val themeKey = when (checkedId) {
                R.id.radio_purple -> "purple"
                R.id.radio_blue -> "blue"
                R.id.radio_red -> "red"
                R.id.radio_green -> "green"
                R.id.radio_orange -> "orange"
                else -> "purple"
            }
            if (themeKey != ThemeHelper.getCurrentTheme(this)) {
                ThemeHelper.applyTheme(this, themeKey)
                // Restart app
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
