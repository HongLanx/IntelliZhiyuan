// com/intelli/zhiyuan/data/local/BasicInfoDatabase.kt
package com.intelli.zhiyuan.data.local.specialsinfo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.intelli.zhiyuan.data.model.specialsinfo.LevelOne
import com.intelli.zhiyuan.data.model.specialsinfo.LevelThree
import com.intelli.zhiyuan.data.model.specialsinfo.LevelTwo
import com.intelli.zhiyuan.data.model.specialsinfo.SpecialsInfo

@Database(entities = [LevelOne::class, LevelTwo::class, LevelThree::class, SpecialsInfo::class], version = 1, exportSchema = false)
abstract class SpecialsInfoDatabase : RoomDatabase() {
    abstract fun levelOneDao(): LevelOneDao
    abstract fun levelTwoDao(): LevelTwoDao
    abstract fun levelThreeDao(): LevelThreeDao
    abstract fun specialsInfoDao(): SpecialsInfoDao

    companion object {
        @Volatile
        private var INSTANCE: SpecialsInfoDatabase? = null

        fun getDatabase(context: Context): SpecialsInfoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SpecialsInfoDatabase::class.java,
                    "SpecialsInfo.db"
                ).createFromAsset("databases/specials_info.db")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}