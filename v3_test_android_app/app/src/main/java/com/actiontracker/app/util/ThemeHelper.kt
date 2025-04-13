package com.actiontracker.app.util

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.actiontracker.app.R

/**
 * Helper class to manage themes in the app
 */
object ThemeHelper {
    private const val THEME_PREFS = "theme_prefs"
    private const val KEY_THEME = "app_theme"
    
    /**
     * Applies the specified theme to the activity
     */
    fun applyTheme(activity: AppCompatActivity, themeName: String) {
        // Save the selected theme
        saveTheme(activity, themeName)
        
        // Apply the theme to the activity
        val themeId = getThemeResourceId(themeName)
        activity.setTheme(themeId)
    }
    
    /**
     * Returns the resource ID for the specified theme name
     */
    private fun getThemeResourceId(themeName: String): Int {
        return when (themeName) {
            "purple" -> R.style.Theme_ActionTracker // Default theme
            "blue" -> R.style.Theme_ActionTracker_Blue
            "red" -> R.style.Theme_ActionTracker_Red
            "green" -> R.style.Theme_ActionTracker_Green
            "orange" -> R.style.Theme_ActionTracker_Orange
            else -> R.style.Theme_ActionTracker // Default to purple if unknown
        }
    }
    
    /**
     * Saves the selected theme to SharedPreferences
     */
    private fun saveTheme(context: Context, themeName: String) {
        val prefs = context.getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_THEME, themeName).apply()
    }
    
    /**
     * Gets the current theme name from SharedPreferences
     */
    fun getCurrentTheme(context: Context): String {
        val prefs = context.getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)
        return prefs.getString(KEY_THEME, "purple") ?: "purple"
    }
}
