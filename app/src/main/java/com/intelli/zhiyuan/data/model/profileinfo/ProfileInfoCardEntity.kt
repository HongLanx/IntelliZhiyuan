package com.intelli.zhiyuan.data.model.profileinfo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile_info_card")
data class ProfileInfoCardEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val provinceId: String,
    val examYear: String,
    val subjectSelectionCode: String,
    val level: String,
    val track:String,
    val examId: Int?,
    val selected: Boolean,
    val examMode :String,
    val examScore:Int
) {


    fun toProfileInfoCard(): ProfileInfoCard {
        return ProfileInfoCard(
            id = id,
            provinceId = provinceId,
            examYear = examYear,
            subjectSelectionCode = subjectSelectionCode,
            level = level,
            track =track,
            examId = examId,
            selected = selected,
            examMode = examMode,
            examScore = examScore
        )
    }

}
