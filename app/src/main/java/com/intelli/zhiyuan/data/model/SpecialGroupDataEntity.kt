package com.intelli.zhiyuan.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "special_group_data")
data class SpecialGroupDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "special_group_id") val specialGroupId: String,
    @ColumnInfo(name = "spcode") val spcode: String,
    @ColumnInfo(name = "sp_id") val spId: String,
    @ColumnInfo(name = "remark") val remark: String?
)
