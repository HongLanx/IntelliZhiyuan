// com/intelli/zhiyuan/data/repository/BasicInfoRepository.kt
package com.intelli.zhiyuan.data.repository

import android.content.Context
import com.intelli.zhiyuan.data.local.basicinfo.BasicInfoDatabase
import com.intelli.zhiyuan.data.model.basicinfo.BasicInfo
import kotlinx.coroutines.flow.Flow

class BasicInfoRepository(context: Context) {
    private val basicInfoDao = BasicInfoDatabase.getDatabase(context).basicInfoDao()

    fun getAllBasicInfo(): Flow<List<BasicInfo>> = basicInfoDao.getAllBasicInfo()

    fun searchBasicInfo(query: String, isChinese: Boolean): Flow<List<BasicInfo>> =
        basicInfoDao.searchBasicInfo(query, isChinese)
}

