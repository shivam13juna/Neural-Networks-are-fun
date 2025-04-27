package com.actiontracker.app.ui

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.actiontracker.app.R
import com.actiontracker.app.models.ActionEntity
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

/**
 * Enhanced color picker dialog with a simplified implementation to avoid crashes
 */
class EnhancedColorPickerDialog(private val context: Context) {

    /**
     * Interface for color selection events
     */
    interface ColorPickerListener {
        fun onColorSelected(action: ActionEntity, color: Int)
    }

    // Rainbow palette colors
    private val rainbowColors = listOf(
        "#FF0000", // Red
        "#FF7F00", // Orange
        "#FFFF00", // Yellow
        "#00FF00", // Green
        "#0000FF", // Blue
        "#4B0082", // Indigo
        "#9400D3"  // Violet
    )

    // Material Design color palette
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
     * Shows the color picker dialog for an action
     */
    fun show(action: ActionEntity, listener: ColorPickerListener) {
        // Create a unified custom dialog with both color wheel and rainbow colors
        val customLayout = LinearLayout(context)
        customLayout.orientation = LinearLayout.VERTICAL
        customLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        customLayout.setPadding(16, 16, 16, 16)
        
        // Create the color picker view
        val colorPickerView = com.skydoves.colorpickerview.ColorPickerView(context)
        colorPickerView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
             500 // Keep fixed height for now
        )

        // Set initial color *before* adding the view
        if (action.backgroundColor != 0) {
            colorPickerView.setInitialColor(action.backgroundColor)
        } else {
            colorPickerView.setInitialColor(Color.RED)
        }
        
        // Add bubble flag *before* adding the view
        val bubbleFlag = com.skydoves.colorpickerview.flag.BubbleFlag(context)
        bubbleFlag.flagMode = com.skydoves.colorpickerview.flag.FlagMode.FADE
        colorPickerView.flagView = bubbleFlag

        // Add the color picker view to the layout *after* setting properties
        customLayout.addView(colorPickerView)
        
