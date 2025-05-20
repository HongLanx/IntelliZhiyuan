package com.intelli.zhiyuan.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// BatchesEntity.kt
@Entity(
    tableName = "batches"
)
data class BatchesEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "ch_name") val chName: String,
    @ColumnInfo(name = "en_name") val enName: String
)

// EnrollmentTypeEntity.kt
@Entity(
    tableName = "enrollment_types"
)
data class EnrollmentTypeEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "ch_name") val chName: String,
    @ColumnInfo(name = "en_name") val enName: String
)

// FilterRuleEntity.kt
@Entity(
    tableName = "filter_rules"
)
data class FilterRuleEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "must_include") val mustInclude: String,
    @ColumnInfo(name = "at_least_one") val atLeastOne: String,
    @ColumnInfo(name = "description_eng") val descriptionEng: String
)

// ProvinceEntity.kt
@Entity(
    tableName = "province"
)
data class ProvinceEntity(
    @PrimaryKey @ColumnInfo(name = "province_id") val provinceId: String,
    @ColumnInfo(name = "ch_name") val chName: String,
    @ColumnInfo(name = "en_name") val enName: String
)

// TypeEntity.kt
@Entity(
    tableName = "types"
)
data class TypeEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "ch_name") val chName: String,
    @ColumnInfo(name = "en_name") val enName: String
)

// ProvinceDataEntity.kt
@Entity(
    tableName = "province_data",
    foreignKeys = [
        ForeignKey(
            entity = BatchesEntity::class,
            parentColumns = ["id"],
            childColumns = ["batch_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EnrollmentTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["enrollment_type_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FilterRuleEntity::class,
            parentColumns = ["id"],
            childColumns = ["filter_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("batch_id"),
        Index("enrollment_type_id"),
        Index("filter_id"),
        Index("province_id"),
        Index("type_id")
    ]
)
data class ProvinceDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "school_id") val schoolId: String,
    @ColumnInfo(name = "province_id") val provinceId: String,
    @ColumnInfo(name = "year") val year: String,
    @ColumnInfo(name = "type_id") val typeId: String,
    @ColumnInfo(name = "batch_id") val batchId: String,
    @ColumnInfo(name = "min") val min: Int,
    @ColumnInfo(name = "min_section") val minSection: Int,
    @ColumnInfo(name = "filter_id") val filterId: Int,
    @ColumnInfo(name = "sg_name") val sgName: String,
    @ColumnInfo(name = "special_group") val specialGroup: String,
    @ColumnInfo(name = "enrollment_type_id") val enrollmentTypeId: Int,
    @ColumnInfo(name = "should_ignore") val shouldIgnore :Int
)
