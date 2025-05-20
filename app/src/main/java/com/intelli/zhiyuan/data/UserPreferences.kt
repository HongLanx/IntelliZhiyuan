package com.intelli.zhiyuan.data

import android.content.Context
import android.nfc.Tag
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID

const val TAG ="UserPreferences"
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        private val LANGUAGE_KEY = stringPreferencesKey("language")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val TOKEN_KEY   = stringPreferencesKey("auth_token")
    }

    val selectedLanguage: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LANGUAGE_KEY] ?: "zh" // 默认中文
        }

    suspend fun saveLanguage(languageCode: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = languageCode
        }
        Log.d(TAG,"save language: $languageCode")
    }

    suspend fun getOrCreateUserId(): String {
        val prefs = context.dataStore.data.first()
        val existing = prefs[USER_ID_KEY]
        return if (existing.isNullOrBlank()) {
            val newId = UUID.randomUUID().toString()
            context.dataStore.edit { it[USER_ID_KEY] = newId }
            newId
        } else existing
    }

    suspend fun setToken(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun getToken(): String? =
        context.dataStore.data.map { it[TOKEN_KEY] }.first()
}

