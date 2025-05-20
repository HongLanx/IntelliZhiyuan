package com.intelli.zhiyuan.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.intelli.zhiyuan.data.model.SpecialGroupDataEntity

@Database(
    entities = [SpecialGroupDataEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SpecialGroupDataDatabase : RoomDatabase() {
    abstract fun specialGroupDataDao(): SpecialGroupDataDao

    companion object {
        @Volatile
        private var INSTANCE: SpecialGroupDataDatabase? = null

        fun getDatabase(context: Context): SpecialGroupDataDatabase {
            return INSTANCE ?: synchronized(this) {
                val inst = Room.databaseBuilder(
                    context.applicationContext,
                    SpecialGroupDataDatabase::class.java,
                    "special_groupData.db"
                )
                    .createFromAsset("databases/special_group_data.db")
                    .build()
                INSTANCE = inst
                inst
            }
        }
    }
}
