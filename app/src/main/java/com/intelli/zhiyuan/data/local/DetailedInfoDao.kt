package com.intelli.zhiyuan.data.local

import androidx.room.Dao
import androidx.room.Query
import com.intelli.zhiyuan.data.model.DetailedInfo

@Dao
interface DetailedInfoDao {
    @Query("SELECT * FROM detailed_info WHERE uid = :uid")
    suspend fun getDetailedInfo(uid: String): DetailedInfo?
}
