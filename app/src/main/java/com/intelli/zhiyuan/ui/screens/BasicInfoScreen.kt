// com/intelli/zhiyuan/ui/screens/BasicInfoScreen.kt
package com.intelli.zhiyuan.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.intelli.zhiyuan.R
import com.intelli.zhiyuan.ui.components.BasicInfoCard
import com.intelli.zhiyuan.ui.viewmodel.BasicInfoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.intelli.zhiyuan.ui.components.UiState

// BasicInfoScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicInfoScreen(
    navController: NavController,
    viewModel: BasicInfoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val showOnlyFavs by viewModel.showOnlyFavorites.collectAsState()
    val isCh by viewModel.isChinese.collectAsState()
    val favoriteUids by viewModel.favoriteUids.collectAsState(initial = emptySet())
    val showOnlyRecs by viewModel.showOnlyRecommended.collectAsState()

    var showSearch by remember { mutableStateOf(false) }
    var textField by remember { mutableStateOf(TextFieldValue(searchQuery)) }

    when (uiState) {
        is UiState.Loading -> {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is UiState.Success -> {
            val list = (uiState as UiState.Success).data
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = stringResource(id = R.string.score_query)) },
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
                                    tint = if (showOnlyFavs) Color.Yellow else Color.White
                                )
                            }
                            // 推荐按钮（灯泡）
                            IconButton(onClick = {
                                viewModel.toggleShowOnlyRecommended()
                                if (showOnlyRecs.not()) viewModel.fetchRecommendations()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ThumbUp,
                                    contentDescription = stringResource(R.string.recommend),
                                    tint = if (showOnlyRecs) Color.Yellow else Color.White
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
                Column(Modifier.padding(padding).fillMaxSize()) {
                    if (showSearch) {
                        OutlinedTextField(
                            value = textField,
                            onValueChange = {
                                textField = it
                                viewModel.updateSearchQuery(it.text)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            placeholder = { Text(stringResource(R.string.search_placeholder)) }
                        )
                    }
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(list) { item ->
                            BasicInfoCard(
                                displayItem = item,
                                isChinese   = isCh,
                                isFavorite  = favoriteUids.contains(item.basicInfo.uid),
                                onToggleFavorite = { viewModel.toggleFavorite(item.basicInfo) },
                                onCardClick      = { navController.navigate("detail/${item.basicInfo.uid}") }
                            )
                        }
                    }
                }
            }
        }
        is UiState.Error -> {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text(stringResource(R.string.loading_failure))
            }
        }
    }
}