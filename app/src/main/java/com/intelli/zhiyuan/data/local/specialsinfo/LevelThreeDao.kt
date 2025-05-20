package com.intelli.zhiyuan.data.local.specialsinfo

import androidx.room.Dao
import androidx.room.Query
import com.intelli.zhiyuan.data.model.specialsinfo.LevelThree

@Dao
interface LevelThreeDao {
    @Query("SELECT * FROM third_levels WHERE level2_id = :levelTwoId")
    suspend fun getLevelThreesByLevelTwo(levelTwoId: String): List<LevelThree>


    @Query("SELECT * FROM third_levels WHERE level2_id IN (:levelTwoIds)")
    suspend fun getLevelThreesByLevelTwos(levelTwoIds: List<String>) : List<LevelThree>
}