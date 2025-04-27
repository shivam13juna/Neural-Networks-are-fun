package com.actiontracker.app.ui

import android.content.Context
import android.graphics.Color
import com.actiontracker.app.models.ActionEntity
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import androidx.appcompat.app.AlertDialog

/**
 * A dialog class for selecting colors for actions using the SkyDoves ColorPickerView
 */
class ColorPickerDialogFixed(private val context: Context) {

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
        // Instead of creating the ColorPickerView manually, use the SkyDoves dialog builder
        // which properly configures all required components
        val colorPickerDialog = com.skydoves.colorpickerview.ColorPickerDialog.Builder(context)
            .setTitle("Choose Color")
            .setPreferenceName("ActionColorPicker")
            .setPositiveButton("Select", 
                com.skydoves.colorpickerview.listeners.ColorEnvelopeListener { envelope, _ ->
                    // Get the selected color from the envelope
                    val selectedColor = envelope.color
                    listener.onColorSelected(action, selectedColor)
                }
            )
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            
        // Create the dialog first
        val dialog = colorPickerDialog.create()
        
        try {
            // Find the ColorPickerView inside the dialog using resource identifier
            val colorPickerViewId = context.resources.getIdentifier(
                "ColorPickerView", "id", "com.skydoves.colorpickerview"
            )
            val pickerView = dialog.findViewById<ColorPickerView>(colorPickerViewId)
            
            // Set initial color if one exists and view was found
            if (pickerView != null) {
                if (action.backgroundColor != 0) {
                    pickerView.setInitialColor(action.backgroundColor)
                } else {
                    // Default color if none is set
                    pickerView.setInitialColor(Color.parseColor("#3F51B5")) // Material Blue default
                }
            }
        } catch (e: Exception) {
            // If we can't set the initial color, just continue showing the dialog
        }
            
        // Show the dialog
        dialog.show()
    }
}
