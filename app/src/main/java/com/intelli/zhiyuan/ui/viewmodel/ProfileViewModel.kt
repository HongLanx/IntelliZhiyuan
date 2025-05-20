package com.intelli.zhiyuan.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel : ViewModel() {
    private val _selectedTab = MutableStateFlow("profile")
    val selectedTab: StateFlow<String> = _selectedTab

    fun setSelectedTab(tab: String) {
        _selectedTab.value = tab
    }
}
