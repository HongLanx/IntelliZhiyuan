package com.intelli.zhiyuan.data.repository

import android.content.Context
import com.intelli.zhiyuan.data.local.ProvinceDataDatabase
import com.intelli.zhiyuan.data.local.SpecialDataDatabase
import com.intelli.zhiyuan.data.model.SpecialDataEntity

// SpecialDataRepository.k

class SpecialDataRepository(context: Context) {
    private val db = SpecialDataDatabase.getDatabase(context)
    private val dao = db.specialDataDao()

    /** MajorScores 省份 */
    suspend fun getMajorProvinces(schoolId: Int): List<Int> =
        dao.getDistinctProvincesForSchool(schoolId)

    suspend fun getMajorYears(schoolId: Int, provId: Int): List<Int> =
        dao.getDistinctYearsForSchoolAndProvince(schoolId, provId)

    suspend fun getMajorTypes(schoolId: Int, provId: Int, year: Int): List<Int> =
        dao.getDistinctTypesForSchoolProvinceYear(schoolId, provId, year)

    suspend fun getMajorBatches(
        schoolId: Int, provId: Int, year: Int, typeId: Int
    ): List<Int> =
        dao.getDistinctBatchesForSchoolProvYearType(schoolId, provId, year, typeId)

    suspend fun getMajorData(
        schoolId: Int, provId: Int, year: Int, typeId: Int, batchId: Int
    ): List<SpecialDataEntity> =
        dao.getDataForSchoolProvYearTypeBatch(schoolId, provId, year, typeId, batchId)
}

