package com.intelli.zhiyuan.data.local.specialsinfo

import androidx.room.Dao
import androidx.room.Query
import com.intelli.zhiyuan.data.model.specialsinfo.SpecialsInfo

@Dao
interface SpecialsInfoDao {
    @Query("SELECT * FROM specials")
    suspend fun getAllSpecials(): List<SpecialsInfo>

    @Query("SELECT * FROM specials WHERE special_id = :specialId LIMIT 1")
    suspend fun getSpecialFromId(specialId:String):SpecialsInfo

    @Query(
        """
        SELECT * FROM specials 
        WHERE (:query == '' OR 
              ( :isChinese = 1 AND name_ch LIKE '%' || :query || '%') OR 
              ( :isChinese = 0 AND name_en LIKE '%' || :query || '%'))
        """
    )
    suspend fun getSpecialsFiltered(query: String, isChinese: Boolean): List<SpecialsInfo>

    @Query("SELECT * FROM specials WHERE level3_id = :level3Id")
    suspend fun getByLevelThree(level3Id: String): List<SpecialsInfo>

    @Query("SELECT * FROM specials WHERE level3_id IN (:level3Ids)")
    suspend fun getByLevelThrees(level3Ids: List<String>): List<SpecialsInfo>

    // —— 新增：批量按 ID 查询，避免多次 getSpecialFromId 调用 ——
    @Query("SELECT * FROM specials WHERE special_id IN (:ids)")
    suspend fun getSpecialsByIds(ids: List<String>): List<SpecialsInfo>
}
