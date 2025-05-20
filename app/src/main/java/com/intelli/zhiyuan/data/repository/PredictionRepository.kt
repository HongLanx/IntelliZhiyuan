package com.intelli.zhiyuan.data.repository

import android.content.Context
import com.intelli.zhiyuan.data.local.PredictionDatabase
import com.intelli.zhiyuan.data.model.PredictionEntity

class PredictionRepository(context: Context) {
    private val dao = PredictionDatabase.getDatabase(context).predictionDao()

    /** 去重：同一 schoolId 只保留 first */
    suspend fun getPredictionsInRange(
        provinceId: String,
        typeId: String,
        low: Int,
        high: Int
    ): List<PredictionEntity> {
        val raw = dao.getByRange(provinceId, typeId, low, high)
        return raw.groupBy { it.schoolId }
            .map { it.value.first() }
    }
}
