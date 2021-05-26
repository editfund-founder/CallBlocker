package com.hareru.fuckspam.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SpamRules(
        val rulesType: Int,
        val rules: String,
        @PrimaryKey(autoGenerate = true)
        val uid: Int = 0,
) {
    companion object {
        const val WhiteType = 1
        const val BlackType = 2

    }
}
