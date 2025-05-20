package com.intelli.zhiyuan.ui.screens

import SpecialsInfoViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.intelli.zhiyuan.R
import com.intelli.zhiyuan.ui.components.FilterBar
import com.intelli.zhiyuan.ui.components.SpecialsInfoCard


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecialsInfoScreen(viewModel: SpecialsInfoViewModel = viewModel()) {
    val searchQuery by viewModel.searchQuery.collectAsState()


    // 初始 specials 列表，这里用 stateIn 初始为空列表
    val specialsList by viewModel.specialListShowed.collectAsState()
    val favoriteSpecialIds by viewModel.favoriteSpecialIds.collectAsState(initial = emptySet())


    val isChinese by viewModel.isChinese.collectAsState()
    var showSearch by remember { mutableStateOf(false) }
    var textFieldState by remember { mutableStateOf(searchQuery) }
    val showOnlyFavorites by viewModel.showOnlyFavorites.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.specials_info)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Blue,
                    titleContentColor = Color.White
                ),
                actions = {
                    // 收藏筛选按钮（可扩展功能）
                    IconButton(onClick = { viewModel.toggleShowOnlyFavorites() }) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = stringResource(R.string.favorite) ,
                            tint = if (showOnlyFavorites) Color.Yellow else Color.White

                        )
                    }
                    // 搜索按钮
                    IconButton(onClick = { showSearch = !showSearch }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = stringResource(R.string.search),
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (showSearch) {
                OutlinedTextField(
                    value = textFieldState,
                    onValueChange = {
                        textFieldState = it
                        viewModel.updateSearchQuery(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    placeholder = { Text(text = stringResource(R.string.search_specials)) }
                )
            }
            // 筛选栏：依次显示 LevelOne、LevelTwo、LevelThree
            FilterBar(viewModel = viewModel,
                isChinese=isChinese)

            // 展示 Specials 卡片
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(specialsList) { special ->
                    val isFavorite = favoriteSpecialIds.contains(special.special_id)

                    SpecialsInfoCard(
                        special = special,
                        isChinese = isChinese,
                        isFavorite=isFavorite,
                        onToggleFavorite = { viewModel.toggleFavorite(special) }
                    )
                }
            }
        }
    }
}

