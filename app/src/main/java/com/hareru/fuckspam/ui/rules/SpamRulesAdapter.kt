package com.hareru.fuckspam.ui.rules

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hareru.fuckspam.R
import com.hareru.fuckspam.databinding.ItemSpamBinding
import com.hareru.fuckspam.database.AppDatabase
import com.hareru.fuckspam.database.entity.SpamRules
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SpamRulesAdapter : PagingDataAdapter<SpamRules, SpamRulesAdapter.VH>(DiffCallback()) {

    private class DiffCallback : DiffUtil.ItemCallback<SpamRules>() {
        override fun areItemsTheSame(oldItem: SpamRules, newItem: SpamRules): Boolean {
            return oldItem.rulesType == newItem.rulesType
        }

        override fun areContentsTheSame(oldItem: SpamRules, newItem: SpamRules): Boolean {
            return oldItem == newItem
        }
    }


    class VH(private val binding: ItemSpamBinding) : RecyclerView.ViewHolder(binding.root) {
        private val job = Job()
        private val scope = CoroutineScope(job)
        private val dao = AppDatabase.getInstance(binding.root.context).spamRulesDao()

        fun bind(item: SpamRules?) {
            if (item == null) return
            binding.title.text = item.rules
            binding.summary.visibility = View.GONE

            binding.menu.setOnClickListener { view ->
                PopupMenu(view.context, view).apply {
                    inflate(R.menu.menu_rules_item)
                    setOnMenuItemClickListener {
                        if (it.itemId == R.id.delete) {
                            delete(item)
                        };true
                    }
                }.show()
            }
        }

        private fun delete(item: SpamRules) {
            scope.launch(Dispatchers.IO) {
                dao.delete(item)
            }
        }
    }


    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemSpamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }
}