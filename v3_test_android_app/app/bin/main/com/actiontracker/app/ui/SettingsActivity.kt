package com.actiontracker.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.actiontracker.app.R
import com.actiontracker.app.util.ThemeHelper

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        // Set up the toolbar with a back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"
        
        val radioGroup = findViewById<RadioGroup>(R.id.theme_radio_group)
        val applyButton = findViewById<Button>(R.id.btn_apply_theme)
        
        // Set the current theme as selected
        val currentTheme = ThemeHelper.getCurrentTheme(this)
        val radioButtonId = when (currentTheme) {
            "purple" -> R.id.radio_purple
            "blue" -> R.id.radio_blue
            "red" -> R.id.radio_red
            "green" -> R.id.radio_green
            "orange" -> R.id.radio_orange
            else -> R.id.radio_purple
        }
        radioGroup.check(radioButtonId)
        
        // Apply the selected theme when the button is clicked
        applyButton.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            
            val themeKey = when (selectedId) {
                R.id.radio_purple -> "purple"
                R.id.radio_blue -> "blue"
                R.id.radio_red -> "red"
                R.id.radio_green -> "green"
                R.id.radio_orange -> "orange"
                else -> "purple"
            }
            
            if (themeKey != ThemeHelper.getCurrentTheme(this)) {
                ThemeHelper.applyTheme(this, themeKey)
                
                // Restart the app to apply the new theme
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
