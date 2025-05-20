package com.intelli.zhiyuan.data.repository

import android.content.Context
import com.intelli.zhiyuan.data.local.SpecialGroupDataDatabase
import com.intelli.zhiyuan.data.model.SpecialGroupDataEntity

class SpecialGroupDataRepository(context: Context) {
    private val dao = SpecialGroupDataDatabase.getDatabase(context).specialGroupDataDao()

    suspend fun getGroupItems(groupId: String): List<SpecialGroupDataEntity> =
        dao.getByGroupId(groupId)
}
