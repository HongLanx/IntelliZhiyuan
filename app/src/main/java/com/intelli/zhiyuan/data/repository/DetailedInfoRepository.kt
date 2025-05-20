package com.intelli.zhiyuan.data.repository

import android.content.Context
import com.intelli.zhiyuan.data.local.DetailedInfoDatabase
import com.intelli.zhiyuan.data.model.DetailedInfo

class DetailedInfoRepository(context: Context) {
    private val dao = DetailedInfoDatabase.getDatabase(context).detailedInfoDao()

    suspend fun getDetailedInfo(uid: String): DetailedInfo? =
        dao.getDetailedInfo(uid)
}
