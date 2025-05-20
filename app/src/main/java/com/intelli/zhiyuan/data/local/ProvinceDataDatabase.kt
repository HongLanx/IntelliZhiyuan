package com.intelli.zhiyuan.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.intelli.zhiyuan.data.model.BatchesEntity
import com.intelli.zhiyuan.data.model.EnrollmentTypeEntity
import com.intelli.zhiyuan.data.model.FilterRuleEntity
import com.intelli.zhiyuan.data.model.ProvinceDataEntity
import com.intelli.zhiyuan.data.model.ProvinceEntity
import com.intelli.zhiyuan.data.model.TypeEntity

// ProvinceDataDatabase.kt
@Database(
    entities = [
        BatchesEntity::class,
        EnrollmentTypeEntity::class,
        FilterRuleEntity::class,
        ProvinceEntity::class,
        TypeEntity::class,
        ProvinceDataEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ProvinceDataDatabase : RoomDatabase() {
    abstract fun batchesDao(): BatchesDao
    abstract fun enrollmentTypeDao(): EnrollmentTypeDao
    abstract fun filterRuleDao(): FilterRuleDao
    abstract fun provinceDao(): ProvinceDao
    abstract fun typeDao(): TypeDao
    abstract fun provinceDataDao(): ProvinceDataDao

    companion object {
        @Volatile private var INSTANCE: ProvinceDataDatabase? = null

        fun getDatabase(context: Context): ProvinceDataDatabase {
            return INSTANCE ?: synchronized(this) {
                val inst = Room.databaseBuilder(
                    context.applicationContext,
                    ProvinceDataDatabase::class.java,
                    "ProvinceData.db"
                )
                    .createFromAsset("databases/province_data.db")
                    .build()
                INSTANCE = inst
                inst
            }
        }
    }
}
