package com.intelli.zhiyuan.data.model.profileinfo


data class ProfileInfoCard(
    val id: Int = 0,  // 本地 ID（如使用数据库，可自增）
    val provinceId: String,
    val examYear: String,         // 原始年份（映射前）
    val subjectSelectionCode: String,
    val level: String,
    val track: String,
    val examId: Int? = null,       // 对应的 examId（查询后获得）
    var selected: Boolean = false,  // 是否选中
    val examMode:String = "0000",
    val examScore:Int
){
    fun toEntity(): ProfileInfoCardEntity {
        return ProfileInfoCardEntity(
            id = id,
            provinceId = provinceId,
            examYear = examYear,
            subjectSelectionCode = subjectSelectionCode,
            level = level,
            track = track,
            examId = examId,
            selected = selected,
            examMode = examMode,
            examScore=examScore
        )
    }
}
