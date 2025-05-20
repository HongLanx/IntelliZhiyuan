package com.intelli.zhiyuan.data.model.specialsinfo

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "second_levels",
    foreignKeys = [
        ForeignKey(
            entity = LevelOne::class,
            parentColumns = ["level_id"],
            childColumns = ["level1_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ])
data class LevelTwo(
    @PrimaryKey val level_id: String,
    val name_ch: String,
    val name_en: String,
    val level1_id: String
)