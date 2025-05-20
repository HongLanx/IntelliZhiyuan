package com.intelli.zhiyuan.data.local.ranking

import androidx.room.Dao
import androidx.room.Query
import com.intelli.zhiyuan.data.model.ranking.TrackType

@Dao
interface TrackTypeDao {
    @Query("SELECT * FROM track_type")
    suspend fun getAllTrackTypes(): List<TrackType>

    @Query("SELECT * FROM track_type WHERE track IN (:trackIds)")
    suspend fun getTrackTypes(trackIds: List<String>): List<TrackType>

    @Query("SELECT * FROM track_type WHERE track = :trackId LIMIT 1")
    suspend fun getTrackType(trackId: String): TrackType
}
