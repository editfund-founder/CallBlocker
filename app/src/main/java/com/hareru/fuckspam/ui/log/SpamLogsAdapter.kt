package com.hareru.fuckspam.ui.log

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hareru.fuckspam.R
import com.hareru.fuckspam.databinding.ItemSpamBinding
import com.hareru.fuckspam.database.AppDatabase
import com.hareru.fuckspam.database.entity.SpamLogs
import com.hareru.fuckspam.database.entity.SpamRules
import com.hareru.fuckspam.databinding.ItemLogsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SpamLogsAdapter : PagingDataAdapter<SpamLogs, SpamLogsAdapter.VH>(DiffCallback()) {

    private class DiffCallback : DiffUtil.ItemCallback<SpamLogs>() {
        override fun areItemsTheSame(oldItem: SpamLogs, newItem: SpamLogs): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: SpamLogs, newItem: SpamLogs): Boolean {
            return oldItem == newItem
        }
    }

    class VH(private val binding: ItemLogsBinding) : RecyclerView.ViewHolder(binding.root) {
        private val job = Job()
        private val scope = CoroutineScope(job)
        private val dao = AppDatabase.getInstance(binding.root.context).spamRulesDao()

        fun bind(item: SpamLogs?) {
            if (item == null) return
            binding.title.text = item.phone
            binding.data.text = SimpleDateFormat("MM-dd hh:mm", Locale.getDefault()).format(item.time)
            binding.summary.text = item.msg
            setMenu(item)
        }

        private fun setMenu(item: SpamLogs) {
            val rules = item.phone.replace("+", "\\+")
            binding.menu.setOnClickListener { view ->
                PopupMenu(view.context, view).apply {
                    inflate(R.menu.menu_logs_item)
                    setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.add_white -> {
                                scope.launch(Dispatchers.IO) {
                                    dao.insert(SpamRules(SpamRules.WhiteType, rules))
                                }
                            }
                            R.id.add_black -> {
                                scope.launch(Dispatchers.IO) {
                                    dao.insert(SpamRules(SpamRules.BlackType, rules))
                                }
                            }
                        };true
                    }
                }.show()
            }

        }

    }


    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemLogsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }
}