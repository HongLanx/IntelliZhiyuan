package com.intelli.zhiyuan.data.model.specialsinfo

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "third_levels",
    foreignKeys = [
        ForeignKey(
            entity = LevelTwo::class,
            parentColumns = ["level_id"],
            childColumns = ["level2_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ])
data class LevelThree(
    @PrimaryKey val level_id: String,
    val name_ch: String,
    val name_en: String,
    val level2_id: String
)