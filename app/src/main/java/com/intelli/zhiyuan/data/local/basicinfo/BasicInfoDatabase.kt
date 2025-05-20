// com/intelli/zhiyuan/data/local/BasicInfoDatabase.kt
package com.intelli.zhiyuan.data.local.basicinfo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.intelli.zhiyuan.data.model.basicinfo.BasicInfo

@Database(entities = [BasicInfo::class], version = 1, exportSchema = true)
abstract class BasicInfoDatabase : RoomDatabase() {
    abstract fun basicInfoDao(): BasicInfoDao

    companion object {
        @Volatile
        private var INSTANCE: BasicInfoDatabase? = null

        fun getDatabase(context: Context): BasicInfoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BasicInfoDatabase::class.java,
                    "basic_info.db"
                )
                    // 假设数据库文件放在 assets/databases/basicInfo.db
                    .createFromAsset("databases/basicInfo.db")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
