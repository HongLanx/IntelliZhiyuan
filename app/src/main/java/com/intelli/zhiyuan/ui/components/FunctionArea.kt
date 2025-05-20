package com.intelli.zhiyuan.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intelli.zhiyuan.R


data class FunctionItem(val name: String, val icon: ImageVector, val onClick: () -> Unit)

@Composable
fun FunctionArea(
    onNavigateToBasicInfo:() -> Unit= {},
    onNavigateToRankQuery: () -> Unit = {},
    onNavigateToSpecialsInfo: () -> Unit = {},
    onNavigateToBasicRecommend: () -> Unit = {},
    onNavigateToIntelliApply: () -> Unit ={},
    isGridMode: Boolean) {
    val functions = listOf(
        FunctionItem(stringResource(id = R.string.score_query), Icons.Default.Search, onNavigateToBasicInfo),
        FunctionItem(stringResource(id = R.string.rank_query), Icons.AutoMirrored.Filled.List,onNavigateToRankQuery),
        FunctionItem(stringResource(id = R.string.specials_info), Icons.Default.Info,onNavigateToSpecialsInfo) ,
        FunctionItem(stringResource(id = R.string.school_recommendation), Icons.Default.Favorite,onNavigateToBasicRecommend),
        FunctionItem(stringResource(id = R.string.intelligent_application), Icons.Default.Check,onNavigateToIntelliApply),
        )

    if (!isGridMode) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(functions) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { item.onClick() },
                    border = BorderStroke(1.dp, Color.LightGray),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.name,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = item.name, fontSize = 20.sp)
                    }
                }
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(functions) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { item.onClick() }
                        .padding(8.dp),
                    border = BorderStroke(1.dp, Color.LightGray),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.name,
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = item.name, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}