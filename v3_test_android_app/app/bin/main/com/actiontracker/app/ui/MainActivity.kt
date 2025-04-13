package com.actiontracker.app.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.actiontracker.app.ActionTrackerApplication
import com.actiontracker.app.R
import com.actiontracker.app.data.ActionRepository
import com.actiontracker.app.data.DayRecordRepository
import com.actiontracker.app.databinding.ActivityMainBinding
import com.actiontracker.app.databinding.DialogAddActionBinding
import com.actiontracker.app.models.ActionEntity
import com.actiontracker.app.util.ThemeHelper
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ActionTrackerViewModel
    private lateinit var adapter: ActionItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply the saved theme before calling super.onCreate()
        ThemeHelper.applyTheme(this, ThemeHelper.getCurrentTheme(this))
        
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val application = application as ActionTrackerApplication
        val actionRepository = ActionRepository(application.database.actionDao())
        val dayRecordRepository = DayRecordRepository(application.database.dayRecordDao())
        
        val viewModelFactory = ActionTrackerViewModel.Companion.Factory(actionRepository, dayRecordRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[ActionTrackerViewModel::class.java]
        
        setupRecyclerView()
        setupDateNavigation()
        setupAddActionButton()
        setupDeleteButton()
        setupObservers()
    }
    
    private fun setupRecyclerView() {
        adapter = ActionItemAdapter(
            onIncrementClicked = { actionId ->
                viewModel.incrementCount(actionId)
            },
            onDecrementClicked = { actionId ->
                viewModel.decrementCount(actionId)
            },
            onLongClick = { action ->
                showDeleteConfirmationDialog(action)
            },
            onSelectionChanged = { selectedCount ->
                // Do not hide the exit button (cross icon) when in delete mode
                // The visibility will be managed by setupDeleteMode and exitSelectionMode
                // This callback only updates the selected item count
            }
        )
        
        binding.actionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }
    
    private fun setupDeleteButton() {
        binding.fabDeleteActions.setOnClickListener {
            if (adapter.isSelectionModeActive()) {
                // Exit selection mode if already active
                exitSelectionMode()
            } else {
                // Enter selection mode
                adapter.setSelectionMode(true)
                setupDeleteMode()
            }
        }
    }
    
    private fun setupDateNavigation() {
        binding.previousDayButton.setOnClickListener {
            viewModel.previousDay()
        }
        
        binding.nextDayButton.setOnClickListener {
            viewModel.nextDay()
        }
        
        binding.selectDateButton.setOnClickListener {
            showDatePickerDialog()
        }
    }
    
    private fun setupAddActionButton() {
        binding.fabAddAction.setOnClickListener {
            showAddActionDialog()
        }
    }
    
    private fun showDeleteConfirmationDialog(action: ActionEntity) {
        AlertDialog.Builder(this)
            .setTitle("Delete ${action.actionName}?")
            .setMessage("This will permanently delete this action and all its records across all days. This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteAction(action)
                createPositionedSnackbar(
                    "Action '${action.actionName}' deleted",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun confirmDeleteSelectedActions(selectedActions: List<ActionEntity>) {
        AlertDialog.Builder(this)
            .setTitle("Delete ${selectedActions.size} Actions?")
            .setMessage("This will permanently delete all selected actions and their records across all days. This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                selectedActions.forEach { action ->
                    viewModel.deleteAction(action)
                }
                exitSelectionMode()
                createPositionedSnackbar(
                    "${selectedActions.size} actions deleted",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showSelectionModeSnackbar() {
        val snackbar = createPositionedSnackbar(
            "Select actions to delete",
            Snackbar.LENGTH_LONG
        ).setAction("Cancel") {
            exitSelectionMode()
        }
        
        snackbar.show()
    }
    
    private fun exitSelectionMode() {
        adapter.setSelectionMode(false)
        
        // Restore the add button to its original state
        binding.fabAddAction.setImageResource(android.R.drawable.ic_input_add)
        binding.fabAddAction.show()
        
        // Reset the add button's click listener
        binding.fabAddAction.setOnClickListener {
            showAddActionDialog()
        }
        
        // Restore the delete button to its original state
        // Keep using our custom white trash icon, but ensure we maintain consistent styling
        binding.fabDeleteActions.setImageResource(R.drawable.ic_trash_white)
        binding.fabDeleteActions.backgroundTintList = android.content.res.ColorStateList.valueOf(getColor(android.R.color.holo_red_light))
        binding.fabDeleteActions.imageTintList = android.content.res.ColorStateList.valueOf(getColor(android.R.color.white))
        
        // Reset the delete button's click listener
        binding.fabDeleteActions.setOnClickListener {
            if (adapter.isSelectionModeActive()) {
                exitSelectionMode()
            } else {
                adapter.setSelectionMode(true)
                setupDeleteMode()
            }
        }
    }
    
    /**
     * Sets up the UI and click listeners for delete mode
     */
    private fun setupDeleteMode() {
        // Ensure the delete button has a red background with white trash icon
        binding.fabDeleteActions.setImageResource(R.drawable.ic_trash_white)
        binding.fabDeleteActions.backgroundTintList = android.content.res.ColorStateList.valueOf(getColor(android.R.color.holo_red_light))
        binding.fabDeleteActions.imageTintList = android.content.res.ColorStateList.valueOf(getColor(android.R.color.white))
        
        // Show cross icon on the right button for exiting delete mode
        binding.fabAddAction.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
        binding.fabAddAction.show()
        
        showSelectionModeSnackbar()
        
        // Set click listeners for both FABs in delete mode
        // Left button (delete) - confirms deletion of selected items
        binding.fabDeleteActions.setOnClickListener {
            val selectedActions = adapter.getSelectedActions()
            if (selectedActions.isNotEmpty()) {
                confirmDeleteSelectedActions(selectedActions)
            } else {
                createPositionedSnackbar("No actions selected", Snackbar.LENGTH_SHORT).show()
            }
        }
        
        // Right button (cross) - exits selection mode
        binding.fabAddAction.setOnClickListener {
            exitSelectionMode()
        }
    }
    
    override fun onBackPressed() {
        if (adapter.isSelectionModeActive()) {
            exitSelectionMode()
        } else {
            super.onBackPressed()
        }
    }
    
    private fun setupObservers() {
        viewModel.currentDateFormatted.observe(this) { formattedDate ->
            binding.dateTextView.text = formattedDate
        }
        
        viewModel.allActions.observe(this) { actions ->
            viewModel.dayRecords.value?.let { dayRecords ->
                val actionsWithCounts = actions.map { action ->
                    viewModel.getActionWithCount(action, dayRecords)
                }
                adapter.submitList(actionsWithCounts)
            } ?: adapter.submitList(emptyList())
        }
        
        viewModel.dayRecords.observe(this) { dayRecords ->
            viewModel.allActions.value?.let { actions ->
                val actionsWithCounts = actions.map { action ->
                    viewModel.getActionWithCount(action, dayRecords)
                }
                adapter.submitList(actionsWithCounts)
            }
        }
    }
    
    private fun showDatePickerDialog() {
        val currentDate = viewModel.currentDate.value ?: Calendar.getInstance()
        
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    viewModel.setDate(this)
                }
            },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    
    private fun showAddActionDialog() {
        val dialogBinding = DialogAddActionBinding.inflate(LayoutInflater.from(this))
        
        AlertDialog.Builder(this)
            .setTitle(R.string.add_action)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.save) { _, _ ->
                val actionName = dialogBinding.actionNameEditText.text.toString().trim()
                if (actionName.isNotBlank()) {
                    viewModel.addAction(actionName)
                } else {
                    createPositionedSnackbar(
                        getString(R.string.action_name_required),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
            .show()
    }

    /**
     * Create the options menu with settings
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    
    /**
     * Handle menu item selections
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                // Open the settings activity
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    /**
     * Helper method to create and position a Snackbar above the bottom action buttons
     */
    private fun createPositionedSnackbar(message: String, duration: Int): Snackbar {
        // Create the Snackbar with the provided message and duration
        val snackbar = Snackbar.make(binding.root, message, duration)
        
        // Get the view of the Snackbar
        val snackbarView = snackbar.view
        
        // Adjust the Snackbar's position through its margins
        val params = snackbarView.layoutParams as android.view.ViewGroup.MarginLayoutParams
        params.bottomMargin = 200 // Position it higher than the default
        
        // Create space between the bottom of the screen and the Snackbar
        snackbar.setAnchorView(binding.fabAddAction)
        
        return snackbar
    }
}
