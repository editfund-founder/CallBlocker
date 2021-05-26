package com.hareru.fuckspam.ui.log

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.hareru.fuckspam.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SpamLogsViewModel(application: Application) : AndroidViewModel(application) {
    private val logsDao = AppDatabase.getInstance(application).spamLogsDao()
    val logs = Pager(PagingConfig(20)) { logsDao.allLogsPage() }
            .flow
            .cachedIn(viewModelScope)
            .asLiveData()

    fun clearLogs() {
        viewModelScope.launch(Dispatchers.IO) {
            logsDao.clearLogs()
        }
    }

}
