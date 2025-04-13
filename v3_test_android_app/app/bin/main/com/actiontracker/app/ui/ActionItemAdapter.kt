package com.actiontracker.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.actiontracker.app.databinding.ItemActionBinding
import com.actiontracker.app.models.ActionEntity

class ActionItemAdapter(
    private val onIncrementClicked: (actionId: Int) -> Unit,
    private val onDecrementClicked: (actionId: Int) -> Unit,
    private val onLongClick: ((ActionEntity) -> Unit)? = null,
    private val onSelectionChanged: ((Int) -> Unit)? = null
) : ListAdapter<Pair<ActionEntity, Int>, ActionItemAdapter.ActionViewHolder>(ActionDiffCallback()) {

    private var selectionMode = false
    private val selectedActions = mutableSetOf<ActionEntity>()
    
    fun setSelectionMode(enabled: Boolean) {
        selectionMode = enabled
        selectedActions.clear()
        notifyDataSetChanged()
        onSelectionChanged?.invoke(0)
    }
    
    fun getSelectedActions(): List<ActionEntity> {
        return selectedActions.toList()
    }
    
    fun isSelectionModeActive(): Boolean {
        return selectionMode
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionViewHolder {
        val binding = ItemActionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ActionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ActionViewHolder(private val binding: ItemActionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Pair<ActionEntity, Int>) {
            val (action, count) = item
            
            binding.actionName.text = action.actionName
            binding.actionCount.text = count.toString()
            
            // Handle selection mode display
            if (selectionMode) {
                binding.actionCheckbox.visibility = android.view.View.VISIBLE
                binding.btnDecrement.visibility = android.view.View.GONE
                binding.btnIncrement.visibility = android.view.View.GONE
                
                binding.actionCheckbox.isChecked = selectedActions.contains(action)
                
                binding.root.setOnClickListener {
                    if (selectedActions.contains(action)) {
                        selectedActions.remove(action)
                    } else {
                        selectedActions.add(action)
                    }
                    binding.actionCheckbox.isChecked = selectedActions.contains(action)
                    notifyItemChanged(adapterPosition)
                    onSelectionChanged?.invoke(selectedActions.size)
                }
                
                binding.actionCheckbox.setOnClickListener {
                    if (binding.actionCheckbox.isChecked) {
                        selectedActions.add(action)
                    } else {
                        selectedActions.remove(action)
                    }
                    onSelectionChanged?.invoke(selectedActions.size)
                }
            } else {
                binding.actionCheckbox.visibility = android.view.View.GONE
                binding.btnDecrement.visibility = android.view.View.VISIBLE
                binding.btnIncrement.visibility = android.view.View.VISIBLE
                
                binding.btnIncrement.setOnClickListener {
                    onIncrementClicked(action.actionId)
                }
                
                binding.btnDecrement.setOnClickListener {
                    onDecrementClicked(action.actionId)
                }
                
                // Set up long click listener for deletion
                binding.root.setOnLongClickListener {
                    onLongClick?.invoke(action)
                    true
                }
                
                // Clear click listener when not in selection mode
                binding.root.setOnClickListener(null)
            }
        }
    }

    class ActionDiffCallback : DiffUtil.ItemCallback<Pair<ActionEntity, Int>>() {
        override fun areItemsTheSame(oldItem: Pair<ActionEntity, Int>, newItem: Pair<ActionEntity, Int>): Boolean {
            return oldItem.first.actionId == newItem.first.actionId
        }

        override fun areContentsTheSame(oldItem: Pair<ActionEntity, Int>, newItem: Pair<ActionEntity, Int>): Boolean {
            return oldItem.first.actionName == newItem.first.actionName &&
                   oldItem.second == newItem.second
        }
    }
}
