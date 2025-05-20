package com.intelli.zhiyuan.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.intelli.zhiyuan.ui.components.FunctionArea
import com.intelli.zhiyuan.ui.components.TopBar
import com.intelli.zhiyuan.ui.theme.IntelliZhiyuanTheme
import com.intelli.zhiyuan.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onNavigateToScoreQuery: () -> Unit={},
    onNavigateToRankQuery: () -> Unit={},
    onNavigateToSpecialsInfo: () -> Unit={},
    onNavigateToBasicRecommend: () -> Unit={},
    onNavigateToIntelliApply: () -> Unit ={},
    viewModel: HomeViewModel = viewModel()
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val isGridMode by viewModel.isGridMode.collectAsState()

    Scaffold(
        topBar = {
            TopBar(
                onToggleMode = { viewModel.toggleGridMode() }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                "home" -> FunctionArea(
                    onNavigateToScoreQuery,
                    onNavigateToRankQuery,
                    onNavigateToSpecialsInfo,
                    onNavigateToBasicRecommend,
                    onNavigateToIntelliApply,
                    isGridMode = isGridMode)
            }
        }
    }
}

// HomeViewModel 负责 UI 状态
@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    IntelliZhiyuanTheme {
        HomeScreen(viewModel = HomeViewModel())
    }
}

// 预览功能区（列表模式）
@Preview(showBackground = true)
@Composable
fun PreviewFunctionList() {
    FunctionArea(isGridMode = false)
}

// 预览功能区（双栏网格模式）
@Preview(showBackground = true)
@Composable
fun PreviewFunctionGrid() {
    FunctionArea(isGridMode = true)
}
