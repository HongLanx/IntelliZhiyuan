package com.intelli.zhiyuan.data.local

import androidx.room.Dao
import androidx.room.Query
import com.intelli.zhiyuan.data.model.BatchesEntity
import com.intelli.zhiyuan.data.model.EnrollmentTypeEntity
import com.intelli.zhiyuan.data.model.FilterRuleEntity
import com.intelli.zhiyuan.data.model.ProvinceDataEntity
import com.intelli.zhiyuan.data.model.ProvinceEntity
import com.intelli.zhiyuan.data.model.TypeEntity

// BatchesDao.kt
@Dao
interface BatchesDao {
    @Query("SELECT * FROM batches")
    suspend fun getAllBatches(): List<BatchesEntity>

    @Query("SELECT * FROM batches WHERE id = :id")
    suspend fun getBatchById(id: String): BatchesEntity?
}

// EnrollmentTypeDao.kt
@Dao
interface EnrollmentTypeDao {
    @Query("SELECT * FROM enrollment_types")
    suspend fun getAllEnrollmentTypes(): List<EnrollmentTypeEntity>
}

// FilterRuleDao.kt
@Dao
interface FilterRuleDao {
    @Query("SELECT * FROM filter_rules")
    suspend fun getAllFilterRules(): List<FilterRuleEntity>
}

// ProvinceDao.kt
@Dao
interface ProvinceDao {
    @Query("SELECT * FROM province")
    suspend fun getAllProvinces(): List<ProvinceEntity>

    @Query("SELECT * FROM province WHERE province_id = :id")
    suspend fun getProvinceById(id: String): ProvinceEntity?
}

// TypeDao.kt
@Dao
interface TypeDao {
    @Query("SELECT * FROM types")
    suspend fun getAllTypes(): List<TypeEntity>

    @Query("SELECT * FROM types WHERE id = :id")
    suspend fun getTypeById(id: String): TypeEntity?
}

// ProvinceDataDao.kt
@Dao
interface ProvinceDataDao {

    // —— 新增：一次 JOIN 拿到省份实体，避免先查 id 再循环查实体 ——
    @Query("""
        SELECT p.* FROM province AS p
        INNER JOIN (
            SELECT DISTINCT province_id FROM province_data WHERE school_id = :uid
        ) AS pd ON p.province_id = pd.province_id
    """)
    suspend fun getProvincesForSchoolEntities(uid: String): List<ProvinceEntity>

    @Query("SELECT DISTINCT year FROM province_data WHERE school_id = :uid AND province_id = :provId")
    suspend fun getDistinctYearsForSchoolAndProvince(uid: String, provId: String): List<String>

    @Query("""
      SELECT DISTINCT type_id 
      FROM province_data 
      WHERE school_id = :uid 
        AND province_id = :provId 
        AND year = :year
    """)
    suspend fun getDistinctTypesForSchoolProvinceYear(
        uid: String, provId: String, year: String
    ): List<String>

    @Query("""
      SELECT * FROM province_data 
      WHERE school_id = :uid 
        AND province_id = :provId 
        AND year = :year 
        AND type_id = :typeId
    """)
    suspend fun getDataForSchoolProvYearType(
        uid: String, provId: String, year: String, typeId: String
    ): List<ProvinceDataEntity>

    /** 只拿 should_ignore != 1 的记录 */
    @Query("""
      SELECT * FROM province_data
      WHERE year = :year
        AND province_id = :provinceId
        AND type_id = :typeId
        AND should_ignore != 1
    """)
    suspend fun getNonIgnoredData(
        year: String,
        provinceId: String,
        typeId: String
    ): List<ProvinceDataEntity>

    @Query("""
    SELECT * FROM province_data
    WHERE year = :year
      AND province_id = :provinceId
      AND type_id = :typeId
      AND should_ignore = 0
      AND min BETWEEN :low AND :high
  """)
    suspend fun getRecommendDataInRange(
        year: String,
        provinceId: String,
        typeId: String,
        low: Int,
        high: Int
    ): List<ProvinceDataEntity>
}

