package com.actiontracker.app.ui

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.actiontracker.app.R
import com.actiontracker.app.models.ActionEntity
import com.actiontracker.app.ui.ColorPaletteView

/**
 * Enhanced color picker dialog using custom ColorPaletteView
 */
class EnhancedColorPickerDialog(private val context: Context) {

    /**
     * Interface for color selection events
     */
    interface ColorPickerListener {
        fun onColorSelected(action: ActionEntity, color: Int)
    }

    // Rainbow palette colors - Keep this list for potential future use
    private val rainbowColors = listOf(
        "#FF0000", // Red
        "#FF7F00", // Orange
        "#FFFF00", // Yellow
        "#00FF00", // Green
        "#0000FF", // Blue
        "#4B0082", // Indigo
        "#9400D3"  // Violet
    )

    // Material Design color palette - Keep this list for potential future use
    private val materialColors = listOf(
        "#F44336", // Red
        "#E91E63", // Pink
        "#9C27B0", // Purple
        "#673AB7", // Deep Purple
        "#3F51B5", // Indigo
        "#2196F3", // Blue
        "#03A9F4", // Light Blue
        "#00BCD4", // Cyan
        "#009688", // Teal
        "#4CAF50", // Green
        "#8BC34A", // Light Green
        "#CDDC39", // Lime
        "#FFEB3B", // Yellow
        "#FFC107", // Amber
        "#FF9800", // Orange
        "#FF5722", // Deep Orange
        "#795548", // Brown
        "#9E9E9E", // Grey
        "#607D8B", // Blue Grey
        "#000000", // Black
        "#FFFFFF"  // White
    )

    /**
     * Shows the color picker dialog for an action using a custom inflated layout.
     */
    fun show(action: ActionEntity, listener: ColorPickerListener) {
        // Inflate the custom layout
        val inflater = LayoutInflater.from(context)
        val customLayout = inflater.inflate(R.layout.dialog_enhanced_color_picker, null)

        // Find our custom ColorPaletteView and rainbow layout
        val colorPickerView = customLayout.findViewById<ColorPaletteView>(R.id.colorPickerView)
        val rainbowLayout = customLayout.findViewById<LinearLayout>(R.id.rainbowLayout)

        // Set initial and track current color selection
        val initialColor = if (action.backgroundColor != 0) action.backgroundColor else Color.RED
        var selectedColor = initialColor
        colorPickerView.setColor(initialColor)
        colorPickerView.setColorChangeListener(object : ColorPaletteView.ColorChangeListener {
            override fun onColorChanged(color: Int) {
                selectedColor = color
            }
        })

        // Build the dialog
        val alertDialogBuilder = AlertDialog.Builder(context)
            .setTitle("Choose Color")
            .setView(customLayout) // Set the inflated custom layout
            .setPositiveButton("Select") { dialogInterface, _ ->
                listener.onColorSelected(action, selectedColor)
                dialogInterface.dismiss()
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }

        // Create the dialog *before* setting up rainbow colors so we can pass it
        val dialog = alertDialogBuilder.create()

        // Set up the rainbow color boxes, passing the created dialog
        setupRainbowColorsForSimpleWheel(rainbowLayout, action, listener, dialog)

        // Show the dialog
        dialog.show()
    }

    /**
     * Sets up the simple rainbow colors row for the color wheel dialog.
     * Now takes the created AlertDialog to allow dismissal on color click.
     */
    private fun setupRainbowColorsForSimpleWheel(
        layout: LinearLayout,
        action: ActionEntity,
        listener: ColorPickerListener,
        dialog: AlertDialog? // Changed to nullable AlertDialog
    ) {
        // Current color from the action
        val currentColor = if (action.backgroundColor != 0) action.backgroundColor else Color.TRANSPARENT

        // Use only the 7 rainbow colors for quick selection
        val quickColors = rainbowColors

        // Calculate the button size
        val displayMetrics = context.resources.displayMetrics
        val buttonSize = (45 * displayMetrics.density).toInt()
        val margin = (4 * displayMetrics.density).toInt()

        // Clear any previous views in the layout (important if dialog is reused)
        layout.removeAllViews()

        quickColors.forEach { colorHex ->
            val button = View(context)
            val color = Color.parseColor(colorHex)

            // Create layout params for equal-sized color boxes
            val params = LinearLayout.LayoutParams(buttonSize, buttonSize)
            params.setMargins(margin, margin, margin, margin)
            button.layoutParams = params

            // Create circular button with the color
            val shape = android.graphics.drawable.GradientDrawable()
            shape.shape = android.graphics.drawable.GradientDrawable.OVAL
            shape.setColor(color)

            // Add white border to the current color if it matches
            if (color == currentColor) {
                shape.setStroke(3, Color.WHITE)
            } else {
                shape.setStroke(1, Color.LTGRAY)
            }

            button.background = shape

            // Set click listener to immediately apply this color and dismiss
            button.setOnClickListener {
                listener.onColorSelected(action, color)
                dialog?.dismiss()
            }

            layout.addView(button)
        }
    }
}
