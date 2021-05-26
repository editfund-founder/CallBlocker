package com.hareru.fuckspam.ui.log

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hareru.fuckspam.R
import com.hareru.fuckspam.databinding.ActivitySpamListBinding

class SpamLogsActivity : AppCompatActivity() {
    private val viewModel by viewModels<SpamLogsViewModel>()
    private val binding by lazy { ActivitySpamListBinding.inflate(layoutInflater) }
    private val spamRulesAdapter = SpamLogsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initToolbar()
    }

    override fun onStart() {
        super.onStart()
        initRvRules()
    }


    private fun initRvRules() {
        binding.rvRules.layoutManager = LinearLayoutManager(this)
        binding.rvRules.adapter = spamRulesAdapter
        viewModel.logs.observe(this) {
            spamRulesAdapter.submitData(lifecycle, it)
        }
    }

    private fun initToolbar() {
        binding.toolbar.title = "日志"
        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.toolbar.inflateMenu(R.menu.menu_logs)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.clear -> {
                    viewModel.clearLogs()
                }
            };true
        }
    }


    companion object {

        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, SpamLogsActivity::class.java)
            context.startActivity(starter)
        }
    }
}