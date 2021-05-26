package com.hareru.fuckspam.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hareru.fuckspam.database.AppDatabase
import com.hareru.fuckspam.database.entity.SpamRules

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).spamRulesDao()

    var whiteCount = dao.count(SpamRules.WhiteType)
    var blackCount = dao.count(SpamRules.BlackType)

    private val _state = MutableLiveData<EnableState>()
    val state: LiveData<EnableState> = _state
    fun setState(state: EnableState) {
        _state.value = state
    }
}

