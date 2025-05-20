package com.intelli.zhiyuan.data.model.specialsinfo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "first_levels")
data class LevelOne(
    @PrimaryKey val level_id: String,
    val name_ch: String,
    val name_en: String
)