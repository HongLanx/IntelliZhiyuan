// com/intelli/zhiyuan/ui/viewmodel/BasicInfoViewModel.kt
package com.intelli.zhiyuan.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.intelli.zhiyuan.data.ExamPreference
import com.intelli.zhiyuan.data.UserPreferences
import com.intelli.zhiyuan.data.model.ProvinceDataEntity
import com.intelli.zhiyuan.data.model.basicinfo.BasicInfo
import com.intelli.zhiyuan.data.repository.BasicInfoRepository
import com.intelli.zhiyuan.data.repository.FavoriteRepository
import com.intelli.zhiyuan.data.repository.ProvinceDataRepository
import com.intelli.zhiyuan.ui.components.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class BasicInfoViewModel(application: Application) : AndroidViewModel(application) {
    private val basicInfoRepo = BasicInfoRepository(application)
    private val favoriteRepo = FavoriteRepository(application)
    private val provinceRepo = ProvinceDataRepository(application)
    private val examPrefs = ExamPreference(application)
    private val userPreferences = UserPreferences(application)

    // 搜索、收藏、语言、推荐
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _showOnlyFavorites = MutableStateFlow(false)
    val showOnlyFavorites: StateFlow<Boolean> = _showOnlyFavorites

    private val _isChinese = MutableStateFlow(true)
    val isChinese: StateFlow<Boolean> = _isChinese

    private val _showOnlyRecommended = MutableStateFlow(false)
    val showOnlyRecommended: StateFlow<Boolean> = _showOnlyRecommended

    // 收藏的 uid 集合
    val favoriteUids: StateFlow<Set<String>> = favoriteRepo.favoriteUids
        .stateIn(viewModelScope, SharingStarted.Lazily, emptySet())

    // 推荐的 uid 集合
    private val _recommendedUids = MutableStateFlow<Set<String>>(emptySet())
    val recommendedUids: StateFlow<Set<String>> = _recommendedUids

    // UI 状态，承载 ScoreDisplayItem 列表
    private val _uiState = MutableStateFlow<UiState<List<ScoreDisplayItem>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<ScoreDisplayItem>>> = _uiState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            provinceRepo.loadAllLookups()
        }

        viewModelScope.launch {
            _isChinese.value = (userPreferences.selectedLanguage.first() == "zh")
        }

        val preferenceDataFlow = combine(
            examPrefs.examYearFlow,
            examPrefs.examProvinceFlow,
            examPrefs.examTrackFlow
        ) { year, prov, track ->
            Triple(year, prov, track)
        }.flatMapLatest { (year, prov, track) ->
            flow {
                if (year != null && prov != null && track != null) {
                    val typeId = when (track) {
                        1 -> "1"; 2 -> "2"; 3 -> "3"
                        6 -> "2073"; 7 -> "2074"
                        else -> null
                    }
                    if (typeId != null) {
                        emit(provinceRepo.getPreferredProvinceData(
                            year.toString(), prov.toString(), typeId
                        ))
                    } else {
                        emit(emptyList())
                    }
                } else {
                    emit(emptyList())
                }
            }.catch { emit(emptyList()) }
        }.distinctUntilChanged()

        val itemsBySearchAndPref: Flow<List<ScoreDisplayItem>> = combine(
            basicInfoRepo.getAllBasicInfo(),
            preferenceDataFlow,
            _searchQuery,
            _isChinese
        ) { basicList, prefData, query, isCh ->
            val filteredBasic = basicList.filter { info ->
                val matches = query.isBlank() ||
                        if (isCh) info.chinese_name.contains(query, true)
                        else info.english_name.contains(query, true)
                matches
            }

            val aMap = prefData.associateBy { it.schoolId }
            val inA = filteredBasic.mapNotNull { info ->
                aMap[info.uid]?.let { pd -> ScoreDisplayItem(info, pd) }
            }
            val notA = filteredBasic
                .filter { info -> !aMap.containsKey(info.uid) }
                .map { info -> ScoreDisplayItem(info, null) }

            val sortedA = inA.sortedByDescending { it.provinceData!!.min }
            val sortedNotA = notA.sortedBy { it.basicInfo.uid.toIntOrNull() ?: Int.MAX_VALUE }

            sortedA + sortedNotA
        }

        viewModelScope.launch {
            combine(
                itemsBySearchAndPref,
                favoriteRepo.favoriteUids,
                _showOnlyFavorites,
                _showOnlyRecommended,
                _recommendedUids
            ) { items, favs, onlyFavs, onlyRecs, recs ->
                when {
                    onlyFavs -> items.filter { favs.contains(it.basicInfo.uid) }
                    onlyRecs -> items.filter { recs.contains(it.basicInfo.uid) }
                    else -> items
                }
            }.collect { finalList ->
                _uiState.value = UiState.Success(finalList)
            }
        }
    }

    fun updateSearchQuery(q: String) {
        _searchQuery.value = q
    }

    fun toggleShowOnlyFavorites() {
        _showOnlyFavorites.value = !_showOnlyFavorites.value
    }

    fun toggleShowOnlyRecommended() {
        _showOnlyRecommended.value = !_showOnlyRecommended.value
    }

    fun toggleFavorite(info: BasicInfo) {
        viewModelScope.launch {
            val curr = favoriteRepo.favoriteUids.first()
            if (curr.contains(info.uid)) {
                favoriteRepo.removeFavorite(info.uid)
            } else {
                favoriteRepo.addFavorite(info.uid)
            }
        }
    }

    // 获取推荐学校 ID
    fun fetchRecommendations() {
        viewModelScope.launch {
            val recs = favoriteRepo.getRecommendations()
            _recommendedUids.value = recs.toSet()
        }
    }
}


/** 用于 UI 展示的组合数据 */
data class ScoreDisplayItem(
    val basicInfo: BasicInfo,
    val provinceData: ProvinceDataEntity?  // 来自列表A，否则 null
)
