import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.intelli.zhiyuan.data.UserPreferences
import com.intelli.zhiyuan.data.model.specialsinfo.LevelOne
import com.intelli.zhiyuan.data.model.specialsinfo.LevelThree
import com.intelli.zhiyuan.data.model.specialsinfo.LevelTwo
import com.intelli.zhiyuan.data.model.specialsinfo.SpecialsInfo
import com.intelli.zhiyuan.data.repository.FavoriteSpecialsRepository
import com.intelli.zhiyuan.data.repository.SpecialsInfoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

const val SIVMTAG="SpecialsInfoViewModel"
class SpecialsInfoViewModel(application: Application) : AndroidViewModel(application) {
    private val userPreferences = UserPreferences(application)

    private val specialsInfoRepository = SpecialsInfoRepository(application)
    private val favoriteRepo = FavoriteSpecialsRepository(application)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _showOnlyFavorites = MutableStateFlow(false)
    val showOnlyFavorites: StateFlow<Boolean> = _showOnlyFavorites.asStateFlow()

    // 假设语言信息从用户偏好中获取，此处默认中文
    private val _isChinese = MutableStateFlow(true)
    val isChinese: StateFlow<Boolean> = _isChinese

    // Level 筛选状态（后续可用来联动过滤）
    private val _selectedLevelOne = MutableStateFlow<String?>(null)
    val selectedLevelOne: StateFlow<String?> = _selectedLevelOne

    private val _selectedLevelTwo = MutableStateFlow<String?>(null)
    val selectedLevelTwo: StateFlow<String?> = _selectedLevelTwo

    private val _selectedLevelThree = MutableStateFlow<String?>(null)
    val selectedLevelThree: StateFlow<String?> = _selectedLevelThree

    private val _levelOneList = MutableStateFlow<List<LevelOne>>(emptyList())
    val levelOneList: StateFlow<List<LevelOne>> = _levelOneList

    private val _levelTwoList = MutableStateFlow<List<LevelTwo>>(emptyList())
    val levelTwoList: StateFlow<List<LevelTwo>> = _levelTwoList

    private val _levelThreeList = MutableStateFlow<List<LevelThree>>(emptyList())
    val levelThreeList: StateFlow<List<LevelThree>> = _levelThreeList

    private val _specialsList = MutableStateFlow<List<SpecialsInfo>>(emptyList())
    val specialsList: StateFlow<List<SpecialsInfo>> = _specialsList

    val favoriteSpecialIds: StateFlow<Set<String>> = favoriteRepo.favoriteSpecials
        .stateIn(viewModelScope, SharingStarted.Lazily, emptySet())


    init {
        viewModelScope.launch {
            val savedLanguage = userPreferences.selectedLanguage.first()
            _isChinese.value = savedLanguage == "zh"
            _levelOneList.value = specialsInfoRepository.getAllLevelOnes()
            _specialsList.value = specialsInfoRepository.getAllSpecials()
        }
    }

    val specialListShowed: StateFlow<List<SpecialsInfo>> = combine(
        _specialsList,
        favoriteRepo.favoriteSpecials,
        _searchQuery,
        _isChinese,
        _showOnlyFavorites
    ) { list, favorites, query, isChinese, showOnlyFavorites ->
        val filtered = list.filter { info ->
            val matchesQuery = query.isBlank() || if (isChinese) {
                info.name_ch.contains(query, ignoreCase = true)
            } else {
                info.name_en.contains(query, ignoreCase = true)
            }
            val matchesFavorites = !showOnlyFavorites || favorites.contains(info.special_id)
            matchesQuery && matchesFavorites
        }
        // 只按 UID 升序排序
        filtered.sortedBy { it.special_id.toIntOrNull() ?: Int.MAX_VALUE }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
//    // 合并搜索及收藏状态（这里只用搜索过滤示例）
//    val specialsList = combine(
//        specialsRepo.getSpecialsFiltered(_searchQuery.value, _isChinese.value),
//        favoriteRepo.favoriteSpecials
//    ) { specials, favorites ->
//        // 此处可根据 Level 筛选条件进行进一步过滤
//        specials.sortedBy { it.special_id.toIntOrNull() ?: Int.MAX_VALUE }
//    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectLevelOne(levelOne: String) {
        _selectedLevelOne.value = levelOne
        viewModelScope.launch {
            _levelTwoList.value = specialsInfoRepository.getLevelTwosByLevelOne(levelOne)
            Log.d(SIVMTAG,"level two option when select level one: ${_levelTwoList.value.map { it.name_ch }}")
            _selectedLevelTwo.value = null
            _selectedLevelThree.value = null
            _specialsList.value = specialsInfoRepository.getByLevelThrees(specialsInfoRepository.getLevelThreesByLevelTwos(_levelTwoList.value.map { it.level_id }).map { it.level_id })
        }
    }

    fun selectLevelTwo(levelTwo: String) {
        _selectedLevelTwo.value = levelTwo
        viewModelScope.launch {
            _levelThreeList.value = specialsInfoRepository.getLevelThreesByLevelTwo(levelTwo)
            Log.d(SIVMTAG,"level three option when select level two: ${_levelThreeList.value.map { it.name_ch }}")

            _selectedLevelThree.value = null
            _specialsList.value = specialsInfoRepository.getByLevelThrees(_levelThreeList.value.map { it.level_id })
        }
    }

    fun selectLevelThree(levelId: String) {
        _selectedLevelThree.value = levelId
        viewModelScope.launch {
            _specialsList.value = specialsInfoRepository.getByLevelThree(levelId)
        }
    }

    fun toggleFavorite(special: SpecialsInfo) {
        viewModelScope.launch {
            val favorites = favoriteRepo.favoriteSpecials.first()
            if (favorites.contains(special.special_id)) {
                favoriteRepo.removeFavorite(special.special_id)
            } else {
                favoriteRepo.addFavorite(special.special_id)
            }
        }
    }

    fun toggleShowOnlyFavorites() {
        _showOnlyFavorites.value = !_showOnlyFavorites.value
    }
}