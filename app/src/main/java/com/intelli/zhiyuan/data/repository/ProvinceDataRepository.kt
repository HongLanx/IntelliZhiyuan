package com.intelli.zhiyuan.data.repository

import android.content.Context
import com.intelli.zhiyuan.data.local.ProvinceDataDatabase
import com.intelli.zhiyuan.data.model.BatchesEntity
import com.intelli.zhiyuan.data.model.EnrollmentTypeEntity
import com.intelli.zhiyuan.data.model.FilterRuleEntity
import com.intelli.zhiyuan.data.model.ProvinceDataEntity
import com.intelli.zhiyuan.data.model.ProvinceEntity
import com.intelli.zhiyuan.data.model.TypeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ProvinceDataRepository(context: Context) {
    private val db = ProvinceDataDatabase.getDatabase(context)
    private val batchesDao = db.batchesDao()
    private val enrollmentDao = db.enrollmentTypeDao()
    private val filterDao = db.filterRuleDao()
    private val provinceDao = db.provinceDao()
    private val typeDao = db.typeDao()
    private val dataDao = db.provinceDataDao()

    // 内存缓存
    private lateinit var batchMap: Map<String, BatchesEntity>
    private lateinit var enrollmentTypeMap: Map<Int, EnrollmentTypeEntity>
    private lateinit var filterRuleMap: Map<Int, FilterRuleEntity>
    private lateinit var typeMap: Map<String, TypeEntity>

    /** 一次性加载所有 lookup 表到内存 */
    suspend fun loadAllLookups() = withContext(Dispatchers.IO) {
        batchMap = batchesDao.getAllBatches().associateBy { it.id }
        enrollmentTypeMap = enrollmentDao.getAllEnrollmentTypes().associateBy { it.id }
        filterRuleMap = filterDao.getAllFilterRules().associateBy { it.id }
        typeMap = typeDao.getAllTypes().associateBy { it.id }
    }

    /** 获取历史省份列表 */
    suspend fun getHistoryProvinces(uid: String): List<ProvinceEntity> =
        dataDao.getProvincesForSchoolEntities(uid)

    suspend fun getHistoryYears(uid: String, provId: String): List<String> =
        dataDao.getDistinctYearsForSchoolAndProvince(uid, provId)

    suspend fun getHistoryTypes(uid: String, provId: String, year: String): List<TypeEntity> {
        val ids = dataDao.getDistinctTypesForSchoolProvinceYear(uid, provId, year)
        return ids.mapNotNull { typeMap[it] }
    }

    suspend fun getHistoryData(uid: String, provId: String, year: String, typeId: String) =
        dataDao.getDataForSchoolProvYearType(uid, provId, year, typeId)

    /** lookup 查询 */
    fun getBatchById(id: String): BatchesEntity? = batchMap[id]
    fun getEnrollmentTypeById(id: Int): EnrollmentTypeEntity? = enrollmentTypeMap[id]
    fun getFilterRuleById(id: Int): FilterRuleEntity? = filterRuleMap[id]
    suspend fun getProvinceById(id: String): ProvinceEntity? =
        provinceDao.getProvinceById(id)
    fun getTypeById(id: String): TypeEntity? = typeMap[id]

    suspend fun getPreferredProvinceData(
        year: String,
        provinceId: String,
        typeId: String
    ): List<ProvinceDataEntity> = withContext(Dispatchers.IO) {
        val raw = dataDao.getNonIgnoredData(year, provinceId, typeId)
        // 去重：同一 schoolId 只保留 first 出现的
        raw.groupBy { it.schoolId }
            .map { it.value.first() }
    }

    suspend fun getRecommendProvinceData(
        year: String,
        provinceId: String,
        typeId: String,
        examScore: Int
    ): List<ProvinceDataEntity> = withContext(Dispatchers.IO) {
        val raw = dataDao.getRecommendDataInRange(
            year,
            provinceId,
            typeId,
            examScore - 8,
            examScore + 8
        )
        raw.groupBy { it.schoolId }.map { it.value.first() }
    }
}
