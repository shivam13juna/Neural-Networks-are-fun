package com.actiontracker.app.ui

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.actiontracker.app.R
import com.actiontracker.app.databinding.ActivityGraphBinding
import com.actiontracker.app.ActionTrackerApplication
import com.actiontracker.app.data.ActionRepository
import com.actiontracker.app.data.DayRecordRepository
import com.actiontracker.app.ui.ActionTrackerViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.Legend
import android.graphics.Color
import android.graphics.Paint
import java.util.Calendar
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.recyclerview.widget.LinearLayoutManager
import com.actiontracker.app.models.ActionEntity
import com.actiontracker.app.models.DayRecordEntity
import com.actiontracker.app.ui.ActionItemAdapter
import androidx.appcompat.app.AlertDialog
import android.content.DialogInterface
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import android.content.Context
import java.io.File
import android.graphics.drawable.BitmapDrawable
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import androidx.core.graphics.ColorUtils

class GraphActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGraphBinding
    private lateinit var viewModel: ActionTrackerViewModel
    private lateinit var dayRepo: DayRecordRepository
    private val isoFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private var startCal = Calendar.getInstance()
    private var endCal = Calendar.getInstance()

    private val selectedActions = mutableSetOf<ActionEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGraphBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyWallpaper()

        // Fix for white status bar: match MainActivity style
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryDark)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        // Toolbar setup: add heading and filter icon
        setSupportActionBar(binding.toolbarGraph)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarGraph.setNavigationOnClickListener { finish() }

        // Initialize ViewModel and repository
        val app = application as ActionTrackerApplication
        val actionRepo = ActionRepository(app.database.actionDao())
        dayRepo = DayRecordRepository(app.database.dayRecordDao())
        val viewModelFactory = ActionTrackerViewModel.Companion.Factory(actionRepo, dayRepo)
        viewModel = ViewModelProvider(this, viewModelFactory)[ActionTrackerViewModel::class.java]

        // Initialize default range: past week
        endCal = Calendar.getInstance()
        startCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -7) }
        updateDateButtons()

        binding.btnStartDate.setOnClickListener { pickDate(isStart = true) }
        binding.btnEndDate.setOnClickListener { pickDate(isStart = false) }
        binding.btnShowGraph.setOnClickListener { drawGraph() }

        // Observe allActions LiveData after ViewModel initialization and redraw the graph when actions load, ensuring the chart displays data.
        viewModel.allActions.observe(this) {
            drawGraph()
        }
        // Populate graph immediately with default last‑7‑day data
        drawGraph()
    }

    private fun updateDateButtons() {
        val fmt = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        binding.btnStartDate.text = fmt.format(startCal.time)
        binding.btnEndDate.text = fmt.format(endCal.time)
    }

    private fun pickDate(isStart: Boolean) {
        val cal = if (isStart) startCal else endCal
        DatePickerDialog(
            this,
            { _, year, month, day ->
                cal.set(year, month, day)
                updateDateButtons()
            },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun drawGraph() {
        // Prepare date range strings
        val startMs = startCal.timeInMillis
        val endMs = endCal.timeInMillis
        val startDateStr = isoFormatter.format(Date(startMs))
        val endDateStr = isoFormatter.format(Date(endMs))
        // Determine which actions to plot (respect any filters)
        val allActions = viewModel.allActions.value ?: emptyList()
        val actionsToPlot = if (selectedActions.isNotEmpty()) selectedActions.toList() else allActions
        val actionIds = actionsToPlot.map { it.actionId }
        // Fetch records for selected actions over range and observe
        dayRepo.getDayRecordsForRange(startDateStr, endDateStr, actionIds)
            .observe(this) { records: List<DayRecordEntity> ->
                // Build list of dates at daily intervals
                val dateList = mutableListOf<Long>()
                val itCal = startCal.clone() as Calendar
                while (!itCal.after(endCal)) {
                    dateList.add(itCal.timeInMillis)
                    itCal.add(Calendar.DAY_OF_MONTH, 1)
                }
                // Group records by actionId then date
                val recordsByAction = records.groupBy { it.actionId }
                // Build a dataset per action
                val dataSets = actionsToPlot.map { action ->
                    val recMap = recordsByAction[action.actionId]
                        ?.associate { it.date to it.count.toFloat() } ?: emptyMap()
                    val entries = dateList.map { ts ->
                        val x = ts.toFloat()
                        val y = recMap[isoFormatter.format(Date(ts))] ?: 0f
                        Entry(x, y)
                    }
                    LineDataSet(entries, action.actionName).apply {
                        // Use a darker shade of the action color for visibility
                        val baseColor = action.backgroundColor
                        val darkColor = ColorUtils.blendARGB(baseColor, Color.BLACK, 0.3f)
                        color = darkColor
                        valueTextColor = darkColor
                        setDrawCircles(false)
                    }
                }
                // Set data and refresh
                binding.lineChart.data = LineData(dataSets)
            }
        binding.lineChart.apply {
            setBackgroundColor(Color.WHITE)
            setDrawGridBackground(false)
            // Provide space: left, top(for legend), right, bottom(for X labels)
            setExtraOffsets(16f, 40f, 16f, 48f)
            // Show legend at top, outside plot area
            legend.apply {
                isEnabled = true
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
            }
            // Disable built-in description
            description.isEnabled = false
        }
        // Dynamic ‘nice-step’ selection for X-axis
        val xAxis = binding.lineChart.xAxis
        val spanMs = endMs - startMs
        val msPerDay = 24f * 60 * 60 * 1000
        val spanDays = spanMs / msPerDay
        val desiredTicks = 6
        // Nice steps in days
        val steps = listOf(1f, 2f, 7f, 14f, 30f, 90f, 180f, 365f)
        val rawStep = spanDays / (desiredTicks - 1)
        val intervalDays = steps.find { it >= rawStep } ?: steps.last()
        val intervalMs = intervalDays * msPerDay
        val labelCount = (spanDays / intervalDays).toInt() + 1
        // Formatter switches by interval length
        val dateFormat = when {
            intervalDays >= 365f -> SimpleDateFormat("yyyy", Locale.getDefault())
            intervalDays >= 30f  -> SimpleDateFormat("MMM yyyy", Locale.getDefault())
            else                   -> SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        }
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawAxisLine(true)
            setDrawGridLines(false)
            axisMinimum = startMs.toFloat()
            axisMaximum = endMs.toFloat()
            setLabelCount(labelCount, true)
            granularity = intervalMs
            isGranularityEnabled = true
            setLabelRotationAngle(-45f)
            setAvoidFirstLastClipping(true)
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float) = dateFormat.format(Date(value.toLong()))
            }
        }
        binding.lineChart.invalidate()
    }

    private fun applyWallpaper() {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val wallpaperPath = prefs.getString("wallpaper_path", null)
        if (wallpaperPath != null) {
            try {
                val file = File(wallpaperPath)
                if (file.exists()) {
                    val drawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val source = ImageDecoder.createSource(file)
                        BitmapDrawable(resources, ImageDecoder.decodeBitmap(source))
                    } else {
                        @Suppress("DEPRECATION")
                        BitmapDrawable(resources, MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(file)))
                    }
                    binding.root.background = drawable
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            binding.root.setBackgroundResource(android.R.color.white)
        }
    }

    private fun showActionFilterDialog() {
        // Get current actions list or empty if not loaded yet
        val actions = viewModel.allActions.value.orEmpty()
        val names = actions.map { it.actionName }.toTypedArray()
        val checkedItems = actions.map { selectedActions.contains(it) }.toBooleanArray()
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.filter))
            .setMultiChoiceItems(names, checkedItems) { _, which, isChecked ->
                val action = actions[which]
                if (isChecked) selectedActions.add(action) else selectedActions.remove(action)
            }
            .setPositiveButton(android.R.string.ok) { _, _ -> drawGraph() }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.graph_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                showActionFilterDialog()
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
