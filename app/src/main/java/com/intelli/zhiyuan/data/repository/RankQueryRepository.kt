package com.intelli.zhiyuan.data.repository

import android.content.Context
import android.util.Log
import com.intelli.zhiyuan.data.local.ranking.RankingDatabase
import com.intelli.zhiyuan.data.model.ranking.Exam
import com.intelli.zhiyuan.data.model.ranking.LevelType
import com.intelli.zhiyuan.data.model.ranking.RankingSegment
import com.intelli.zhiyuan.data.model.ranking.Province
import com.intelli.zhiyuan.data.model.ranking.TrackType
const val TAG="RankQueryRepository"
class RankQueryRepository(context: Context) {
    private val database = RankingDatabase.getDatabase(context)
    private val examDao = database.examDao()
    private val rankingSegmentDao = database.rankingSegmentDao()
    private val provinceDao = database.provinceDao()
    private val levelTypeDao =database.levelTypeDao()
    private val trackTypeDao = database.trackTypeDao()

    suspend fun getExam(provinceId: String, year: String, track: String, level: String): Exam? {
        return examDao.getExam(provinceId, year, track, level)
    }
    suspend fun getExam(examId: Int): Exam? {
        return examDao.getExam(examId)
    }

    suspend fun getRankingSegments(examId: Int): List<RankingSegment> {
        return rankingSegmentDao.getRankingSegmentsByExamId(examId)
    }

    suspend fun getRankingSegmentForScore(examId: Int, inputScore: String): RankingSegment? {
        return rankingSegmentDao.getRankingSegmentForScore(examId, inputScore)
    }

    suspend fun getAllProvinces(): List<Province> {
        Log.d(TAG, "getting provinces")
        return provinceDao.getAllProvinces()
    }
    suspend fun getProvince(provinceId: String): Province = provinceDao.getProvince(provinceId)
    suspend fun getLevelType(levelId: String): LevelType = levelTypeDao.getLevelType(levelId.toInt())
    suspend fun getTrackType(trackId:String): TrackType = trackTypeDao.getTrackType(trackId)

    suspend fun getAllLevelTypes(): List<LevelType> = levelTypeDao.getAllLevelTypes()
    suspend fun getAllTrackTypes(): List<TrackType> = trackTypeDao.getAllTrackTypes()

    suspend fun getYearsByProvince(provinceId: String): List<String> = examDao.getYearsByProvince(provinceId)
    suspend fun getTracksByProvinceAndYear(provinceId: String, year: String): List<String> = examDao.getTracksByProvinceAndYear(provinceId, year)
    suspend fun getLevelsByProvinceYearTrack(provinceId: String, year: String, track: String): List<String> =
        examDao.getLevelsByProvinceYearTrack(provinceId, year, track)
    suspend fun getTracksByProvinceYearLevel(provinceId: String, year: String, level: String): List<String> =
        examDao.getTracksByProvinceYearLevel(provinceId,year,level)
    suspend fun getYearsByProvinceTrackLevel(provinceId: String, track: String, level: String): List<String> =
        examDao.getYearsByProvinceTrackLevel(provinceId,track,level)

    suspend fun getTrackTypes(trackIds: List<String>): List<TrackType> = trackTypeDao.getTrackTypes(trackIds)
    suspend fun getLevelTypes(levelIds: List<String>): List<LevelType> = levelTypeDao.getLevelTypes(levelIds.mapNotNull { it.toIntOrNull() })
}
