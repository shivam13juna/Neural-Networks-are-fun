package com.actiontracker.app.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.actiontracker.app.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class WallpaperActivity : AppCompatActivity() {

    private lateinit var wallpaperPreview: ImageView
    private lateinit var chooseImageButton: Button
    private lateinit var applyWallpaperButton: Button
    private lateinit var removeWallpaperButton: Button
    
    private var selectedImageUri: Uri? = null
    private lateinit var prefs: SharedPreferences
    
    // Register image picker activity launcher
    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            selectedImageUri = data?.data
            
            // Display the selected image in the preview
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(contentResolver, selectedImageUri!!)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                }
                wallpaperPreview.setImageBitmap(bitmap)
                applyWallpaperButton.isEnabled = true
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallpaper)
        
        // Set up the toolbar with a back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Set Wallpaper"
        
        // Initialize views
        wallpaperPreview = findViewById(R.id.wallpaper_preview)
        chooseImageButton = findViewById(R.id.btn_choose_image)
        applyWallpaperButton = findViewById(R.id.btn_apply_wallpaper)
        removeWallpaperButton = findViewById(R.id.btn_remove_wallpaper)
        
        // Initialize preferences
        prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        
        // Disable apply button initially
        applyWallpaperButton.isEnabled = false
        
        // Load current wallpaper if exists
        loadCurrentWallpaper()
        
        // Set up button click listeners
        chooseImageButton.setOnClickListener {
            openImagePicker()
        }
        
        applyWallpaperButton.setOnClickListener {
            saveWallpaper()
        }
        
        removeWallpaperButton.setOnClickListener {
            removeWallpaper()
        }
    }
    
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImage.launch(intent)
    }
    
    private fun saveWallpaper() {
        selectedImageUri?.let { uri ->
            try {
                // Get the bitmap from the uri using the appropriate method based on API level
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(contentResolver, uri)
                }
                
                // Save image to internal storage
                val filename = "app_wallpaper.png"
                val file = File(filesDir, filename)
                
                val fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
                fos.close()
                
                // Save the path in shared preferences
                prefs.edit().putString("wallpaper_path", file.absolutePath).apply()
                
                Toast.makeText(this, "Wallpaper applied", Toast.LENGTH_SHORT).show()
                
                // Return to main activity
                setResult(Activity.RESULT_OK)
                finish()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to save wallpaper", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun loadCurrentWallpaper() {
        val wallpaperPath = prefs.getString("wallpaper_path", null)
        if (wallpaperPath != null) {
            val file = File(wallpaperPath)
            if (file.exists()) {
                val uri = Uri.fromFile(file)
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(contentResolver, uri)
                }
                wallpaperPreview.setImageBitmap(bitmap)
                applyWallpaperButton.isEnabled = true
                removeWallpaperButton.isEnabled = true
            }
        } else {
            removeWallpaperButton.isEnabled = false
        }
    }
    
    private fun removeWallpaper() {
        val wallpaperPath = prefs.getString("wallpaper_path", null)
        if (wallpaperPath != null) {
            val file = File(wallpaperPath)
            if (file.exists()) {
                file.delete()
            }
        }
        
        // Clear the wallpaper path from preferences
        prefs.edit().remove("wallpaper_path").apply()
        
        // Clear the preview
        wallpaperPreview.setImageDrawable(null)
        wallpaperPreview.setBackgroundResource(android.R.color.darker_gray)
        
        // Disable buttons
        applyWallpaperButton.isEnabled = false
        removeWallpaperButton.isEnabled = false
        
        Toast.makeText(this, "Wallpaper removed", Toast.LENGTH_SHORT).show()
        
        // Return to main activity
        setResult(Activity.RESULT_OK)
        finish()
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
