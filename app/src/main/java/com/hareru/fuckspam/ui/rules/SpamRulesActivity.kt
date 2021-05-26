package com.hareru.fuckspam.ui.rules

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hareru.fuckspam.R
import com.hareru.fuckspam.databinding.ActivitySpamListBinding
import com.hareru.fuckspam.database.entity.SpamRules

class SpamRulesActivity : AppCompatActivity() {
    private val viewModel by viewModels<SpamRulesViewModel>()
    private val binding by lazy { ActivitySpamListBinding.inflate(layoutInflater) }
    private val spamRulesAdapter = SpamRulesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val rulesType = intent.extras!!.getInt(RULES_TYPE)
        viewModel.setSpamType(rulesType)
        initToolbar(rulesType)
    }

    override fun onStart() {
        super.onStart()
        initRvRules()
    }

    private fun initRvRules() {
        binding.rvRules.layoutManager = LinearLayoutManager(this)
        binding.rvRules.adapter = spamRulesAdapter
        viewModel.rules.observe(this) {
            spamRulesAdapter.submitData(lifecycle, it)
        }
    }

    private fun initToolbar(type: Int) {
        when (type) {
            SpamRules.WhiteType -> {
                binding.toolbar.title = "白名单"
            }
            SpamRules.BlackType -> {
                binding.toolbar.title = "黑名单"
            }
        }
        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.toolbar.inflateMenu(R.menu.menu_rules)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.add -> {
                    RulesAddFragment().show(supportFragmentManager, "TAG")
                }
            };true
        }
    }


    companion object {
        private const val RULES_TYPE = "RULES_TYPE"

        @JvmStatic
        fun start(context: Context, spamType: Int) {
            val starter = Intent(context, SpamRulesActivity::class.java)
                    .putExtra(RULES_TYPE, spamType)
            context.startActivity(starter)
        }
    }
}