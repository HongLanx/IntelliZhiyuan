package com.intelli.zhiyuan.ui.viewmodel


import android.app.Application
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.intelli.zhiyuan.data.ExamPreference
import com.intelli.zhiyuan.data.UserPreferences
import com.intelli.zhiyuan.data.model.BatchesEntity
import com.intelli.zhiyuan.data.model.DetailedInfo
import com.intelli.zhiyuan.data.model.ProvinceDataEntity
import com.intelli.zhiyuan.data.model.ProvinceEntity
import com.intelli.zhiyuan.data.model.SpecialDataEntity
import com.intelli.zhiyuan.data.model.TypeEntity
import com.intelli.zhiyuan.data.model.ranking.Exam
import com.intelli.zhiyuan.data.model.specialsinfo.SpecialsInfo
import com.intelli.zhiyuan.data.repository.DetailedInfoRepository
import com.intelli.zhiyuan.data.repository.ProvinceDataRepository
import com.intelli.zhiyuan.data.repository.SpecialDataRepository
import com.intelli.zhiyuan.data.repository.SpecialGroupDataRepository
import com.intelli.zhiyuan.data.repository.SpecialsInfoRepository
import com.intelli.zhiyuan.ui.components.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SpecialGroupItem(
    val code: String,
    val name: String
)

