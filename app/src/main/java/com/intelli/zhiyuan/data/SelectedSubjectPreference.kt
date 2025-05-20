package com.intelli.zhiyuan.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.subjectDataStore by preferencesDataStore(name = "subject_prefs")

class SelectedSubjectPreference(private val context: Context) {
    companion object {
        val SUBJECT_CODE_KEY = stringPreferencesKey("selected_subject_code")
    }

    val selectedSubjectCodeFlow: Flow<String?> = context.subjectDataStore.data.map { preferences ->
        preferences[SUBJECT_CODE_KEY]
    }

    suspend fun saveSelectedSubjectCode(code: String) {
        context.subjectDataStore.edit { preferences ->
            preferences[SUBJECT_CODE_KEY] = code
        }
    }
}
