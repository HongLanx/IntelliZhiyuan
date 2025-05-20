package com.intelli.zhiyuan.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.intelli.zhiyuan.data.model.SpecialDataEntity

// 3. 定义 RoomDatabase —— 指向 special_data.db
@Database(
    entities = [ SpecialDataEntity::class ],
    version = 1,
    exportSchema = false
)
abstract class SpecialDataDatabase : RoomDatabase() {
    abstract fun specialDataDao(): SpecialDataDao

    companion object {
        @Volatile
        private var INSTANCE: SpecialDataDatabase? = null

        fun getDatabase(context: Context): SpecialDataDatabase {
            return INSTANCE ?: synchronized(this) {
                val inst = Room.databaseBuilder(
                    context.applicationContext,
                    SpecialDataDatabase::class.java,
                    "special_data.db"
                )
                    .createFromAsset("databases/special_data.db")
                    .build()
                INSTANCE = inst
                inst
            }
        }
    }
}