        // Add a divider between the color wheel and rainbow colors
        val divider = View(context)
        divider.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            2
        )
        divider.setBackgroundColor(Color.parseColor("#CCCCCC"))
        
        // Add title for the rainbow colors section
        val rainbowTitle = TextView(context)
        rainbowTitle.text = "Quick Colors"
        rainbowTitle.setPadding(8, 24, 8, 8)
        rainbowTitle.setTextColor(Color.BLACK)
        rainbowTitle.textSize = 16f
        
        // Create a layout for rainbow color boxes
        val rainbowLayout = LinearLayout(context)
        rainbowLayout.orientation = LinearLayout.HORIZONTAL
        rainbowLayout.gravity = android.view.Gravity.CENTER
        rainbowLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        rainbowLayout.setPadding(8, 8, 8, 8)
        
        // Add all components to the custom layout
        customLayout.addView(divider)
        customLayout.addView(rainbowTitle)
        customLayout.addView(rainbowLayout)
        
        // Set up the rainbow color boxes
        setupRainbowColorsForSimpleWheel(rainbowLayout, action, listener, null)
        
        // Create the dialog builder with our custom layout
        val alertDialogBuilder = AlertDialog.Builder(context)
            .setTitle("Choose Color")
            .setView(customLayout)
            .setPositiveButton("Select") { dialogInterface, _ ->
                listener.onColorSelected(action, colorPickerView.color)
                dialogInterface.dismiss()
            }
            .setNegativeButton("Cancel") { dialogInterface, _ -> 
                dialogInterface.dismiss()
            }
        
        // Create and show the dialog
        val dialog = alertDialogBuilder.create()
        dialog.show()
    }
    
    /**
     * Sets up the simple rainbow colors row for the color wheel dialog
     */
    private fun setupRainbowColorsForSimpleWheel(
        layout: LinearLayout,
        action: ActionEntity,
        listener: ColorPickerListener,
        dialog: AlertDialog?
    ) {
        // Current color from the action
        val currentColor = if (action.backgroundColor != 0) action.backgroundColor else Color.TRANSPARENT
        
        // Use only the 7 rainbow colors for quick selection
        val quickColors = rainbowColors
        
        // Calculate the button size
        val displayMetrics = context.resources.displayMetrics
        val buttonSize = (45 * displayMetrics.density).toInt()
        val margin = (4 * displayMetrics.density).toInt()
        
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
                shape.setStroke(4, Color.WHITE)
            } else {
                // Add a thin gray border to all color circles for better visibility
                shape.setStroke(1, Color.LTGRAY)
            }
            
            button.background = shape
            
            // Set click listener to immediately apply this color
            button.setOnClickListener {
                listener.onColorSelected(action, color)
                dialog?.dismiss() ?: (context as? androidx.fragment.app.FragmentActivity)?.supportFragmentManager
                    ?.findFragmentByTag("COLOR_PICKER_DIALOG")
                    ?.let { fragment ->
                        (fragment as? androidx.fragment.app.DialogFragment)?.dismiss()
                    }
            }
            
            layout.addView(button)
        }
    }
    
    /**
     * Sets up the rainbow colors buttons in the grid
     */
    private fun setupRainbowColors(
        gridLayout: GridLayout,
        action: ActionEntity,
        listener: ColorPickerListener,
        dialog: AlertDialog
    ) {
        // Set column count based on the number of rainbow colors
        gridLayout.columnCount = rainbowColors.size
        
        // Current color from the action
        val currentColor = if (action.backgroundColor != 0) action.backgroundColor else Color.TRANSPARENT
        
        rainbowColors.forEachIndexed { index, colorHex ->
            val button = Button(context)
            val color = Color.parseColor(colorHex)
            button.setBackgroundColor(color)
            
            // Set minimal height and remove text
            button.minimumHeight = 0
            button.text = ""
            button.height = 60
            
            // Create layout params
            val params = GridLayout.LayoutParams().apply {
                rowSpec = GridLayout.spec(0)
                columnSpec = GridLayout.spec(index, 1f)
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                setMargins(4, 4, 4, 4)
            }
            
            button.layoutParams = params
            
            // Set click listener
            button.setOnClickListener {
                listener.onColorSelected(action, color)
                dialog.dismiss()
            }
            
            // Highlight current color if it matches
            if (color == currentColor) {
                val shape = android.graphics.drawable.GradientDrawable()
                shape.setColor(color)
                shape.setStroke(8, Color.WHITE)
                shape.cornerRadius = 4f
                button.background = shape
            }
            
            gridLayout.addView(button)
        }
    }
    
    /**
     * Sets up the color quick-select buttons in the grid
     */
    private fun setupColorButtons(
        gridLayout: GridLayout,
        action: ActionEntity,
        listener: ColorPickerListener,
        dialog: AlertDialog
    ) {
        // Calculate the number of columns to display (4 or 5 work well on most screens)
        val numColumns = 4
        
        // Set the column count for the grid
        gridLayout.columnCount = numColumns
        
        // Highlight the current color from the action if it exists
        val currentColor = if (action.backgroundColor != 0) action.backgroundColor else Color.TRANSPARENT
        
        materialColors.forEachIndexed { index, colorHex ->
            val button = Button(context)
            val color = Color.parseColor(colorHex)
            button.setBackgroundColor(color)
            
            // Set minimal height and remove text
            button.minimumHeight = 0
            button.text = ""
            button.height = 60
            
            // Calculate row and column based on index
            val row = index / numColumns
            val column = index % numColumns
            
            // Create layout params
            val params = GridLayout.LayoutParams().apply {
                rowSpec = GridLayout.spec(row)
                columnSpec = GridLayout.spec(column, 1f)
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                setMargins(8, 8, 8, 8)
            }
            
            button.layoutParams = params
            
            // Set click listener to immediately apply this color
            button.setOnClickListener {
                listener.onColorSelected(action, color)
                dialog.dismiss()
            }
            
            // Add a border to the current color if it matches
            if (color == currentColor) {
                // Use programmatic approach for highlighting
                button.height = 70
                
                // Create a layer with a border
                val shape = android.graphics.drawable.GradientDrawable()
                shape.setColor(color)
                shape.setStroke(8, Color.WHITE)
                shape.cornerRadius = 4f
                button.background = shape
            }
            
            gridLayout.addView(button)
        }
    }
}
