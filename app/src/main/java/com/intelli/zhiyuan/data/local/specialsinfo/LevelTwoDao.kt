package com.intelli.zhiyuan.data.local.specialsinfo

import androidx.room.Dao
import androidx.room.Query
import com.intelli.zhiyuan.data.model.specialsinfo.LevelTwo

@Dao
interface LevelTwoDao {
    @Query("SELECT * FROM second_levels WHERE level1_id = :levelOneId")
    suspend fun getLevelTwosByLevelOne(levelOneId: String): List<LevelTwo>
}