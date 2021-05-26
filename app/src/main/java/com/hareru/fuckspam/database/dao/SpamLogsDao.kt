package com.hareru.fuckspam.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hareru.fuckspam.database.entity.SpamLogs

@Dao
interface SpamLogsDao {
    @Insert
    fun insert(spam: SpamLogs)

    @Query("SELECT * FROM SpamLogs ORDER BY time DESC")
    fun allLogsPage(): PagingSource<Int, SpamLogs>

    @Query("DELETE FROM SpamLogs")
    fun clearLogs()
}