package com.hareru.fuckspam.ui.rules

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.hareru.fuckspam.database.AppDatabase
import com.hareru.fuckspam.database.entity.SpamRules
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SpamRulesViewModel(application: Application) : AndroidViewModel(application) {

    private val rulesType = MutableLiveData<Int>()
    fun setSpamType(type: Int) {
        rulesType.value = type
    }

    private val dao = AppDatabase.getInstance(application).spamRulesDao()
    val rules = rulesType.switchMap {
        Pager(config = PagingConfig(pageSize = 20)) { dao.spamRulesPage(it) }
                .flow
                .cachedIn(viewModelScope)
                .asLiveData()
    }

    fun addSpam(s: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(SpamRules(rulesType.value!!, s))
        }
    }

}


