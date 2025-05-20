package com.intelli.zhiyuan.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "favorites_specials_prefs")

class FavoriteSpecialsRepository(private val context: Context) {
    companion object {
        val FAVORITE_KEY = stringSetPreferencesKey("favorite_specials")
    }

    val favoriteSpecials: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[FAVORITE_KEY] ?: emptySet()
    }

    suspend fun addFavorite(specialId: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[FAVORITE_KEY] ?: emptySet()
            preferences[FAVORITE_KEY] = current + specialId
        }
    }

    suspend fun removeFavorite(specialId: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[FAVORITE_KEY] ?: emptySet()
            preferences[FAVORITE_KEY] = current - specialId
        }
    }
}