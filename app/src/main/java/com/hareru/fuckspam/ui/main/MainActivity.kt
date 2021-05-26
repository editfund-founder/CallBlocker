package com.hareru.fuckspam.ui.main

import android.app.Activity
import android.app.role.RoleManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.hareru.fuckspam.ui.main.EnableState.*
import com.hareru.fuckspam.databinding.ActivityMainBinding
import android.content.Intent
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import com.hareru.fuckspam.R
import com.hareru.fuckspam.databinding.DialogAbortBinding
import com.hareru.fuckspam.database.entity.SpamRules
import com.hareru.fuckspam.ui.log.SpamLogsActivity
import com.hareru.fuckspam.ui.rules.SpamRulesActivity


class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel>()
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val preferences by lazy { getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE) }
    private val roleManager by lazy { getSystemService(ROLE_SERVICE) as RoleManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }


    override fun onStart() {
        super.onStart()
        checkRole()
        initStateCard()
        initRulesCard()
        binding.logs.setOnClickListener {
            SpamLogsActivity.start(this)
        }
        binding.about.setOnClickListener {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val inflate = DialogAbortBinding.inflate(layoutInflater)
            inflate.icon.setBackgroundResource(R.drawable.ic_logo)
            inflate.title.setText(R.string.app_name)
            inflate.summary.text = packageInfo.versionName
            AlertDialog.Builder(this).setView(inflate.root).show()
        }
    }

    private val registerRoleResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            viewModel.setState(Stop)
        }
    }

    private fun checkRole() {
        if (!roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)) {
            viewModel.setState(NotSupport)
            return
        }
        if (!roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
            viewModel.setState(NotPermissions)
            return
        }
        if (preferences.getBoolean("enable", false)) {
            viewModel.setState(Running)
            return
        }
        viewModel.setState(Stop)
    }

    private fun initRulesCard() {
        viewModel.whiteCount.observe(this) {
            binding.whiteListSummary.text = "${it}条规则"
        }
        binding.whiteList.setOnClickListener {
            SpamRulesActivity.start(this, SpamRules.WhiteType)
        }
        viewModel.blackCount.observe(this) {
            binding.blackListSummary.text = "${it}条规则"
        }
        binding.blackList.setOnClickListener {
            SpamRulesActivity.start(this, SpamRules.BlackType)
        }
    }

    private fun initStateCard() {
        viewModel.state.observe(this) {
            when (it) {
                Running -> {
                    binding.state.setCardBackgroundColor(getColor(R.color.primaryCardColorStarted))
                    binding.state.setOnClickListener {
                        viewModel.setState(Stop)
                    }
                    binding.stateIcon.setImageResource(R.drawable.ic_baseline_started_24)
                    binding.stateTitle.text = "运行中"
                    binding.stateSummary.text = "轻按关闭"
                    preferences.edit {
                        putBoolean("enable", true)
                    }
                }
                Stop -> {
                    binding.state.setCardBackgroundColor(getColor(R.color.primaryCardColorStopped))
                    binding.state.setOnClickListener {
                        viewModel.setState(Running)
                    }
                    binding.stateIcon.setImageResource(R.drawable.ic_baseline_stopped_24)
                    binding.stateTitle.text = "未开启"
                    binding.stateSummary.text = "轻按开启"
                    preferences.edit {
                        putBoolean("enable", false)
                    }
                }
                NotSupport -> {
                    binding.state.setCardBackgroundColor(getColor(android.R.color.holo_red_dark))
                    binding.state.setOnClickListener {
                        Snackbar.make(binding.root, "设备不支持", Snackbar.LENGTH_LONG).show()
                    }
                    binding.stateIcon.setImageResource(R.drawable.ic_baseline_warning_24)
                    binding.stateTitle.text = "设备不支持"
                    binding.stateSummary.text = "设备不支持"
                }
                NotPermissions -> {
                    binding.state.setCardBackgroundColor(getColor(android.R.color.holo_orange_dark))
                    binding.state.setOnClickListener {
                        registerRoleResult.launch(roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING))
                    }
                    binding.stateIcon.setImageResource(R.drawable.ic_baseline_error_24)
                    binding.stateTitle.text = "未设置相关权限"
                    binding.stateSummary.text = "轻按设置"
                }
                null -> {
                    Snackbar.make(binding.root, "状态异常", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        binding.state.setOnLongClickListener {
            startActivity(Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS));true
        }
    }
}