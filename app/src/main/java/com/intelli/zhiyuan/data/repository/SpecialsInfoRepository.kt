package com.intelli.zhiyuan.data.repository

import android.content.Context
import com.intelli.zhiyuan.data.local.specialsinfo.SpecialsInfoDatabase
import com.intelli.zhiyuan.data.model.specialsinfo.LevelOne
import com.intelli.zhiyuan.data.model.specialsinfo.LevelThree
import com.intelli.zhiyuan.data.model.specialsinfo.LevelTwo
import com.intelli.zhiyuan.data.model.specialsinfo.SpecialsInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SpecialsInfoRepository(context: Context) {
    private val database = SpecialsInfoDatabase.getDatabase(context)
    private val levelOneDao = database.levelOneDao()
    private val levelTwoDao = database.levelTwoDao()
    private val levelThreeDao = database.levelThreeDao()
    private val specialsInfoDao =database.specialsInfoDao()

    fun getSpecialsFiltered(query: String, isChinese: Boolean):
            Flow<List<SpecialsInfo>> = flow {
        emit(specialsInfoDao.getSpecialsFiltered(query, isChinese))
    }
    // 新增批量查询接口
    suspend fun getSpecialsByIds(ids: List<String>): List<SpecialsInfo> =
        specialsInfoDao.getSpecialsByIds(ids)
    suspend fun getAllLevelOnes(): List<LevelOne> = levelOneDao.getAllLevelOnes()
    suspend fun getLevelTwosByLevelOne(levelOneId: String): List<LevelTwo> =levelTwoDao.getLevelTwosByLevelOne(levelOneId)
    suspend fun getLevelThreesByLevelTwo(levelTwoId: String): List<LevelThree> = levelThreeDao.getLevelThreesByLevelTwo(levelTwoId)
    suspend fun getByLevelThree(level3Id: String): List<SpecialsInfo> = specialsInfoDao.getByLevelThree(level3Id)
    suspend fun getAllSpecials(): List<SpecialsInfo> = specialsInfoDao.getAllSpecials()
    suspend fun getLevelThreesByLevelTwos(levelTwoIds: List<String>) : List<LevelThree> = levelThreeDao.getLevelThreesByLevelTwos(levelTwoIds)
    suspend fun getByLevelThrees(level3Ids: List<String>): List<SpecialsInfo> = specialsInfoDao.getByLevelThrees(level3Ids)
    suspend fun getSpecialFromId(specialId:String):SpecialsInfo = specialsInfoDao.getSpecialFromId(specialId)


}