package com.intelli.zhiyuan.data.model.ranking

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "ranking_segment",
        foreignKeys = [
    ForeignKey(
        entity = Exam::class,
        parentColumns = ["id"],
        childColumns = ["exam_id"],
        onDelete = ForeignKey.NO_ACTION,
        onUpdate = ForeignKey.NO_ACTION
    )])
data class RankingSegment(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val exam_id: Int,
    val input_score: String,
    val score_range: String,
    val num: Int,
    val total: Int,
    val rank_range: String
)
