package com.actiontracker.app.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
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
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ActionTrackerViewModel
    private lateinit var adapter: ActionItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
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
                // Update the delete FAB visibility when selection changes
                if (selectedCount > 0) {
                    binding.fabAddAction.show()
                } else {
                    binding.fabAddAction.hide()
                }
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
                binding.fabAddAction.hide()
                
                // Show the deletion FAB in bottom right
                binding.fabAddAction.setImageResource(android.R.drawable.ic_menu_delete)
                binding.fabAddAction.show()
                
                showSelectionModeSnackbar()
                
                // Change the function of the + button to be delete selected
                binding.fabAddAction.setOnClickListener {
                    val selectedActions = adapter.getSelectedActions()
                    confirmDeleteSelectedActions(selectedActions)
                }
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
