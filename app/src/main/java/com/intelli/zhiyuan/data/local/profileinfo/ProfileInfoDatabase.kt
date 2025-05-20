package com.intelli.zhiyuan.data.local.profileinfo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.intelli.zhiyuan.data.model.profileinfo.ProfileInfoCardEntity

@Database(entities = [ProfileInfoCardEntity::class], version = 1, exportSchema = false)
abstract class ProfileInfoDatabase : RoomDatabase() {
    abstract fun profileInfoCardDao(): ProfileInfoCardDao

    companion object {
        @Volatile
        private var INSTANCE: ProfileInfoDatabase? = null

        fun getDatabase(context: Context): ProfileInfoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProfileInfoDatabase::class.java,
                    "profile_info_card_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
