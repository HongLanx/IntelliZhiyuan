package com.intelli.zhiyuan.data.local.ranking

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.intelli.zhiyuan.data.model.ranking.Exam
import com.intelli.zhiyuan.data.model.ranking.LevelType
import com.intelli.zhiyuan.data.model.ranking.Province
import com.intelli.zhiyuan.data.model.ranking.RankingSegment
import com.intelli.zhiyuan.data.model.ranking.TrackType

@Database(
    entities = [Exam::class, RankingSegment::class, Province::class, LevelType::class, TrackType::class],
    version = 1,
    exportSchema = false
)
abstract class RankingDatabase : RoomDatabase() {
    abstract fun examDao(): ExamDao
    abstract fun rankingSegmentDao(): RankingSegmentDao
    abstract fun provinceDao(): ProvinceDao
    abstract fun levelTypeDao(): LevelTypeDao
    abstract fun trackTypeDao(): TrackTypeDao

    companion object {
        @Volatile
        private var INSTANCE: RankingDatabase? = null

        fun getDatabase(context: Context): RankingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RankingDatabase::class.java,
                    "ranking1.db"
                )
                    .createFromAsset("databases/ranking.db")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
