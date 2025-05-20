// com/intelli/zhiyuan/data/local/BasicInfoDao.kt
package com.intelli.zhiyuan.data.local.basicinfo

import androidx.room.Dao
import androidx.room.Query
import com.intelli.zhiyuan.data.model.basicinfo.BasicInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface BasicInfoDao {
    @Query("SELECT * FROM basic_info ORDER BY CAST(uid AS INTEGER) ASC")
    fun getAllBasicInfo(): Flow<List<BasicInfo>>

    @Query("SELECT * FROM basic_info WHERE (:isChinese = 1 AND chinese_name LIKE '%' || :query || '%') OR (:isChinese = 0 AND english_name LIKE '%' || :query || '%') ORDER BY CAST(uid AS INTEGER) ASC")
    fun searchBasicInfo(query: String, isChinese: Boolean): Flow<List<BasicInfo>>
}
