package com.intelli.zhiyuan.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.intelli.zhiyuan.data.model.PredictionEntity

@Database(entities = [PredictionEntity::class], version = 1, exportSchema = false)
abstract class PredictionDatabase : RoomDatabase() {
    abstract fun predictionDao(): PredictionDao

    companion object {
        @Volatile private var INSTANCE: PredictionDatabase? = null
        fun getDatabase(context: Context): PredictionDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    PredictionDatabase::class.java,
                    "Predictions.db"
                )
                    .createFromAsset("databases/predictions.db")
                    .build().also { INSTANCE = it }
            }
    }
}
