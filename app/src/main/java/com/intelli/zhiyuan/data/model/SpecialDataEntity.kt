package com.intelli.zhiyuan.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// 1. 定义 Entity —— 只为 special_data 表
@Entity(
    tableName = "special_data",
    indices = [
        Index("school_id"),
        Index("province_id"),
        Index("year"),
        Index("type_id"),
        Index("batch_id"),
        Index("filter_id")
    ]
)
data class SpecialDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = 0,
    @ColumnInfo("school_id") val schoolId: Int?,
    @ColumnInfo("province_id") val provinceId: Int?,
    @ColumnInfo("year")       val year: Int?,
    @ColumnInfo("type_id")    val typeId: Int?,
    @ColumnInfo("batch_id")   val batchId: Int?,
    @ColumnInfo("min")        val min: Int?,
    @ColumnInfo("min_section")val minSection: Int?,
    @ColumnInfo("filter_id")  val filterId: Int?,
    @ColumnInfo("special_id") val specialId: Int?
)
