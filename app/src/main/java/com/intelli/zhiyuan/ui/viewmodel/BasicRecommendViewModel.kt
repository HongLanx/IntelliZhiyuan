package com.intelli.zhiyuan.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.intelli.zhiyuan.data.ExamPreference
import com.intelli.zhiyuan.data.UserPreferences
import com.intelli.zhiyuan.data.model.basicinfo.BasicInfo
import com.intelli.zhiyuan.data.repository.BasicInfoRepository
import com.intelli.zhiyuan.data.repository.FavoriteRepository
import com.intelli.zhiyuan.data.repository.ProvinceDataRepository
import com.intelli.zhiyuan.ui.components.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// BasicRecommendViewModel.kt
@OptIn(ExperimentalCoroutinesApi::class)
class BasicRecommendViewModel(application: Application) : AndroidViewModel(application) {
    private val basicInfoRepo = BasicInfoRepository(application)
    private val favoriteRepo  = FavoriteRepository(application)
    private val provinceRepo  = ProvinceDataRepository(application)
    private val examPrefs     = ExamPreference(application)
    private val userPrefs     = UserPreferences(application)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _showOnlyFavorites = MutableStateFlow(false)
    val showOnlyFavorites: StateFlow<Boolean> = _showOnlyFavorites

    private val _isChinese = MutableStateFlow(true)
    val isChinese: StateFlow<Boolean> = _isChinese

    val examScore: StateFlow<Int?> = examPrefs.examScoreFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val favoriteUids: StateFlow<Set<String>> = favoriteRepo.favoriteUids
        .stateIn(viewModelScope, SharingStarted.Lazily, emptySet())

    private val _uiState = MutableStateFlow<UiState<List<ScoreDisplayItem>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<ScoreDisplayItem>>> = _uiState

    init {

        // 预加载 lookup
        viewModelScope.launch(Dispatchers.IO) {
            provinceRepo.loadAllLookups()
        }
        // 语言
        viewModelScope.launch {
            _isChinese.value = (userPrefs.selectedLanguage.first() == "zh")
        }

        // 三步合并：year/prov/track -> Triple
        val tripleFlow = combine(
            examPrefs.examYearFlow,
            examPrefs.examProvinceFlow,
            examPrefs.examTrackFlow
        ) { y, p, t -> Triple(y, p, t) }

        // 再合并 examScore
        val recommendFlow = combine(tripleFlow, examScore) { triple, score ->
            Pair(triple, score)
        }.flatMapLatest { (triple, score) ->
            flow {
                val (y, p, t) = triple
                if (y != null && p != null && t != null && score != null) {
                    val typeId = when (t) {
                        1 -> "1"; 2 -> "2"; 3 -> "3"
                        6 -> "2073"; 7 -> "2074"
                        else -> null
                    }
                    if (typeId != null) {
                        val pdList = provinceRepo.getRecommendProvinceData(
                            year = y.toString(),
                            provinceId = p.toString(),
                            typeId = typeId,
                            examScore = score
                        )
                        val basicMap = basicInfoRepo.getAllBasicInfo().first()
                            .associateBy { it.uid }
                        val items = pdList.mapNotNull { pd ->
                            basicMap[pd.schoolId]?.let { bi ->
                                ScoreDisplayItem(bi, pd)
                            }
                        }
                        emit(items.sortedByDescending { it.provinceData!!.min })
                    } else emit(emptyList())
                } else emit(emptyList())
            }
        }

        // 搜索 + 收藏 过滤，并最终更新 UiState
        viewModelScope.launch {
            combine(
                recommendFlow,
                searchQuery,
                favoriteUids,
                showOnlyFavorites
            ) { items, query, favs, only ->
                val filtered = if (query.isBlank()) items else {
                    val isCh = _isChinese.value
                    items.filter {
                        if (isCh) it.basicInfo.chinese_name.contains(query, true)
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
