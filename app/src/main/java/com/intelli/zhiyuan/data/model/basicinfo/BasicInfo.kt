// com/intelli/zhiyuan/data/model/BasicInfo.kt
package com.intelli.zhiyuan.data.model.basicinfo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "basic_info")
data class BasicInfo(
    @PrimaryKey val uid: String,
    val chinese_name: String,
    val english_name: String,
    val chinese_location: String,
    val english_location: String,

    // 改成 Boolean，Room 会自动转换为 INTEGER 存储
    @ColumnInfo(name = "is985")
    val is985: Boolean,

    @ColumnInfo(name = "is211")
    val is211: Boolean,

    @ColumnInfo(name = "has_logo")
    val has_logo: Boolean // Room 会自动转换
)
