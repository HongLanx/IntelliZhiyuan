package com.intelli.zhiyuan.data.local

import androidx.room.Dao
import androidx.room.Query
import com.intelli.zhiyuan.data.model.PredictionEntity

@Dao
interface PredictionDao {
    /** 按省份&类型拿到全部预测记录 */
    @Query("""
    SELECT * FROM predictions
    WHERE province_id = :provinceId
      AND type_id = :typeId
  """)
    suspend fun getByProvinceAndType(
        provinceId: String,
        typeId: String
    ): List<PredictionEntity>

    /** 按省份&类型&范围筛选 min_section_pred */
    @Query("""
    SELECT * FROM predictions
    WHERE province_id = :provinceId
      AND type_id = :typeId
      AND min_section_pred BETWEEN :low AND :high
  """)
    suspend fun getByRange(
        provinceId: String,
        typeId: String,
        low: Int,
        high: Int
    ): List<PredictionEntity>
}
