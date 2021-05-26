package com.hareru.fuckspam.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hareru.fuckspam.database.dao.SpamLogsDao
import com.hareru.fuckspam.database.dao.SpamRulesDao
import com.hareru.fuckspam.database.entity.SpamLogs
import com.hareru.fuckspam.database.entity.SpamRules

@Database(entities = [SpamRules::class, SpamLogs::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun spamRulesDao(): SpamRulesDao
    abstract fun spamLogsDao(): SpamLogsDao

    companion object {
        private lateinit var instance: AppDatabase

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (!Companion::instance.isInitialized) {
                instance = Room.databaseBuilder(context, AppDatabase::class.java, "data.db").build()
            }
            return instance
        }
    }
}
