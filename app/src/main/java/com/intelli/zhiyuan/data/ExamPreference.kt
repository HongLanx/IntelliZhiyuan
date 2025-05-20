package com.intelli.zhiyuan.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.intelli.zhiyuan.data.model.ranking.Exam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.examDataStore by preferencesDataStore(name = "exam_prefs")

const val EPTAG ="EXAM_PREFERENCE"
class ExamPreference(private val context: Context) {
    companion object {
        val EXAM_ID_KEY = intPreferencesKey("exam_id")
        val EXAM_YEAR_KEY = intPreferencesKey("exam_year")
        val EXAM_PROVINCE_KEY= intPreferencesKey("exam_province")
        val EXAM_TRACK_KEY = intPreferencesKey("exam_track")
        val EXAM_SCORE_KEY = intPreferencesKey("exam_score")
    }

    val examIdFlow: Flow<Int?> = context.examDataStore.data.map { preferences ->
        preferences[EXAM_ID_KEY]
    }
    val examYearFlow:Flow<Int?> =context.examDataStore.data.map { preferences ->
        preferences[EXAM_YEAR_KEY]
    }
    val examProvinceFlow:Flow<Int?> =context.examDataStore.data.map { preferences ->
        preferences[EXAM_PROVINCE_KEY]
    }
    val examTrackFlow:Flow<Int?> =context.examDataStore.data.map { preferences ->
        preferences[EXAM_TRACK_KEY]
    }
    val examScoreFlow:Flow<Int?> =context.examDataStore.data.map { preferences ->
        preferences[EXAM_SCORE_KEY]}

    suspend fun saveExamId(examId: Int) {
        context.examDataStore.edit { preferences ->
            preferences[EXAM_ID_KEY] = examId
        }
    }
    suspend fun saveExamYear(examYear: Int) {
        context.examDataStore.edit { preferences ->
            preferences[EXAM_YEAR_KEY] = examYear
        }
    }

    suspend fun saveExamProvince(examProvince: Int) {
        context.examDataStore.edit { preferences ->
            preferences[EXAM_PROVINCE_KEY] = examProvince
        }
    }

    suspend fun saveExamTrack(examTrack: Int) {
        context.examDataStore.edit { preferences ->
            preferences[EXAM_TRACK_KEY] = examTrack
        }
    }

    suspend fun saveExamScore(examScore: Int) {
        context.examDataStore.edit { preferences ->
            preferences[EXAM_SCORE_KEY] = examScore
        }
    }


     suspend fun saveExam(exam: Exam, examScore:Int){
         this.saveExamId(exam.id)
         this.saveExamYear(exam.year.toInt())
         this.saveExamProvince(exam.province_id.toInt())
         this.saveExamTrack(exam.track.toInt())
         this.saveExamScore(examScore)
         Log.d(EPTAG,"exam saved")
    }
}
