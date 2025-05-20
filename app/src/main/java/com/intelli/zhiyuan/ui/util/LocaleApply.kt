package com.intelli.zhiyuan.ui.util

import android.content.Context
import com.intelli.zhiyuan.data.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale

fun applySavedLocale(context: Context) {
    val userPreferences = UserPreferences(context)
    runBlocking {
        val savedLanguage = userPreferences.selectedLanguage.first()
        applyLocale(context, savedLanguage)
    }
}

fun applyLocale(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = context.resources.configuration
    config.setLocale(locale)
    context.createConfigurationContext(config)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}

fun getCurrentLanguage(context: Context): String {
    return context.resources.configuration.locales[0].language
}