package com.hareru.fuckspam.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SpamLogs(
        val phone: String,
        val time: Long,
        val msg: String,
        @PrimaryKey(autoGenerate = true)
        val uid: Int = 0,
)
