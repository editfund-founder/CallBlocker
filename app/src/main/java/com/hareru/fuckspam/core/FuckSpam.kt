package com.hareru.fuckspam.core

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import com.hareru.fuckspam.R
import com.hareru.fuckspam.database.AppDatabase
import com.hareru.fuckspam.database.entity.SpamLogs
import com.hareru.fuckspam.database.entity.SpamRules
import kotlinx.coroutines.*
import java.util.*

class FuckSpam : CallScreeningService() {
    private val job = Job()
    private val scope = CoroutineScope(job)

    override fun onScreenCall(callDetails: Call.Details) {
        val startTime = Date().time
        val spamDao = AppDatabase.getInstance(this).spamRulesDao()
        val logsDao = AppDatabase.getInstance(this).spamLogsDao()
        val phone = callDetails.handle.schemeSpecificPart
        val time = callDetails.creationTimeMillis
        scope.launch(Dispatchers.IO) {
            val isEnableDeferred = async {
                val preferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
                preferences.getBoolean("enable", false)
            }
            val isWhiteDeferred = async {
                spamDao.spamRulesList(SpamRules.WhiteType).find {
                    Regex(it.rules).matches(phone)
                }
            }
            val isBlackDeferred = async {
                spamDao.spamRulesList(SpamRules.BlackType).find {
                    Regex(it.rules).matches(phone)
                }
            }
            val builder = CallResponse.Builder()
            val isEnable = isEnableDeferred.await()
            val white = isWhiteDeferred.await()
            val black = isBlackDeferred.await()
            if (!isEnable) {
                logsDao.insert(SpamLogs(phone, time, "放行:拦截功能未开启"))
            } else if (isEnable && white != null) {
                logsDao.insert(SpamLogs(phone, time, "放行:白名单规则:${white.rules}"))
            } else if (isEnable && black != null) {
                builder.setDisallowCall(true)
                builder.setRejectCall(true)
                builder.setSkipCallLog(true)
                builder.setSkipNotification(true)
                logsDao.insert(SpamLogs(phone, time, "拦截:黑名单规则:${black.rules}"))
            } else {
                logsDao.insert(SpamLogs(phone, time, "放行:非名单规则"))
            }
            respondToCall(callDetails, builder.build())
            Log.d("TAG", "onScreenCall: $phone > ${Date().time - startTime}")
        }
    }
}