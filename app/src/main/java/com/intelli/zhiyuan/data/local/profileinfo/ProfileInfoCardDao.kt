package com.intelli.zhiyuan.data.local.profileinfo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.intelli.zhiyuan.data.model.profileinfo.ProfileInfoCardEntity

@Dao
interface ProfileInfoCardDao {
    @Query("SELECT * FROM profile_info_card")
    suspend fun getAllCards(): List<ProfileInfoCardEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: ProfileInfoCardEntity)

    @Delete
    suspend fun deleteCard(card: ProfileInfoCardEntity)
}
