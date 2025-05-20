package com.intelli.zhiyuan.ui.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.intelli.zhiyuan.data.UserPreferences
import com.intelli.zhiyuan.ui.util.applyLocale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale

const val TAG ="LanguageViewModel"
class LanguageViewModel(application: Application) : AndroidViewModel(application) {
    private val userPreferences = UserPreferences(application)

    private val _selectedLanguage = MutableStateFlow("zh")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    init {
        viewModelScope.launch {
            val savedLanguage = userPreferences.selectedLanguage.first()
            _selectedLanguage.value = savedLanguage
        }
    }

    fun changeLanguage(context: Context, languageCode: String) {
        viewModelScope.launch {
            userPreferences.saveLanguage(languageCode)
            _selectedLanguage.value = languageCode
            applyLocale(context, languageCode)
            Log.d(TAG, "change Language to: $languageCode")
        }
    }

}
