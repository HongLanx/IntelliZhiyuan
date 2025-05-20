package com.intelli.zhiyuan.data.local

import androidx.room.Dao
import androidx.room.Query
import com.intelli.zhiyuan.data.model.SpecialGroupDataEntity

@Dao
interface SpecialGroupDataDao {
    @Query("SELECT * FROM special_group_data WHERE special_group_id = :groupId")
    suspend fun getByGroupId(groupId: String): List<SpecialGroupDataEntity>
}