const val DIVMTAG="DetailInfoViewModel"
@OptIn(ExperimentalCoroutinesApi::class)
class DetailInfoViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    private val uid: String = checkNotNull(savedStateHandle["uid"])
    private val provinceRepo = ProvinceDataRepository(application)
    private val detailedRepo = DetailedInfoRepository(application)
    private val specialRepo  = SpecialsInfoRepository(application)
    private val specialDataRepo = SpecialDataRepository(application)
    private val userPrefs    = UserPreferences(application)
    private val specialGroupRepo = SpecialGroupDataRepository(application)
    private val examPrefs = ExamPreference(application)
    // UI state
    private val _uiState = MutableStateFlow<UiState<DetailedInfo>>(UiState.Loading)
    val uiState: StateFlow<UiState<DetailedInfo>> = _uiState

    // 语言、详情、special map
    private val _isChinese = MutableStateFlow(true)
    val isChinese: StateFlow<Boolean> = _isChinese

    private val _detailedInfo = MutableStateFlow<DetailedInfo?>(null)
    val detailedInfo = _detailedInfo.asStateFlow()

    private var allSpecialsMap: Map<String, SpecialsInfo> = emptyMap()

    // HistoryScores StateFlows
    private val _historyProvinceOptions = MutableStateFlow<List<ProvinceEntity>>(emptyList())
    private val _selectedHistoryProvince = MutableStateFlow<ProvinceEntity?>(null)
    private val _historyYearOptions = MutableStateFlow<List<String>>(emptyList())
    private val _selectedHistoryYear = MutableStateFlow<String?>(null)
    private val _historyTypeOptions = MutableStateFlow<List<TypeEntity>>(emptyList())
    private val _selectedHistoryType = MutableStateFlow<TypeEntity?>(null)
    private val _historyData = MutableStateFlow<List<ProvinceDataEntity>>(emptyList())

    // MajorScores StateFlows
    private val _majorProvinceOptions = MutableStateFlow<List<ProvinceEntity>>(emptyList())
    private val _selectedMajorProvince = MutableStateFlow<ProvinceEntity?>(null)
    private val _majorYearOptions = MutableStateFlow<List<String>>(emptyList())
    private val _selectedMajorYear = MutableStateFlow<String?>(null)
    private val _majorTypeOptions = MutableStateFlow<List<TypeEntity>>(emptyList())
    private val _selectedMajorType = MutableStateFlow<TypeEntity?>(null)
    private val _majorBatchOptions = MutableStateFlow<List<BatchesEntity>>(emptyList())
    private val _selectedMajorBatch = MutableStateFlow<BatchesEntity?>(null)
    private val _majorData = MutableStateFlow<List<SpecialDataEntity>>(emptyList())

    // SpecialFeature StateFlows
    private val _evaluationList = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    private val _nationalFeatured = MutableStateFlow<List<String>>(emptyList())
    private val _provincialFeatured = MutableStateFlow<List<String>>(emptyList())
    private val _firstClass = MutableStateFlow<List<String>>(emptyList())

    // SpecialGroup
    private val _specialGroupItems = MutableStateFlow<List<SpecialGroupItem>>(emptyList())

    init {
        viewModelScope.launch {
            // 省份变化后同步 MajorProvince
            _selectedHistoryProvince
                .filterNotNull()
                .collectLatest { selectMajorProvince(it.provinceId) }
        }
        viewModelScope.launch {
            // 省份 + 年份 都非空后再同步 MajorYear
            _selectedHistoryProvince.filterNotNull()
                .flatMapLatest { _selectedHistoryYear.filterNotNull() }
                .collectLatest {
                    Log.d(DIVMTAG,"current year:$it")
                    selectMajorYear(it) }
        }
        viewModelScope.launch {
            // 省份 + 年 + 类型 都非空后再同步 MajorType
            _selectedHistoryProvince.filterNotNull()
                .flatMapLatest { _selectedHistoryYear.filterNotNull() }
                .flatMapLatest { _selectedHistoryType.filterNotNull() }
                .collectLatest {
                    Log.d(DIVMTAG,"current type:$it")
                    selectMajorType(it.id) }
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                // 1) 预加载 lookup
                async(Dispatchers.IO) { provinceRepo.loadAllLookups() }.await()

                // 2) 并行拉取：语言、历史省份、详情、allSpecials、Major省份
                val langD   = async { userPrefs.selectedLanguage.first() }
                val histProvD = async(Dispatchers.IO) { provinceRepo.getHistoryProvinces(uid) }
                val infoD     = async(Dispatchers.IO) { detailedRepo.getDetailedInfo(uid) }
                val specialsD = async(Dispatchers.IO) { specialRepo.getAllSpecials() }
                val majorProvD= async(Dispatchers.IO) {
                    specialDataRepo.getMajorProvinces(uid.toInt())
                }

                // 3) 赋值
                _isChinese.value = (langD.await() == "zh")
                _historyProvinceOptions.value = histProvD.await()
                val info = infoD.await() ?: throw NullPointerException("DetailedInfo is null")
                _detailedInfo.value = info

                allSpecialsMap = specialsD.await().associateBy { it.special_id }
                loadSpecialFeatures(info)

                // MajorScores 省份实体
                _majorProvinceOptions.value = majorProvD.await()
                    .mapNotNull { provinceRepo.getProvinceById(it.toString()) }

                _uiState.value = UiState.Success(info)


            } catch (e: Throwable) {
                _uiState.value = UiState.Error(e)
            }
        }

        viewModelScope.launch {
            // （C）按序从 prefs 里取并选中历史省份/年份/类型
            // — 等 ProvinceOptions 准备好 —
            _historyProvinceOptions.filter { it.isNotEmpty() }.first()
            examPrefs.examProvinceFlow.firstOrNull()
                ?.let { selectHistoryProvince(it.toString()) }

            // — 等 YearOptions 加载完 —
            _historyYearOptions.filter { it.isNotEmpty() }.first()
            examPrefs.examYearFlow.firstOrNull()
                ?.let { selectHistoryYear(it.toString()) }

            // — 等 TypeOptions 加载完 —
            _historyTypeOptions.filter { it.isNotEmpty() }.first()

            examPrefs.examTrackFlow.firstOrNull()
                ?.let { track ->
                    // map track to typeId
                    val typeId = when (track) {
                        1 -> "1"; 2 -> "2"; 3 -> "3"
                        6 -> "2073"; 7 -> "2074"
                        else -> null
                    }
                    typeId?.let { selectHistoryType(it) }
                }
        }

    }

    private suspend fun setHistoryYearOptions(provinceId: String){
        _historyYearOptions.value = provinceRepo.getHistoryYears(uid, provinceId).sorted()
        if (_historyYearOptions.value.size == 1)
            selectHistoryYear(_historyYearOptions.value.first())
    }
    private suspend fun setHistoryTypeOptions(provinceId: String, year: String){
        _historyTypeOptions.value = provinceRepo.getHistoryTypes(uid, provinceId,year)
        if (_historyTypeOptions.value.size == 1)
            selectHistoryType(_historyTypeOptions.value.first().id)
    }

    private suspend fun setMajorYearOptions(provinceId: String){
        _majorYearOptions.value = specialDataRepo.getMajorYears(uid.toInt(), provinceId.toInt()).map { it.toString() }.sorted()
        if (_majorYearOptions.value.size == 1)
            selectMajorYear(_majorYearOptions.value.first())
    }
    private suspend fun setMajorTypeOptions(provinceId: String, year: String){
        _majorTypeOptions.value = specialDataRepo
            .getMajorTypes(uid.toInt(), provinceId.toInt(), year.toInt())
            .mapNotNull { provinceRepo.getTypeById(it.toString()) }
        if (_majorTypeOptions.value.size == 1)
            selectMajorType(_majorTypeOptions.value.first().id)
    }
    private suspend fun setMajorBatchOptions(provinceId: String,year:String,typeId: String){
        _majorBatchOptions.value = specialDataRepo
            .getMajorBatches(uid.toInt(), provinceId.toInt(), year.toInt(), typeId.toInt())
            .mapNotNull { provinceRepo.getBatchById(it.toString()) }
        if (_majorBatchOptions.value.size == 1)
            selectMajorBatch(_majorBatchOptions.value.first().id)
    }


