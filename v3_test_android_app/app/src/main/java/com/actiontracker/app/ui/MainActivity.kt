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
        setupObservers()
    }
    
    private fun setupRecyclerView() {
        adapter = ActionItemAdapter(
            onIncrementClicked = { actionId ->
                viewModel.incrementCount(actionId)
            },
            onDecrementClicked = { actionId ->
                viewModel.decrementCount(actionId)
            }
        )
        
        binding.actionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
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
                    Snackbar.make(
                        binding.root,
                        R.string.action_name_required,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
            .show()
    }
}
