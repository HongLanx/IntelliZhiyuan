// com/intelli/zhiyuan/data/repository/FavoriteRepository.kt
package com.intelli.zhiyuan.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.intelli.zhiyuan.data.UserPreferences
import com.intelli.zhiyuan.network.FavoriteApi
import com.intelli.zhiyuan.network.NetworkModule
import com.intelli.zhiyuan.network.UidRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "favorites_university_prefs")

class FavoriteRepository(
    private val context: Context
) {
    // 把 api 的获取延迟到第一次使用时
    private val api: FavoriteApi by lazy {
        NetworkModule.provideFavoriteApi(context)
    }
    private val FRTAG="FavoriteRepository"
    companion object {
        private val FAVORITE_KEY = stringSetPreferencesKey("favorite_uids")
    }

    private val dataStore = context.dataStore  // 你的原来 DataStore 实例

    val favoriteUids: Flow<Set<String>> =
        dataStore.data.map { prefs -> prefs[FAVORITE_KEY] ?: emptySet() }

    suspend fun addFavorite(uid: String) {
        // 1. 本地更新
        context.dataStore.edit { prefs ->
            val current = prefs[FAVORITE_KEY] ?: emptySet()
            prefs[FAVORITE_KEY] = current + uid
        }

        try {
            // 2. 获取 userId 并调用服务端接口
            val userId = UserPreferences(context).getOrCreateUserId()
            Log.d(FRTAG, "addFavorite → userId=$userId, uid=$uid")

            api.addFavorite(userId, UidRequest(uid))
            Log.d(FRTAG, "Add Favorite successfully, token saved")
        } catch (e: HttpException) {
            Log.w(FRTAG, "Add Favorite HTTP error (status ${e.code()}), proceeding offline", e)
        } catch (e: IOException) {
            Log.w(FRTAG, "Add Favorite network error, proceeding offline", e)
        } catch (e: Exception) {
            Log.w(FRTAG, "Unexpected Add Favorite error, proceeding offline", e)
        }
    }

    suspend fun removeFavorite(uid: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[FAVORITE_KEY] ?: emptySet()
            prefs[FAVORITE_KEY] = current - uid
        }
        try {
            val userId = UserPreferences(context).getOrCreateUserId()
            api.removeFavorite(userId, uid)
            Log.d(FRTAG, "Remove Favorite successfully, token saved")
        } catch (e: HttpException) {
            Log.w(FRTAG, "Remove Favorite HTTP error (status ${e.code()}), proceeding offline", e)
        } catch (e: IOException) {
            Log.w(FRTAG, "Remove Favorite network error, proceeding offline", e)
        } catch (e: Exception) {
            Log.w(FRTAG, "Unexpected Remove Favorite error, proceeding offline", e)
        }
    }

    suspend fun fetchFavoritesFromServer(): List<String> {
        val userId = UserPreferences(context).getOrCreateUserId()
        return api.listFavorites(userId)
    }

    suspend fun getRecommendations(): List<String> {
        val userId = UserPreferences(context).getOrCreateUserId()
        return try {
            api.recommend(userId)
        } catch (e: Exception) {
            // 网络/鉴权失败则返回空列表
            emptyList()
        }
    }
}