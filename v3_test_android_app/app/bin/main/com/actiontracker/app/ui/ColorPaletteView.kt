package com.actiontracker.app.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Custom view that displays a color wheel/palette for intuitive color selection
 */
class ColorPaletteView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Interface to notify when color changes
    interface ColorChangeListener {
        fun onColorChanged(color: Int)
    }

    // Properties
    private var colorChangeListener: ColorChangeListener? = null
    private var centerX: Float = 0f
    private var centerY: Float = 0f
    private var radius: Float = 0f
    private var selectedColor: Int = Color.RED

    // Paints for drawing
    private val wheelPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val centerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val selectorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    // Colors for the wheel
    private val colors = intArrayOf(
        Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN,
        Color.GREEN, Color.YELLOW, Color.RED
    )

    // Inner white circle properties
    private val innerCircleFactor = 0.7f
    private var brightness = 1.0f
    private val brightnessPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Initialize the view
    init {
        centerPaint.color = selectedColor
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        
        // Calculate center point and radius
        centerX = w / 2f
        centerY = h / 2f
        radius = (Math.min(w, h) / 2f) * 0.9f
        
        // Create the color wheel gradient
        val shader = SweepGradient(centerX, centerY, colors, null)
        wheelPaint.shader = shader
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw the color wheel (outer ring)
        canvas.drawCircle(centerX, centerY, radius, wheelPaint)
        
        // Draw the brightness center circle
        brightnessPaint.color = selectedColor
        canvas.drawCircle(centerX, centerY, radius * innerCircleFactor, brightnessPaint)
        
        // Draw the selected color point
        val hsv = FloatArray(3)
        Color.colorToHSV(selectedColor, hsv)
        
        val angle = Math.toRadians(hsv[0].toDouble())
        val distance = radius * (hsv[1])
        
        val pointX = centerX + (distance * Math.cos(angle)).toFloat()
        val pointY = centerY + (distance * Math.sin(angle)).toFloat()
        
        // Draw selector circle
        canvas.drawCircle(pointX, pointY, 10f, selectorPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                // Calculate distance from center
                val dx = x - centerX
                val dy = y - centerY
                val distance = hypot(dx, dy)
                
                // Handle touch events based on where they occur
                if (distance <= radius) {
                    if (distance <= radius * innerCircleFactor) {
                        // Inside the brightness circle - adjust brightness
                        val brightnessY = centerY - y
                        brightness = (brightnessY / (radius * innerCircleFactor)) + 0.5f
                        brightness = brightness.coerceIn(0.1f, 1.0f)
                        
                        val hsv = FloatArray(3)
                        Color.colorToHSV(selectedColor, hsv)
                        hsv[2] = brightness
                        selectedColor = Color.HSVToColor(hsv)
                    } else {
                        // Inside the color wheel - select color
                        val angle = atan2(dy, dx)
                        val hue = (Math.toDegrees(angle.toDouble()) + 360) % 360
                        
                        // Calculate saturation based on distance from center
                        val saturation = (distance / radius).coerceIn(0f, 1f)
                        
                        // Create color from HSV
                        val hsv = floatArrayOf(hue.toFloat(), saturation, brightness)
                        selectedColor = Color.HSVToColor(hsv)
                    }
                    
                    // Update the center paint color
                    centerPaint.color = selectedColor
                    
                    // Notify listener
                    colorChangeListener?.onColorChanged(selectedColor)
                    
                    // Redraw
                    invalidate()
                    return true
                }
            }
        }
        
        return super.onTouchEvent(event)
    }

    /**
     * Set a listener to be notified of color changes
     */
    fun setColorChangeListener(listener: ColorChangeListener) {
        this.colorChangeListener = listener
    }

    /**
     * Set the current color (for initial state)
     */
    fun setColor(color: Int) {
        selectedColor = color
        
        // Extract brightness from the color
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        brightness = hsv[2]
        
        centerPaint.color = color
        invalidate()
    }

    /**
     * Get the currently selected color
     */
    fun getColor(): Int = selectedColor
}
