package com.intelli.zhiyuan.data.local.ranking

import androidx.room.Dao
import androidx.room.Query
import com.intelli.zhiyuan.data.model.ranking.LevelType

@Dao
interface LevelTypeDao {
    @Query("SELECT * FROM level_type")
    suspend fun getAllLevelTypes(): List<LevelType>

    @Query("SELECT * FROM level_type WHERE level IN (:levelIds)")
    suspend fun getLevelTypes(levelIds: List<Int>): List<LevelType>

    @Query("SELECT * FROM level_type WHERE level = :levelIds LIMIT 1")
    suspend fun getLevelType(levelIds: Int): LevelType
}
