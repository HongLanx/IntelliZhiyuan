package com.intelli.zhiyuan.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "predictions",
)
data class PredictionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "school_id") val schoolId: String,
    @ColumnInfo(name = "province_id") val provinceId: String,
    @ColumnInfo(name = "type_id") val typeId: String,
    @ColumnInfo(name = "batch_id") val batchId: String,
    @ColumnInfo(name = "min_section_pred") val minSectionPred: Int
)
