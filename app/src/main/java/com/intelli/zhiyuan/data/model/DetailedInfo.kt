package com.intelli.zhiyuan.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detailed_info")
data class DetailedInfo(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "chinese_name") val chineseName: String,
    @ColumnInfo(name = "english_name") val englishName: String,
    @ColumnInfo(name = "chinese_location") val chineseLocation: String,
    @ColumnInfo(name = "english_location") val englishLocation: String,
    @ColumnInfo(name = "is985") val is985: Boolean,
    @ColumnInfo(name = "is211") val is211: Boolean,
    @ColumnInfo(name = "has_logo") val hasLogo: Boolean,
    @ColumnInfo(name = "school_website") val schoolWebsite: String?,
    @ColumnInfo(name = "is_national_featured") val isNationalFeatured: String,
    @ColumnInfo(name = "is_provincial_featured") val isProvincialFeatured: String,
    @ColumnInfo(name = "is_nation_first_class") val isNationFirstClass: String,
    @ColumnInfo(name = "rank_A") val rankA: String,
    @ColumnInfo(name = "rank_B") val rankB: String,
    @ColumnInfo(name = "rank_C") val rankC: String
)
