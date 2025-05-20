package com.intelli.zhiyuan.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    // 定义页面 Tab
    private val _selectedTab = MutableStateFlow("home")
    val selectedTab: StateFlow<String> = _selectedTab

    private val _isGridMode = MutableStateFlow(false)
    val isGridMode: StateFlow<Boolean> = _isGridMode

    fun setSelectedTab(tab: String) {
        _selectedTab.value = tab
    }

    fun toggleGridMode() {
        _isGridMode.value = !_isGridMode.value
    }
}
