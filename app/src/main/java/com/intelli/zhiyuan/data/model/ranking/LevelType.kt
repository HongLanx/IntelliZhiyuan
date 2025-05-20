package com.intelli.zhiyuan.data.model.ranking

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "level_type",
    indices = [
        Index(value = ["ch_name"], unique = true),
        Index(value = ["en_name"], unique = true)
    ])
data class LevelType(
    @PrimaryKey val level: Int,
    val ch_name: String,
    val en_name: String
)
