package com.intelli.zhiyuan.data.model.specialsinfo

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "specials",
    foreignKeys = [
        ForeignKey(
            entity = LevelThree::class,
            parentColumns = ["level_id"],
            childColumns = ["level3_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ])
data class SpecialsInfo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val special_id: String,
    val spcode: String,
    val name_ch: String,
    val name_en: String,
    val level3_id: String
)