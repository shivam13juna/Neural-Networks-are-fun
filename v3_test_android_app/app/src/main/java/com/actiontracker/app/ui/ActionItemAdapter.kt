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
    private val onDecrementClicked: (actionId: Int) -> Unit
) : ListAdapter<Pair<ActionEntity, Int>, ActionItemAdapter.ActionViewHolder>(ActionDiffCallback()) {

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
            
            binding.btnIncrement.setOnClickListener {
                onIncrementClicked(action.actionId)
            }
            
            binding.btnDecrement.setOnClickListener {
                onDecrementClicked(action.actionId)
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
