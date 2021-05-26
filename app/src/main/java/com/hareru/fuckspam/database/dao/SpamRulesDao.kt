package com.hareru.fuckspam.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.hareru.fuckspam.database.entity.SpamRules

@Dao
interface SpamRulesDao {
    @Insert
    fun insert(spam: SpamRules)

    @Delete
    fun delete(spam: SpamRules)

    @Query("SELECT * FROM SpamRules WHERE rulesType = :rulesType")
    fun spamRulesPage(rulesType: Int): PagingSource<Int, SpamRules>

    @Query("SELECT * FROM SpamRules WHERE rulesType = :rulesType")
    fun spamRulesList(rulesType: Int): List<SpamRules>

    @Query("SELECT COUNT() FROM SpamRules WHERE rulesType = :rulesType")
    fun count(rulesType: Int): LiveData<Int>

}