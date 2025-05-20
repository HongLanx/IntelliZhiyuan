package com.intelli.zhiyuan.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.intelli.zhiyuan.data.ExamPreference
import com.intelli.zhiyuan.data.SelectedSubjectPreference
import com.intelli.zhiyuan.data.UserPreferences
import com.intelli.zhiyuan.data.local.profileinfo.ProfileInfoDatabase
import com.intelli.zhiyuan.data.model.ranking.LevelType
import com.intelli.zhiyuan.data.model.ranking.Province
import com.intelli.zhiyuan.data.repository.RankQueryRepository
import com.intelli.zhiyuan.data.model.profileinfo.ProfileInfoCard
import com.intelli.zhiyuan.data.model.ranking.Exam
import com.intelli.zhiyuan.data.model.ranking.TrackType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val PIVMTAG ="ProfileInfoViewModel"
class ProfileInfoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RankQueryRepository(application)
    private val examPreference = ExamPreference(application)
    private val userPreferences = UserPreferences(application)
    private val selectedSubjectPreference = SelectedSubjectPreference(application)

    private val database = ProfileInfoDatabase.getDatabase(application)
    private val cardDao = database.profileInfoCardDao()

    // 卡片列表
    private val _cards = MutableStateFlow<List<ProfileInfoCard>>(emptyList())
    val cards: StateFlow<List<ProfileInfoCard>> = _cards

    private val _isChinese = MutableStateFlow(true)
    val isChinese: StateFlow<Boolean> = _isChinese.asStateFlow()


    val isPreReform = MutableStateFlow(false)
    val isPostReform3Plus3 = MutableStateFlow(false)
    val isPostReform3Plus3Zhejiang = MutableStateFlow(false)
    val isPostReform3Plus1Plus2 = MutableStateFlow(false)

    // 下拉菜单选项
    val provinceOptions = MutableStateFlow<List<Province>>(emptyList())
    val yearOptions = MutableStateFlow<List<String>>(emptyList())
    val levelOptions = MutableStateFlow<List<LevelType>>(emptyList())


    val subjectOptionsUnified = MutableStateFlow<List<String>>(emptyList())

    // 新卡片创建表单状态
    val selectedProvinceId = MutableStateFlow<String?>(null)
    val selectedYear = MutableStateFlow<String?>(null)

    val selectedProvince = MutableStateFlow<Province?>(null)
    val selectedLevelType = MutableStateFlow<LevelType?>(null)
    val selectedTrackType = MutableStateFlow<TrackType?>(null)
    val selectedSubjectCode = MutableStateFlow<String>("0000000")
    val selectedSubjectDescription = MutableStateFlow<String>("")
    val selectedFirstSubjectDescription = MutableStateFlow<String>("")
    val selectedSecondSubjectsDescription = MutableStateFlow<String>("")
    val examScore = MutableStateFlow<Int?>(null)


    val selectedLevel = MutableStateFlow<String?>(null)
    private val _provinceNames = MutableStateFlow<Map<String, String>>(emptyMap())
    val provinceNames: StateFlow<Map<String, String>> = _provinceNames

    fun loadProvinceName(provinceId: String) {
        viewModelScope.launch {
            val name = if (isChinese.value)
                repository.getProvince(provinceId).ch_name
            else
                repository.getProvince(provinceId).en_name
            _provinceNames.update { it + (provinceId to name) }
        }
    }
    private val _levelNames = MutableStateFlow<Map<String, String>>(emptyMap())
    val levelNames: StateFlow<Map<String, String>> = _levelNames

    fun loadLevelName(levelId: String) {
        viewModelScope.launch {
            val name = if (isChinese.value)
                repository.getLevelType(levelId).ch_name
            else
                repository.getLevelType(levelId).en_name
            _levelNames.update { it + (levelId to name) }
        }
    }
    // 额外变量：是否智能志愿（例如 examYear in {"2025","2026","2027"} -> applyAvailable）
    val applyAvailable: Boolean
        get() = selectedYear.value in listOf("2025", "2026", "2027")

    init {
        viewModelScope.launch {
            val savedLanguage = userPreferences.selectedLanguage.first()
            _isChinese.value = savedLanguage == "zh"
            provinceOptions.value = repository.getAllProvinces()

            // 从数据库读取卡片
            val savedCards = cardDao.getAllCards().map { it.toProfileInfoCard() }
            _cards.value = savedCards

            // 对于后改革，我们简化为单选（例如针对3+3）或分为两部分（3+1+2）
            subjectOptionsUnified.value = if(isChinese.value) listOf(
                "物理",
                "化学",
                "生物",
                "历史",
                "政治",
                "地理",
                "技术"
            )
            else listOf(
                "Physics",
                "Chemistry",
                "Biology",
                "History",
                "Politics",
                "Geography",
                "technology"
            )
        }
    }

    fun selectProvince(provinceId: String) {
        selectedProvinceId.value = provinceId
        viewModelScope.launch {
            selectedProvince.value=repository.getProvince(provinceId)
            // 获取年份选项，并额外加上 2025, 2026, 2027
            val years = repository.getYearsByProvince(provinceId).toMutableList()
            years.addAll(listOf("2025", "2026", "2027"))
            yearOptions.value = years.distinct().sorted()
            // 清除后续选项
            selectedYear.value = null
            selectedSubjectCode.value = "0000000"
            selectedLevel.value = null
            selectedSubjectDescription.value=""
            selectedFirstSubjectDescription.value=""
            selectedSecondSubjectsDescription.value=""
        }
    }

    fun selectYear(year: String) {
        selectedYear.value = year
        // 根据省份和年份判断选科模式，后续UI可据此显示不同组件
        selectedSubjectCode.value = "0000000"
        selectedSubjectDescription.value=""
        selectedFirstSubjectDescription.value=""
        selectedSecondSubjectsDescription.value=""

        selectedLevel.value = null
        viewModelScope.launch {
            val yearNum=year.toIntOrNull()
            if (selectedProvinceId.value == "31" && yearNum !=null) {
                if (yearNum < 2017)
                    setIsPreReform()
                else
                    setIsPostReform3Plus3()
            }
            else if (selectedProvinceId.value == "33" && yearNum != null){
                if (yearNum < 2017)
                    setIsPreReform()
                else
                    setIsPostReform3Plus3Zhejiang()
            }
            else if (selectedProvinceId.value == "33" && yearNum != null){
                if (yearNum < 2017)
                    setIsPreReform()
                else
                    setIsPostReform3Plus3Zhejiang()
            }
            else if (selectedProvinceId.value in listOf("11","37","12","46") && yearNum != null){
                if (yearNum < 2020)
                    setIsPreReform()
                else
                    setIsPostReform3Plus3()
            }
            else if (selectedProvinceId.value in listOf("50","43","44", "32", "13", "42", "35", "21") && yearNum != null){
                if (yearNum < 2021)
                    setIsPreReform()
                else
                    setIsPostReform3Plus1Plus2()
            }
            else if (selectedProvinceId.value in listOf("62", "23", "22", "34", "36", "52", "45") && yearNum != null){
                if (yearNum < 2024)
                    setIsPreReform()
                else
                    setIsPostReform3Plus1Plus2()
            }
            else if (selectedProvinceId.value in listOf("41", "53", "51", "14", "15", "61", "63", "64") && yearNum != null){
                if (yearNum < 2025)
                    setIsPreReform()
                else
                    setIsPostReform3Plus1Plus2()
            }
            else{
                setIsPreReform()
            }
        }
    }

    private fun initSubjectSelection(){
        selectedLevel.value = null
    }
    suspend fun finishSubjectSelection(){
        val provinceId = selectedProvinceId.value ?: return
        val year = selectedYear.value ?: return
        // 映射年份：若 year in {"2025","2026","2027"} 则映为 "2024"
        val mappedYear = if (year in listOf("2025", "2026", "2027")) "2024" else year
        if (checkSelectedSubject()){
            val track = getTrack()
            Log.d(PIVMTAG, "current track: $track")

            selectedTrackType.value = repository.getTrackType(track)
            levelOptions.value=repository.getLevelTypes(repository.getLevelsByProvinceYearTrack(provinceId, mappedYear,track))

        }
        if (isPreReform.value || isPostReform3Plus3.value || isPostReform3Plus3Zhejiang.value) {
            selectedSubjectDescription.value = transferSubjectCodeToDescription(
                subjectCode = selectedSubjectCode.value,
            )
        }

        else if (isPostReform3Plus1Plus2.value){
            val selectedFirstSubject =
                if(selectedSubjectCode.value[0]=='1') subjectOptionsUnified.value.getOrNull(0)
                else if(selectedSubjectCode.value[3]=='1')  subjectOptionsUnified.value.getOrNull(3)
                else ""
            val selectedSecondSubjects =
                selectedSubjectCode.value.mapIndexedNotNull { index, c ->
                    if (c == '1' && index != 0 && index != 3) subjectOptionsUnified.value.getOrNull(index) else null
                }
            val selectedFirstSubjectDescrip= if(isChinese.value) "首选科目: " else "First Subject: "
            val selectedSecondSubjectsDescrip= if(isChinese.value) "次选科目: " else "Second Subjects: "
            if(selectedFirstSubject == ""){
                selectedFirstSubjectDescription.value=if (isChinese.value) "未选择" else "None Selected"
            }
            else{
                selectedFirstSubjectDescription.value = selectedFirstSubjectDescrip + selectedFirstSubject
            }
            if (selectedSecondSubjects.isEmpty()){
                selectedSecondSubjectsDescription.value=if (isChinese.value) "未选择" else "None Selected"
            }
            else if (selectedSecondSubjects.size!=2){
                selectedSecondSubjectsDescription.value=if (isChinese.value) "未选完" else "Selection Not Completed"
            }
            else{
                selectedSecondSubjectsDescription.value= selectedSecondSubjectsDescrip +selectedSecondSubjects.joinToString(separator = " ")
            }
        }
    }


    fun selectSubjectPreRefrom(isNatural: Boolean) {
//        selectedSubject.value = subject
        initSubjectSelection()
        viewModelScope.launch{
            setSubjectPreReform(isNatural)
            finishSubjectSelection()
        }
    }

    private fun setSubjectPreReform(isNatural: Boolean) {
        selectedSubjectCode.value = if (isNatural) "1110000" else "0001110"
    }

    // 后改革：多选模式，通过 toggleSubject 更新选中状态
    fun toggleSubject(index: Int) {
        initSubjectSelection()
        val current = selectedSubjectCode.value.toCharArray()
        // 如果当前位置已经为 1，则允许反选（即设置为 0）
        if (current[index] == '1') {
            current[index] = '0'
        } else {
            // 当前为 0，先计算已有的“1”的个数
            val countOnes = current.count { it == '1' }
            // 如果已经达到 3 个，不允许再选择
            if (countOnes >= 3) {
                // 此处可以触发 UI 通知下拉菜单收起（由 UI 层处理），或者简单忽略该操作
                return
            }
            current[index] = '1'
        }
        selectedSubjectCode.value = String(current)
        viewModelScope.launch{
            finishSubjectSelection()
        }
    }


    // 若后改革 3+1+2 模式：
    fun selectFirstSubject(index: Int) {
        initSubjectSelection()
        val current = selectedSubjectCode.value.toCharArray()
        val mappedIndex=index*3
        Log.d(PIVMTAG,"index: $index, mappedIndex:$mappedIndex ")
        if (index ==0 && current[3]=='1'){
            current[0] ='1'
            current[3]='0'
        }
        else if (index == 1 && current[0]=='1')
        {
            current[0]='0'
            current[3]='1'
        }
        else
            current[mappedIndex] = if (current[mappedIndex] == '1') '0' else '1'

        Log.d(PIVMTAG,"current code: ${String(current)}")
        selectedSubjectCode.value = String(current)
        viewModelScope.launch{
            finishSubjectSelection()
        }
    }

    fun toggleSecondSubject(index: Int) {
        initSubjectSelection()
        if (index == 0){
            toggleSubject(1)
        }
        else if (index == 1){
            toggleSubject(2)
        }
        else if (index == 2){
            toggleSubject(5)
        }
        else if (index == 3){
            toggleSubject(4)
        }
        else
            return
    }

    fun selectLevel(level: LevelType) {
        selectedLevel.value = level.level.toString()
        selectedLevelType.value = level
        examScore.value = null
    }

    fun setScore(textValue:TextFieldValue){
        if (textValue.text.toIntOrNull() != null)
        {
            val score = textValue.text.toInt()
            if (score in 1..900)
                // TODO: 这里的分数判断逻辑可以修改，根据各省和各年份的实际情况进行判断
                examScore.value = score
        }
        viewModelScope.launch {
            selectedSubjectPreference.saveSelectedSubjectCode(selectedSubjectCode.value)
            // 根据当前选项映射 exam:
            val provinceId = selectedProvinceId.value ?: return@launch
            val year = selectedYear.value ?: return@launch
            val level = selectedLevel.value ?: return@launch
            val examScore = examScore.value ?: return@launch
            // 映射年份：若 year in {"2025","2026","2027"} 则映为 "2024"
            val mappedYear = if (year in listOf("2025", "2026", "2027")) "2024" else year
            // 确定 track 根据选科：
            val track = getTrack()
            // 获取 exam
            val exam = repository.getExam(provinceId, mappedYear, track, level)
            exam?.let {
                // 保存 examId到偏好
                examPreference.saveExam(it,examScore)
            }
        }
    }

    private fun getTrack(): String{
        return when {
            // pre-reform:
            isPreReform.value -> {
                if (selectedSubjectCode.value == "1110000") "1" else "2"
            }
            // 后改革：区分 3+3 和 3+1+2
            isPostReform3Plus3.value || isPostReform3Plus3Zhejiang.value -> "3" // 3+3 模式
            isPostReform3Plus1Plus2.value -> {
                if (selectedSubjectCode.value[0] == '1') "6" else "7"
            }
            else -> ""
        }
    }

    private fun getExamMode(): String{
        return when {
            // pre-reform:
            isPreReform.value -> "1000"
            // 后改革：区分 3+3 和 3+1+2
            isPostReform3Plus3.value -> "0100" // 3+3 模式
            isPostReform3Plus3Zhejiang.value -> "0010"
            isPostReform3Plus1Plus2.value -> "0001"
            else -> "0000"
        }
    }

     fun getProvinceName(provinceId: String):String{
        var provinceName:String = ""
        viewModelScope.launch {
            provinceName = if (isChinese.value) repository.getProvince(provinceId).ch_name else repository.getProvince(provinceId).en_name
        }
        return provinceName
    }
    fun getTrackName(trackId: String):String{
        var trackName:String = ""
        viewModelScope.launch {
            trackName = if (isChinese.value) repository.getTrackType(trackId).ch_name else repository.getTrackType(trackId).en_name
        }
        return trackName
    }
    fun getLevelName(levelId: String):String{
        var levelName:String = ""
        viewModelScope.launch {
            levelName = if (isChinese.value) repository.getLevelType(levelId).ch_name else repository.getLevelType(levelId).en_name
        }
        return levelName
    }



    // 创建新卡片
    fun createNewCard() {
        var examId:Int? = null
        val provinceId = selectedProvinceId.value ?: return
        val examYear = selectedYear.value ?: return
        val trackType = selectedTrackType.value?.track ?: return
        val level = selectedLevel.value ?: return
        val examScore = examScore.value ?: return
        Log.d(PIVMTAG, "current examScore : $examScore")

        viewModelScope.launch{
            val mappedYear = if (examYear in listOf("2025", "2026", "2027")) "2024" else examYear
            val track = getTrack()
            val exam = repository.getExam(provinceId, mappedYear, track, level)
            examId = exam?.id
            Log.d(PIVMTAG,"current examId: $examId")

            val examMode = getExamMode()
            val newCard = ProfileInfoCard(
                provinceId = provinceId,
                examYear = examYear,
                subjectSelectionCode = selectedSubjectCode.value,
                level = level,
                track = trackType,
                examId = examId,
                selected = false,
                examMode = examMode,
                examScore = examScore
            )
            Log.d(PIVMTAG,"current examId: $examId")


            newCard.let { card ->
                viewModelScope.launch {
                    val entity = card.toEntity()
                    cardDao.insertCard(entity)
                    _cards.value = cardDao.getAllCards().map { it.toProfileInfoCard() }
                }
            }
        }
        // TODO: 可以优化一下，使得如果_card中已经有跟newCard的各个数据都相同的card，不再加入_card

    }

    // 删除卡片
    fun deleteCard(card: ProfileInfoCard) {
        viewModelScope.launch {
            cardDao.deleteCard(card.toEntity())
            _cards.value = cardDao.getAllCards().map { it.toProfileInfoCard() }
        }
    }


    fun toggleCardSelection(card: ProfileInfoCard) {
        val updatedCards = _cards.value.map { c ->
            if (c == card) {
                Log.d(PIVMTAG,"toggle current pid before selection: ${c.provinceId}")
                Log.d(PIVMTAG,"toggle current selected before selection : ${c.selected}")
                Log.d(PIVMTAG,"toggle current eid before selection: ${c.examId}")

                val newSelected = !c.selected
                if (newSelected && c.examId != null) {
                    viewModelScope.launch {
                        selectedSubjectPreference.saveSelectedSubjectCode(card.subjectSelectionCode)
                        examPreference.saveExamId(c.examId)
                        examPreference.saveExamProvince(c.provinceId.toInt())
                        examPreference.saveExamYear(c.examYear.toInt())
                        examPreference.saveExamTrack(c.track.toInt())
                        examPreference.saveExamScore(c.examScore)
                    }
                }
                viewModelScope.launch {
                    cardDao.insertCard(c.copy(selected = newSelected).toEntity())
                }
                c.copy(selected = newSelected)
            } else c
        }
        _cards.value = updatedCards
    }



    /**
     * 根据选科二进制码和当前 trackType（用于确定 pre-reform 时理科/文科）
     * 将7位二进制字符串转换为文本描述。
     *
     * 对于 pre-reform 模式，"1110000" 表示理科，"0001110" 表示文科；
     * 对于后改革模式，直接将每个位置的 1 映射到对应学科名称，
     * 顺序固定：["物理", "化学", "生物", "历史", "政治", "地理", "技术"]。
     */
    fun transferSubjectCodeToDescription(
        subjectCode: String,
        examMode:String ="0000"
    ): String {
        if (examMode!="0000") {
            when {
                examMode == "1000" -> return when (subjectCode) {
                    "1110000" -> if (isChinese.value) "理科" else "Natural Science"
                    "0001110" -> if (isChinese.value) "文科" else "Social Science" // 此处可根据需求调整：例如文科对应不同的文本
                    else -> if (isChinese.value) "非法选科" else "Invalid Subject Selection"
                }
                (examMode == "0100" || examMode == "0010") -> {
                    // 后改革：将 subjectCode 中的每一位为 1 的学科取出来，用分号分隔
                    val selectedSubjects = subjectCode.mapIndexedNotNull { index, c ->
                        if (c == '1') subjectOptionsUnified.value.getOrNull(index) else null
                    }
                    return if (selectedSubjects.isEmpty() ) {
                        if (isChinese.value) "未选择" else "None Selected"
                    } else if(!checkSubjectCode(subjectCode)){
                        if (isChinese.value) "未选完" else "Selection Not Completed"
                    } else {
                        selectedSubjects.joinToString(separator = " ")
                    }
                }
                examMode == "0001" -> {
                    val selectedFirstSubject =
                        if (subjectCode[0] == '1') subjectOptionsUnified.value.getOrNull(0)
                        else if (subjectCode[3] == '1') subjectOptionsUnified.value.getOrNull(3)
                        else ""
                    val selectedSecondSubjects =
                        subjectCode.mapIndexedNotNull { index, c ->
                            if (c == '1' && index != 0 && index != 3) subjectOptionsUnified.value.getOrNull(
                                index
                            ) else null
                        }
                    return if (selectedFirstSubject == "" || selectedSecondSubjects.isEmpty() || selectedSecondSubjects.size != 2) {
                        if (isChinese.value) "未选择" else "None Selected"
                    } else {
                        selectedFirstSubject + " " + selectedSecondSubjects.joinToString(separator = " ")
                    }
                }

            }
        }
        return if (isPreReform.value) {
            when (subjectCode) {
                "1110000" -> if (isChinese.value) "理科" else "Natural Science"
                "0001110" -> if (isChinese.value) "文科" else "Social Science" // 此处可根据需求调整：例如文科对应不同的文本
                else -> if (isChinese.value) "非法选科" else "Invalid Subject Selection"
            }
        } else if (isPostReform3Plus3.value || isPostReform3Plus3Zhejiang.value) {
            // 后改革：将 subjectCode 中的每一位为 1 的学科取出来，用分号分隔
            val selectedSubjects = subjectCode.mapIndexedNotNull { index, c ->
                if (c == '1') subjectOptionsUnified.value.getOrNull(index) else null
            }
            if (selectedSubjects.isEmpty() ) {
                if (isChinese.value) "未选择" else "None Selected"
            } else if(!checkSubjectCode(subjectCode)){
                if (isChinese.value) "未选完" else "Selection Not Completed"
            }
            else
            {
                selectedSubjects.joinToString(separator = " ")
            }
        }
        else if (isPostReform3Plus1Plus2.value){
            val selectedFirstSubject =
                if(subjectCode[0]=='1') subjectOptionsUnified.value.getOrNull(0)
                else if(subjectCode[3]=='1')  subjectOptionsUnified.value.getOrNull(3)
                else ""
            val selectedSecondSubjects =
                subjectCode.mapIndexedNotNull { index, c ->
                    if (c == '1' && index != 0 && index != 3) subjectOptionsUnified.value.getOrNull(index) else null
                }
            if(selectedFirstSubject == "" || selectedSecondSubjects.isEmpty() || selectedSecondSubjects.size!=2){
                if (isChinese.value) "未选择" else "None Selected"
            }
            else{
                 selectedFirstSubject + " " +selectedSecondSubjects.joinToString(separator = " ")
            }
        }
        else
            if (isChinese.value) "非法选科" else "Invalid Subject Selection"
    }
    private fun checkSelectedSubject() : Boolean{
        val countOnes = selectedSubjectCode.value.toCharArray().count { it == '1' }
        return countOnes == 3
    }
    private fun checkSubjectCode(subjectCode: String):Boolean{
        val countOnes = subjectCode.toCharArray().count { it == '1' }
        return countOnes == 3
    }

    private fun setIsPreReform(){
        isPreReform.value = true
        isPostReform3Plus3.value =false
        isPostReform3Plus3Zhejiang.value=false
        isPostReform3Plus1Plus2.value=false
    }
    private fun setIsPostReform3Plus3(){
        isPreReform.value = false
        isPostReform3Plus3.value =true
        isPostReform3Plus3Zhejiang.value=false
        isPostReform3Plus1Plus2.value=false
    }
    private fun setIsPostReform3Plus3Zhejiang(){
        isPreReform.value = false
        isPostReform3Plus3.value =false
        isPostReform3Plus3Zhejiang.value=true
        isPostReform3Plus1Plus2.value=false
    }
    private fun setIsPostReform3Plus1Plus2(){
        isPreReform.value = false
        isPostReform3Plus3.value =false
        isPostReform3Plus3Zhejiang.value=false
        isPostReform3Plus1Plus2.value=true
    }



}
