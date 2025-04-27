package com.actiontracker.app.ui

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AlertDialog
import com.actiontracker.app.models.ActionEntity
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.skydoves.colorpickerview.ColorPickerDialog as SkyColorPickerDialog

/**
 * A wrapper class for the SkyDoves ColorPickerDialog to simplify color selection for actions
 */
class ColorPickerDialog(private val context: Context) {

    /**
     * Interface for color selection events
     */
    interface ColorPickerListener {
        fun onColorSelected(action: ActionEntity, color: Int)
    }

    /**
     * Shows the color picker dialog for an action
     */
    fun show(action: ActionEntity, listener: ColorPickerListener) {
        // Build the color picker dialog using the SkyDoves library
        val initialColor = if (action.backgroundColor != 0) {
            action.backgroundColor
        } else {
            Color.parseColor("#3F51B5") // Material Blue default
        }
        
        val builder = SkyColorPickerDialog.Builder(context)
            .setTitle("Choose Color")
            .setPreferenceName("ActionColorPicker")
            .setPositiveButton("Select", ColorEnvelopeListener { envelope, fromUser ->
                // The color envelope contains the selected color
                val selectedColor = envelope.color
                listener.onColorSelected(action, selectedColor)
            })
            .setNegativeButton("Cancel", { dialogInterface, which ->
                dialogInterface.dismiss()
            })
        
        // Create the dialog
        val dialog = builder.create()
        
        // Show the dialog and then try to set the initial color safely
        dialog.show()
        
        try {
            // Find the ColorPickerView inside the dialog after it's shown
            dialog.window?.decorView?.post {
                val colorPickerViewId = context.resources.getIdentifier(
                    "ColorPickerView", "id", "com.skydoves.colorpickerview"
                )
                val pickerView = dialog.findViewById<ColorPickerView>(colorPickerViewId)
                
                // Set the initial color based on action background
                pickerView?.setInitialColor(initialColor)
            }
        } catch (e: Exception) {
            // If we can't set the initial color, just continue showing the dialog
        }
    }
}
