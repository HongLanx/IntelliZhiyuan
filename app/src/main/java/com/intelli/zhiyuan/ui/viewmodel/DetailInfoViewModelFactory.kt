package com.intelli.zhiyuan.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*

class DetailInfoViewModelFactory(
    private val application: Application,
    private val uid: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailInfoViewModel::class.java)) {
            val handle = SavedStateHandle(mapOf("uid" to uid))
            @Suppress("UNCHECKED_CAST")
            return DetailInfoViewModel(application, handle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
