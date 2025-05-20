package com.intelli.zhiyuan.data.model.ranking

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "track_type",
    indices = [
        Index(value = ["ch_name"], unique = true),
        Index(value = ["en_name"], unique = true)
    ])
data class TrackType(
    @PrimaryKey val track: String,
    val ch_name: String,
    val en_name: String
)
