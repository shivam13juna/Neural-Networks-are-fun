package com.actiontracker.app.ui

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.actiontracker.app.R
import com.actiontracker.app.databinding.DialogColorPickerBinding
import com.actiontracker.app.models.ActionEntity

class ColorPickerDialog(private val context: Context) {
    
    interface ColorPickerListener {
        fun onColorSelected(action: ActionEntity, color: Int)
    }
    
    fun show(action: ActionEntity, listener: ColorPickerListener) {
        val binding = DialogColorPickerBinding.inflate(LayoutInflater.from(context))
        
        val dialog = AlertDialog.Builder(context)
            .setTitle("Change Color for ${action.actionName}")
            .setView(binding.root)
            .setNegativeButton("Cancel", null)
            .create()
        
        // Define the colors
        val colors = mapOf(
            binding.colorWhite to Color.WHITE,
            binding.colorRed to Color.parseColor("#FFCDD2"),
            binding.colorPink to Color.parseColor("#F8BBD0"),
            binding.colorPurple to Color.parseColor("#E1BEE7"),
            binding.colorDeepPurple to Color.parseColor("#D1C4E9"),
            binding.colorIndigo to Color.parseColor("#C5CAE9"),
            binding.colorBlue to Color.parseColor("#BBDEFB"),
            binding.colorLightBlue to Color.parseColor("#B3E5FC"),
            binding.colorCyan to Color.parseColor("#B2EBF2"),
            binding.colorTeal to Color.parseColor("#B2DFDB"),
            binding.colorGreen to Color.parseColor("#C8E6C9"),
            binding.colorYellow to Color.parseColor("#FFF9C4")
        )
        
        // Set click listeners for all color buttons
        colors.forEach { (button, color) ->
            button.setOnClickListener {
                listener.onColorSelected(action, color)
                dialog.dismiss()
            }
        }
        
        dialog.show()
    }
}
