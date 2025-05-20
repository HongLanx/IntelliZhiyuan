package com.intelli.zhiyuan.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.intelli.zhiyuan.data.ExamPreference
import com.intelli.zhiyuan.data.UserPreferences
import com.intelli.zhiyuan.data.model.basicinfo.BasicInfo
import com.intelli.zhiyuan.data.repository.BasicInfoRepository
import com.intelli.zhiyuan.data.repository.FavoriteRepository
import com.intelli.zhiyuan.data.repository.PredictionRepository
import com.intelli.zhiyuan.data.repository.RankQueryRepository
import com.intelli.zhiyuan.ui.components.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class IntelliApplyItem(
    val basicInfo: BasicInfo,
    val minSectionPred: Int
)

@OptIn(ExperimentalCoroutinesApi::class)
class IntelliApplyViewModel(application: Application) : AndroidViewModel(application) {
    private val basicInfoRepo = BasicInfoRepository(application)
    private val favoriteRepo  = FavoriteRepository(application)
    private val rankRepo      = RankQueryRepository(application)
    private val predRepo      = PredictionRepository(application)
    private val examPrefs     = ExamPreference(application)
    private val userPrefs     = UserPreferences(application)

    // 搜索、收藏、语言
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _showOnlyFavorites = MutableStateFlow(false)
    val showOnlyFavorites: StateFlow<Boolean> = _showOnlyFavorites

    private val _isChinese = MutableStateFlow(true)
    val isChinese: StateFlow<Boolean> = _isChinese

    // 收藏集合
    val favoriteUids = favoriteRepo.favoriteUids
        .stateIn(viewModelScope, SharingStarted.Lazily, emptySet())

    // UI 状态
    private val _uiState =
        MutableStateFlow<UiState<List<IntelliApplyItem>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<IntelliApplyItem>>> = _uiState

    // 暴露 examId 和 examScore 的 Flow
    private val examIdFlow    = examPrefs.examIdFlow
    private val examScoreFlow = examPrefs.examScoreFlow

    /** 暴露给 UI 的 lowestSection StateFlow */
    val lowestSection: StateFlow<Int?> = combine(
        examIdFlow,
        examScoreFlow
    ) { examId, score -> Pair(examId, score) }
        .flatMapLatest { (examId, score) ->
            flow {
                if (examId != null && score != null) {
                    val seg = rankRepo.getRankingSegmentForScore(examId, score.toString())
                    val low = seg
                        ?.rank_range
                        ?.substringBefore("-")
                        ?.toIntOrNull()
                    emit(low)
                } else {
                    emit(null)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        // 预加载语言偏好
        viewModelScope.launch {
            _isChinese.value = (userPrefs.selectedLanguage.first() == "zh")
        }

        // 主流程：compute lowestSection -> fetch predictions -> join BasicInfo -> filter&sort -> post UiState
        viewModelScope.launch {
            // 1) lowestSectionFlow: 从 examId & examScore -> RankingSegment -> lowestSection: Int?
            val lowestSectionFlow: Flow<Int?> = combine(
                examPrefs.examIdFlow,
                examPrefs.examScoreFlow
            ) { examId, score ->
                Pair(examId, score)
            }.flatMapLatest { (examId, score) ->
                flow {
                    if (examId != null && score != null) {
                        val seg = rankRepo.getRankingSegmentForScore(examId, score.toString())
                        seg?.rank_range
                            ?.substringBefore("-")
                            ?.toIntOrNull()
                            ?.let { emit(it) }
                    }
                    // 否则 emit null
                }
            }.distinctUntilChanged()

            // 2) predictionsFlow: combine province, track -> typeId ； then with lowestSectionFlow
            val recFlow: Flow<List<IntelliApplyItem>> = combine(
                examPrefs.examProvinceFlow,
                examPrefs.examTrackFlow,
                lowestSectionFlow
            ) { prov, track, lowSec -> Triple(prov, track, lowSec) }
                .flatMapLatest { (prov, track, lowSec) ->
                    flow {
                        if (prov != null && track != null && lowSec != null) {
                            val typeId = when (track) {
                                1 -> "1"; 2 -> "2"; 3 -> "3"
                                6 -> "1"; 7 -> "2"
                                else -> null
                            }
                            if (typeId != null) {
                                // range: lowSec -1500 ... lowSec+1500
                                val list = predRepo.getPredictionsInRange(
                                    provinceId = prov.toString(),
                                    typeId     = typeId,
                                    low        = lowSec - 1000,
                                    high       = lowSec + 2000
                                )
                                // join to BasicInfo
                                val basicMap = basicInfoRepo.getAllBasicInfo().first()
                                    .associateBy { it.uid }
                                val items = list.mapNotNull { pd ->
                                    basicMap[pd.schoolId]?.let { bi ->
                                        IntelliApplyItem(bi, pd.minSectionPred)
                                    }
                                }
                                // 按 minSectionPred 升序
                                emit(items.sortedBy { it.minSectionPred })
                            } else emit(emptyList())
                        } else emit(emptyList())
                    }
                }

            // 3) apply search & favorites & expose UI
            combine(
                recFlow,
                searchQuery,
                favoriteUids,
                showOnlyFavorites
            ) { items, query, favs, only ->
                val filtered = if (query.isBlank()) items else {
                    val isCh = _isChinese.value
                    items.filter {
                        if (isCh)
                            it.basicInfo.chinese_name.contains(query, true)
                        else it.basicInfo.english_name.contains(query, true)
                    }
                }
                if (!only) filtered else filtered.filter { favs.contains(it.basicInfo.uid) }
            }.collect { list ->
                _uiState.value = UiState.Success(list)
            }
        }
    }

    fun updateSearchQuery(q: String) { _searchQuery.value = q }
    fun toggleShowOnlyFavorites() { _showOnlyFavorites.value = !_showOnlyFavorites.value }
    fun toggleFavorite(info: BasicInfo) {
        viewModelScope.launch {
            val curr = favoriteUids.first()
            if (curr.contains(info.uid)) favoriteRepo.removeFavorite(info.uid)
            else favoriteRepo.addFavorite(info.uid)
        }
    }
}
