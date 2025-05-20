package com.intelli.zhiyuan.data.model.ranking

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exam",
    foreignKeys = [
        ForeignKey(
            entity = Province::class,
            parentColumns = ["province_id"],
            childColumns = ["province_id"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = TrackType::class,
            parentColumns = ["track"],
            childColumns = ["track"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = LevelType::class,
            parentColumns = ["level"],
            childColumns = ["level"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["province_id", "year", "track", "level"], unique = true)]
)
data class Exam(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val province_id: String,
    val year: String,
    val track: String,
    val level: String
)