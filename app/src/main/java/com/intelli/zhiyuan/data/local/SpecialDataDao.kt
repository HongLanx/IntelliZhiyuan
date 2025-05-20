package com.intelli.zhiyuan.data.local

import androidx.room.Dao
import androidx.room.Query
import com.intelli.zhiyuan.data.model.SpecialDataEntity

@Dao
interface SpecialDataDao {
    @Query("SELECT DISTINCT province_id FROM special_data WHERE school_id = :schoolId")
    suspend fun getDistinctProvincesForSchool(schoolId: Int): List<Int>



    @Query("""
    SELECT DISTINCT year 
      FROM special_data 
     WHERE school_id = :schoolId 
       AND province_id = :provinceId
  """)
    suspend fun getDistinctYearsForSchoolAndProvince(
        schoolId: Int, provinceId: Int
    ): List<Int>

    @Query("""
    SELECT DISTINCT type_id 
      FROM special_data 
     WHERE school_id = :schoolId 
       AND province_id = :provinceId 
       AND year = :year
  """)
    suspend fun getDistinctTypesForSchoolProvinceYear(
        schoolId: Int, provinceId: Int, year: Int
    ): List<Int>

    @Query("""
    SELECT DISTINCT batch_id 
      FROM special_data 
     WHERE school_id = :schoolId 
       AND province_id = :provinceId 
       AND year = :year 
       AND type_id = :typeId
  """)
    suspend fun getDistinctBatchesForSchoolProvYearType(
        schoolId: Int, provinceId: Int, year: Int, typeId: Int
    ): List<Int>

    @Query("""
    SELECT * 
      FROM special_data 
     WHERE school_id = :schoolId 
       AND province_id = :provinceId 
       AND year = :year 
       AND type_id = :typeId 
       AND batch_id = :batchId
  """)
    suspend fun getDataForSchoolProvYearTypeBatch(
        schoolId: Int, provinceId: Int, year: Int, typeId: Int, batchId: Int
    ): List<SpecialDataEntity>
}
