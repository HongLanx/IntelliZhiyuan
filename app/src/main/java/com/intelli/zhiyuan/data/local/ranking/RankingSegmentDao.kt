package com.intelli.zhiyuan.data.local.ranking

import androidx.room.Dao
import androidx.room.Query
import com.intelli.zhiyuan.data.model.ranking.RankingSegment

@Dao
interface RankingSegmentDao {
    @Query("SELECT * FROM ranking_segment WHERE exam_id = :examId")
    suspend fun getRankingSegmentsByExamId(examId: Int): List<RankingSegment>

    @Query("SELECT * FROM ranking_segment WHERE exam_id = :examId AND input_score = :inputScore LIMIT 1")
    suspend fun getRankingSegmentForScore(examId: Int, inputScore: String): RankingSegment?
}
