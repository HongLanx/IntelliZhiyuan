package com.intelli.zhiyuan.data.local.ranking

import androidx.room.Dao
import androidx.room.Query
import com.intelli.zhiyuan.data.model.ranking.Exam

@Dao
interface ExamDao {
    @Query("SELECT DISTINCT year FROM exam WHERE province_id = :provinceId")
    suspend fun getYearsByProvince(provinceId: String): List<String>

    @Query("SELECT DISTINCT track FROM exam WHERE province_id = :provinceId AND year = :year")
    suspend fun getTracksByProvinceAndYear(provinceId: String, year: String): List<String>

    @Query("SELECT DISTINCT level FROM exam WHERE province_id = :provinceId AND year = :year AND track = :track")
    suspend fun getLevelsByProvinceYearTrack(provinceId: String, year: String, track: String): List<String>

    @Query("SELECT DISTINCT track FROM exam WHERE province_id = :provinceId AND year = :year AND level = :level")
    suspend fun getTracksByProvinceYearLevel(provinceId: String, year: String, level: String): List<String>

    @Query("SELECT DISTINCT year FROM exam WHERE province_id = :provinceId AND track = :track AND level = :level")
    suspend fun getYearsByProvinceTrackLevel(provinceId: String, track: String, level: String): List<String>

    @Query("SELECT * FROM exam WHERE province_id = :provinceId AND year = :year AND track = :track AND level = :level LIMIT 1")
    suspend fun getExam(provinceId: String, year: String, track: String, level: String): Exam?

    @Query("SELECT * FROM exam WHERE id = :examId LIMIT 1")
    suspend fun getExam(examId : Int): Exam?

}
