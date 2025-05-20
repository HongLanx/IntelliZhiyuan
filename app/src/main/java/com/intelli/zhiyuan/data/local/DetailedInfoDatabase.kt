package com.intelli.zhiyuan.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.intelli.zhiyuan.data.model.DetailedInfo

@Database(
    entities = [DetailedInfo::class],
    version = 1,
    exportSchema = false
)
abstract class DetailedInfoDatabase : RoomDatabase() {
    abstract fun detailedInfoDao(): DetailedInfoDao

    companion object {
        @Volatile
        private var INSTANCE: DetailedInfoDatabase? = null

        fun getDatabase(context: Context): DetailedInfoDatabase {
            return INSTANCE ?: synchronized(this) {
                val inst = Room.databaseBuilder(
                    context.applicationContext,
                    DetailedInfoDatabase::class.java,
                    "DetailedInfo.db"
                )
                    .createFromAsset("databases/detailed_info.db")
                    .build()
                INSTANCE = inst
                inst
            }
        }
    }
}
