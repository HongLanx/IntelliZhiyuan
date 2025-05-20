package com.intelli.zhiyuan.data.local.ranking

import androidx.room.Dao
import androidx.room.Query
import com.intelli.zhiyuan.data.model.ranking.Province

@Dao
interface ProvinceDao {
    @Query("SELECT * FROM province")
    suspend fun getAllProvinces(): List<Province>
    @Query("SELECT * FROM province WHERE province_id = :provinceId LIMIT 1")
    suspend fun getProvince(provinceId: String): Province
}