//    private suspend fun setHistoryYearOptions(provinceId: String){
//        _historyYearOptions.value = provinceRepo.getHistoryYears(uid, provinceId).sorted()
//        if (_historyYearOptions.value.size == 1)
//            selectHistoryYear(_historyYearOptions.value.first())
//    }



    /** SpecialFeature 逻辑 */
    private fun loadSpecialFeatures(info: DetailedInfo) {
        val isCh = _isChinese.value
        val natIds  = info.isNationalFeatured.split(";").filter(String::isNotBlank).distinct()
        val provIds = info.isProvincialFeatured.split(";").filter(String::isNotBlank).distinct()
        val firstIds= info.isNationFirstClass.split(";").filter(String::isNotBlank).distinct()

        val rankAMap = parseRankMap(info.rankA, "A")
        val rankBMap = parseRankMap(info.rankB, "B")
        val rankCMap = parseRankMap(info.rankC, "C")
        val allRankIds = (rankAMap.keys + rankBMap.keys + rankCMap.keys).distinct()

        _evaluationList.value = allRankIds.mapNotNull { id ->
            allSpecialsMap[id]?.let { spec ->
                val rank = rankAMap[id] ?: rankBMap[id] ?: rankCMap[id] ?: ""
                val name = if (isCh) spec.name_ch else spec.name_en
                name to rank
            }
        }
        _nationalFeatured.value   = natIds.mapNotNull   { allSpecialsMap[it]?.let { s -> if (isCh) s.name_ch else s.name_en } }
        _provincialFeatured.value = provIds.mapNotNull  { allSpecialsMap[it]?.let { s -> if (isCh) s.name_ch else s.name_en } }
        _firstClass.value         = firstIds.mapNotNull { allSpecialsMap[it]?.let { s -> if (isCh) s.name_ch else s.name_en } }
    }

    /** SpecialGroup */
    fun loadSpecialGroupItems(groupId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val entities = specialGroupRepo.getGroupItems(groupId)
            val isCh = _isChinese.value
            _specialGroupItems.value = entities.map { e ->
                val name = allSpecialsMap[e.spId]?.let { if (isCh) it.name_ch else it.name_en }.orEmpty()
                SpecialGroupItem(code = e.spcode, name = "$name ${e.remark.orEmpty()}")
            }
        }
    }

    /** 解析 rank 格式 */
    private fun parseRankMap(raw: String, grade: String) = raw
        .split(";").filter(String::isNotBlank).mapNotNull {
            it.split("_").takeIf { it.size==2 }?.let { (p,id)-> id to when(p){
                "p"->"$grade+";"m"->"$grade-";"n"->grade;else->grade } }
        }.toMap()

    //=== HistoryScores 的 select 系列 ===//
    fun selectHistoryProvince(provId: String) = viewModelScope.launch {
        _selectedHistoryProvince.value =
            _historyProvinceOptions.value.firstOrNull { it.provinceId==provId }
        viewModelScope.launch {
            if ( _selectedHistoryYear.value != null || _selectedHistoryType.value != null){
                _selectedHistoryYear.value = null
                _historyTypeOptions.value = emptyList()
                _selectedHistoryType.value = null
                _historyData.value = emptyList()
            }
            setHistoryYearOptions(provId)
        }
    }
    fun selectHistoryYear(year: String) = viewModelScope.launch {
        _selectedHistoryYear.value = year
        val provId = _selectedHistoryProvince.value?.provinceId ?: return@launch
        viewModelScope.launch {
            if (_selectedHistoryType.value!=null){
                _selectedHistoryType.value = null
                _historyData.value = emptyList()
            }
            setHistoryTypeOptions(provId,year)

        }
    }
    fun selectHistoryType(typeId: String) = viewModelScope.launch {
        _selectedHistoryType.value =
            _historyTypeOptions.value.firstOrNull { it.id==typeId }
        val provId = _selectedHistoryProvince.value?.provinceId ?: return@launch
        val year   = _selectedHistoryYear.value ?: return@launch
        _historyData.value =
            provinceRepo.getHistoryData(uid, provId, year, typeId)
    }

    //=== MajorScores 的 select 系列 ===//
    fun selectMajorProvince(provId: String) = viewModelScope.launch(Dispatchers.IO) {
        _selectedMajorProvince.value =
            _majorProvinceOptions.value.firstOrNull { it.provinceId==provId }
        viewModelScope.launch {
            if (_selectedMajorYear.value!=null || _selectedMajorType.value!=null || _selectedMajorBatch.value!=null){
                _selectedMajorYear.value = null
                _majorTypeOptions.value = emptyList()
                _selectedMajorType.value = null
                _majorBatchOptions.value = emptyList()
                _selectedMajorBatch.value = null
                _majorData.value = emptyList()
            }
            setMajorYearOptions(provId)

        }

    }
    fun selectMajorYear(year: String) = viewModelScope.launch(Dispatchers.IO) {
        _selectedMajorYear.value = year
        val provId = _selectedMajorProvince.value?.provinceId ?: return@launch
        viewModelScope.launch {
            if (_selectedMajorType.value!=null || _selectedMajorBatch.value!=null){
                _selectedMajorType.value = null
                _majorBatchOptions.value = emptyList()
                _selectedMajorBatch.value = null
                _majorData.value = emptyList()
            }

            setMajorTypeOptions(provId,year)

        }
    }
    fun selectMajorType(typeId: String) = viewModelScope.launch(Dispatchers.IO) {
        _selectedMajorType.value =
            _majorTypeOptions.value.firstOrNull { it.id==typeId }
        val provId = _selectedMajorProvince.value?.provinceId ?: return@launch
        val year   = _selectedMajorYear.value?: return@launch
        viewModelScope.launch {
            if (_selectedMajorBatch.value!=null){
                _selectedMajorBatch.value = null
                _majorData.value = emptyList()
            }
            setMajorBatchOptions(provId,year,typeId)

        }

    }
    fun selectMajorBatch(batchId: String) = viewModelScope.launch(Dispatchers.IO) {
        _selectedMajorBatch.value =
            _majorBatchOptions.value.firstOrNull { it.id==batchId }
        val provId = _selectedMajorProvince.value?.provinceId?.toInt() ?: return@launch
        val year   = _selectedMajorYear.value?.toInt() ?: return@launch
        val typeId = _selectedMajorType.value?.id?.toInt() ?: return@launch
        _majorData.value = specialDataRepo
            .getMajorData(uid.toInt(), provId, year, typeId, batchId.toInt())
    }

    // lookup helpers
    fun getBatchName(id: String, isChinese: Boolean) =
        provinceRepo.getBatchById(id)?.let { if (isChinese) it.chName else it.enName }.orEmpty()
    fun getEnrollmentTypeName(id: Int, isChinese: Boolean) =
        provinceRepo.getEnrollmentTypeById(id)?.let { if (isChinese) it.chName else it.enName }.orEmpty()
    fun getFilterDescription(id: Int, isChinese: Boolean) =
        provinceRepo.getFilterRuleById(id)?.let { if (isChinese) it.description else it.descriptionEng }.orEmpty()
    fun getSpecialName(id: String, isChinese: Boolean) =
        allSpecialsMap[id]?.let { if (isChinese) it.name_ch else it.name_en }.orEmpty()

    // expose StateFlows
    val historyProvinceOptions = _historyProvinceOptions.asStateFlow()
    val selectedHistoryProvince = _selectedHistoryProvince.asStateFlow()
    val historyYearOptions = _historyYearOptions.asStateFlow()
    val selectedHistoryYear = _selectedHistoryYear.asStateFlow()
    val historyTypeOptions = _historyTypeOptions.asStateFlow()
    val selectedHistoryType = _selectedHistoryType.asStateFlow()
    val historyData = _historyData.asStateFlow()

    val majorProvinceOptions = _majorProvinceOptions.asStateFlow()
    val selectedMajorProvince = _selectedMajorProvince.asStateFlow()
    val majorYearOptions = _majorYearOptions.asStateFlow()
    val selectedMajorYear = _selectedMajorYear.asStateFlow()
    val majorTypeOptions = _majorTypeOptions.asStateFlow()
    val selectedMajorType = _selectedMajorType.asStateFlow()
    val majorBatchOptions = _majorBatchOptions.asStateFlow()
    val selectedMajorBatch = _selectedMajorBatch.asStateFlow()
    val majorData = _majorData.asStateFlow()

    val evaluationList = _evaluationList.asStateFlow()
    val nationalFeatured = _nationalFeatured.asStateFlow()
    val provincialFeatured = _provincialFeatured.asStateFlow()
    val firstClass = _firstClass.asStateFlow()
    val specialGroupItems = _specialGroupItems.asStateFlow()
}