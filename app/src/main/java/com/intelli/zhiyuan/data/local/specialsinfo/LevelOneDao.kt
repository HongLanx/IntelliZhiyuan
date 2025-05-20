package com.intelli.zhiyuan.data.local.specialsinfo

import androidx.room.Dao
import androidx.room.Query
import com.intelli.zhiyuan.data.model.specialsinfo.LevelOne

@Dao
interface LevelOneDao {
    @Query("SELECT * FROM first_levels")
    suspend fun getAllLevelOnes(): List<LevelOne>
}