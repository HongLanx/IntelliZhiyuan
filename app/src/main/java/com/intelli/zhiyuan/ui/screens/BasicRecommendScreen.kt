package com.intelli.zhiyuan.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.intelli.zhiyuan.R
import com.intelli.zhiyuan.ui.components.BasicInfoCard
import com.intelli.zhiyuan.ui.components.UiState
import com.intelli.zhiyuan.ui.viewmodel.BasicRecommendViewModel

// BasicRecommendScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicRecommendScreen(
    viewModel: BasicRecommendViewModel = viewModel()
) {
    // 明确给一个初始值 Loading
    val uiState by viewModel.uiState.collectAsState(initial = UiState.Loading)
    val searchQuery by viewModel.searchQuery.collectAsState()
    val showFav by viewModel.showOnlyFavorites.collectAsState()
    val isCh by viewModel.isChinese.collectAsState()
    val favs by viewModel.favoriteUids.collectAsState(initial = emptySet())
    val examScore by viewModel.examScore.collectAsState()

    var showSearch by remember { mutableStateOf(false) }
    var textState by remember { mutableStateOf(TextFieldValue(searchQuery)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.school_recommendation)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Blue,
                    titleContentColor = Color.White
                ),
                actions = {
                    // 收藏筛选按钮
                    IconButton(onClick = { viewModel.toggleShowOnlyFavorites() }) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = stringResource(id = R.string.favorite),
                            tint = if (showFav) Color.Yellow else Color.White
                        )
                    }
                    // 搜索按钮
                    IconButton(onClick = { showSearch = !showSearch }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = stringResource(id = R.string.search),
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (uiState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is UiState.Success -> {
                    Column(Modifier.fillMaxSize()) {
                        if (showSearch) {
                            OutlinedTextField(
                                value = textState,
                                onValueChange = {
                                    textState = it
                                    viewModel.updateSearchQuery(it.text)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                placeholder = {
                                    Text(stringResource(R.string.search_placeholder))
                                }
                            )
                        }
                        LazyColumn(Modifier.fillMaxSize()) {
                            items((uiState as UiState.Success).data) { item ->
                                BasicInfoCard(
                                    displayItem      = item,
                                    isChinese        = isCh,
                                    isFavorite       = favs.contains(item.basicInfo.uid),
                                    onToggleFavorite = { viewModel.toggleFavorite(item.basicInfo) },
                                    onCardClick      = null,
                                    isRecommendCard  = true,
                                    examScore        = examScore
                                )
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    Text(
                        text = stringResource(R.string.loading_failure),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}
