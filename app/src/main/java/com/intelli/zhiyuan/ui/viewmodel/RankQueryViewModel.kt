package com.intelli.zhiyuan.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.intelli.zhiyuan.data.ExamPreference
import com.intelli.zhiyuan.data.UserPreferences
import com.intelli.zhiyuan.data.model.ranking.Exam
import com.intelli.zhiyuan.data.model.ranking.LevelType
import com.intelli.zhiyuan.data.model.ranking.Province
import com.intelli.zhiyuan.data.model.ranking.TrackType
import com.intelli.zhiyuan.data.repository.RankQueryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

const val RQVMTAG ="RankQueryViewModel"

class RankQueryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RankQueryRepository(application)
    private val examPreference = ExamPreference(application)
    private val userPreferences = UserPreferences(application)

    private val _isChinese = MutableStateFlow(true)
    val isChinese: StateFlow<Boolean> = _isChinese.asStateFlow()

    // 下拉菜单选项状态
    private val _provinceOptions = MutableStateFlow<List<Province>>(emptyList())
    val provinceOptions: StateFlow<List<Province>> = _provinceOptions

    private val _yearOptions = MutableStateFlow<List<String>>(emptyList())
    val yearOptions: StateFlow<List<String>> = _yearOptions

    private val _trackOptions = MutableStateFlow<List<TrackType>>(emptyList())
    val trackOptions: StateFlow<List<TrackType>> = _trackOptions

    private val _levelOptions = MutableStateFlow<List<LevelType>>(emptyList())
    val levelOptions: StateFlow<List<LevelType>> = _levelOptions

    // 选中项
    private val _selectedProvinceId = MutableStateFlow<String?>(null)
    val selectedProvinceId: StateFlow<String?> = _selectedProvinceId

    private val _selectedYear = MutableStateFlow<String?>(null)
    val selectedYear: StateFlow<String?> = _selectedYear

    private val _selectedTrack = MutableStateFlow<String?>(null)
    val selectedTrack: StateFlow<String?> = _selectedTrack

    private val _selectedLevel = MutableStateFlow<String?>(null)
    val selectedLevel: StateFlow<String?> = _selectedLevel

    private val _inputExamScore = MutableStateFlow<String?>("")
    val inputExamScore: StateFlow<String?> = _inputExamScore

    // exam_id
    private val _exam = MutableStateFlow<Exam?>(null)
    val exam: StateFlow<Exam?> = _exam

    // ranking segments（去重后的列表）
    private val _rankingSegments = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val rankingSegments: StateFlow<List<Pair<String, String>>> = _rankingSegments

    // 查询结果（rank_range）
    private val _resultRankRange = MutableStateFlow<String?>(null)
    val resultRankRange: StateFlow<String?> = _resultRankRange

    init {
        viewModelScope.launch {
            val savedLanguage = userPreferences.selectedLanguage.first()
            _isChinese.value = savedLanguage == "zh"
            _provinceOptions.value = repository.getAllProvinces()
//
//            _trackOptions.value = repository.getAllTrackTypes()
//            _levelOptions.value = repository.getAllLevelTypes()
            // 尝试读取保存的 exam_id 并填充选项（如果存在）
//            examPreference.examIdFlow.collect { savedExamId ->
//                savedExamId?.let {
//
//                }
//            }
            examPreference.examIdFlow.firstOrNull() ?.let {
                    id ->
                // 获取对应的 exam
                val exam: Exam? = repository.getExam(id)
                _exam.value = exam
                exam?.let {
                    if (_selectedProvinceId.value == null &&
                        _selectedYear.value == null &&
                        _selectedTrack.value == null &&
                        _selectedLevel.value == null){
                        setYearOptions(it.province_id)
                        setTrackOptions(it.province_id,it.year)
                        setLevelOptions(it.province_id,it.year,it.track)
                        _selectedProvinceId.value = it.province_id
                        _selectedYear.value = it.year
                        _selectedTrack.value = it.track
                        _selectedLevel.value = it.level
                        loadRankingSegments(it.id)
                    }
                }
            }
            examPreference.examScoreFlow.firstOrNull() ?.let{
                    score ->
                Log.d(RQVMTAG, "current score : $score")
                _inputExamScore.value = score.toString()
                queryRank()

            }

        }
    }

    private suspend fun setYearOptions(provinceId: String){
        _yearOptions.value = repository.getYearsByProvince(provinceId)
        if (_yearOptions.value.size == 1)
            selectYear(_yearOptions.value.first())
    }
    private suspend fun setTrackOptions(provinceId: String, year: String){
        _trackOptions.value = repository.getTrackTypes(repository.getTracksByProvinceAndYear(provinceId,year))
        if (_trackOptions.value.size == 1)
            selectTrack(_trackOptions.value.first())
    }
    private suspend fun setLevelOptions(provinceId: String, year: String, track: String){
        _levelOptions.value = repository.getLevelTypes(repository.getLevelsByProvinceYearTrack(provinceId, year,track))
        if (_levelOptions.value.size == 1)
            selectLevel(_levelOptions.value.first())
    }

    fun changeInputScore(input: String){
        _inputExamScore.value = input
    }

    fun selectProvince(provinceId: String) {
        _selectedProvinceId.value = provinceId
        viewModelScope.launch {
            if (_selectedYear.value != null || _selectedTrack.value != null || _selectedLevel.value != null){
                _selectedYear.value = null
                _selectedTrack.value = null
                _selectedLevel.value = null
                _trackOptions.value = emptyList()
                _levelOptions.value = emptyList()
                _rankingSegments.value = emptyList()
                _resultRankRange.value = null
            }
            setYearOptions(provinceId)
        }
    }

    fun selectYear(year: String) {
        _selectedYear.value = year
        val provinceId = _selectedProvinceId.value ?: return
        viewModelScope.launch {
            if (_selectedTrack.value != null || _selectedLevel.value != null) {
                _selectedTrack.value = null
                _selectedLevel.value = null
                _levelOptions.value = emptyList()
                _rankingSegments.value = emptyList()
                _resultRankRange.value = null
            }
            setTrackOptions(provinceId,year)
        }
    }

    fun selectTrack(track: TrackType) {
        _selectedTrack.value = track.track
        val provinceId = _selectedProvinceId.value ?: return
        val year = _selectedYear.value ?: return
        viewModelScope.launch {
            if (_selectedLevel.value != null){
                _selectedLevel.value = null
                _rankingSegments.value = emptyList()
                _resultRankRange.value = null
            }
            setLevelOptions(provinceId,year,track.track)
        }
    }

    fun selectLevel(level: LevelType) {
        _selectedLevel.value = level.level.toString()
        val provinceId = _selectedProvinceId.value ?: return
        val year = _selectedYear.value ?: return
        val track = _selectedTrack.value ?: return
        viewModelScope.launch {
            val exam = repository.getExam(provinceId, year, track, level.level.toString())
            _exam.value = exam
            _exam.value?.let {
                loadRankingSegments(it.id)
            }
        }
    }

    private fun loadRankingSegments(examId: Int) {
        viewModelScope.launch {
            val segments = repository.getRankingSegments(examId)

            // 先去重（去掉重复的 score_range & rank_range 组合）
            val deduped = segments.distinctBy { it.score_range to it.rank_range }
                .sortedByDescending { pair ->
                    // 按照分数区间的最低值降序排序
                    val lower = pair.score_range.split("-").firstOrNull()?.toIntOrNull() ?: 0
                    lower
                }

            // 重新构造 score_range，使最高的分数段保持不变，最低的分数段变成 "0-xxx"
            val processedSegments = mutableListOf<Pair<String, String>>()

            deduped.forEachIndexed { index, segment ->
                val scoreRangeParts = segment.score_range.split("-")
                val lower = scoreRangeParts.firstOrNull()?.toIntOrNull() ?: 0

                val newScoreRange = if (index == deduped.lastIndex) { // 只有最后一项才变成 "0-xxx"
                    "0-${scoreRangeParts.lastOrNull()}"
                } else {
                    segment.score_range
                }

                processedSegments.add(newScoreRange to segment.rank_range)
            }

            _rankingSegments.value = processedSegments
        }
    }



    fun queryRank() {
        val inputScore = _inputExamScore.value ?: ""
        if (inputScore.toIntOrNull() == null)
            return
        val examScore = inputScore.toInt()
        if (examScore !in 1..900)
            return
        else
        {
            _inputExamScore.value = inputScore
            viewModelScope.launch {
                val examId = _exam.value?.id ?: return@launch
                _exam.value?.let {
                    examPreference.saveExam(it,examScore)
                }
                val segment = repository.getRankingSegmentForScore(examId, inputScore)
                _resultRankRange.value = segment?.rank_range
            }
        }

    }
}
