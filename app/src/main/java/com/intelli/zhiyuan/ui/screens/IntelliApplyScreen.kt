package com.intelli.zhiyuan.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
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
import com.intelli.zhiyuan.ui.components.IntelliApplyCard
import com.intelli.zhiyuan.ui.components.UiState
import com.intelli.zhiyuan.ui.viewmodel.IntelliApplyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntelliApplyScreen(
    viewModel: IntelliApplyViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState(initial = UiState.Loading)
    val searchQuery by viewModel.searchQuery.collectAsState()
    val showFav by viewModel.showOnlyFavorites.collectAsState()
    val isCh by viewModel.isChinese.collectAsState()
    val favs by viewModel.favoriteUids.collectAsState(initial = emptySet())

    val lowestSection    by viewModel.lowestSection.collectAsState()

    var showSearch by remember { mutableStateOf(false) }
    var textState by remember { mutableStateOf(TextFieldValue(searchQuery)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.intelligent_application)) },
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
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (uiState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
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
                                placeholder = { Text(stringResource(R.string.search_placeholder)) }
                            )
                        }
                        // 新增：当前排名提示
                        Text(
                            text = stringResource(
                                R.string.current_rank_format,
                                lowestSection ?: "--"
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE0F7FA))
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                        )
                        LazyColumn(Modifier.fillMaxSize()) {
                            items((uiState as UiState.Success).data) { item ->
                                IntelliApplyCard(
                                    item,
                                    isChinese = isCh,
                                    isFavorite = favs.contains(item.basicInfo.uid),
                                    onToggleFavorite = { viewModel.toggleFavorite(item.basicInfo) },
                                    onCardClick = null,
                                    lowestSection = lowestSection
                                )
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    Text(
                        stringResource(R.string.loading_failure),
                        Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}
