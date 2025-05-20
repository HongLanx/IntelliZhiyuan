package com.intelli.zhiyuan.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.intelli.zhiyuan.R
import com.intelli.zhiyuan.ui.components.DropdownMenuWithLabel
import com.intelli.zhiyuan.ui.viewmodel.RankQueryViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

const val RQSTAG ="RankQueryScreen"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankQueryScreen(viewModel: RankQueryViewModel = viewModel()) {
    val provinceOptions by viewModel.provinceOptions.collectAsState()
    val yearOptions by viewModel.yearOptions.collectAsState()
    val trackOptions by viewModel.trackOptions.collectAsState()
    val levelOptions by viewModel.levelOptions.collectAsState()
    val isChinese by viewModel.isChinese.collectAsState()

    val selectedProvinceId by viewModel.selectedProvinceId.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val selectedTrack by viewModel.selectedTrack.collectAsState()
    val selectedLevel by viewModel.selectedLevel.collectAsState()

    val rankingSegments by viewModel.rankingSegments.collectAsState()
    val resultRankRange by viewModel.resultRankRange.collectAsState()

    val inputExamScore by viewModel.inputExamScore.collectAsState()
//    var inputScore by remember { mutableStateOf(inputExamScore ?: "") }

    val provinceIdSelected = selectedProvinceId != null
    val yearSelected = selectedYear != null
    val trackSelected = selectedTrack != null
    val levelSelected = selectedLevel != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.rank_query))
                        },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Blue,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 下拉菜单：省份
            DropdownMenuWithLabel(
                label = stringResource(id = R.string.province),
                options = provinceOptions.map { if (isChinese) it.ch_name else it.en_name },
                selectedOption = provinceOptions.find { it.province_id == selectedProvinceId }
                    ?.let { if (isChinese) it.ch_name else it.en_name } ?: "",
                onOptionSelected = { index ->
                    val province = provinceOptions.getOrNull(index) ?: return@DropdownMenuWithLabel
                    viewModel.selectProvince(province.province_id)
                }
            )

            // 下拉菜单：年份（省份选完后显示）
            if (provinceIdSelected && yearOptions.size > 1) {
                DropdownMenuWithLabel(
                    label = stringResource(id = R.string.year),
                    options = yearOptions,
                    selectedOption = selectedYear ?: "",
                    onOptionSelected = { index ->
                        val year = yearOptions.getOrNull(index) ?: return@DropdownMenuWithLabel
                        viewModel.selectYear(year)
                    }
                )
            }

            // 下拉菜单：选科（年份选完后显示）
            if (yearSelected && trackOptions.size > 1) {
                DropdownMenuWithLabel(
                    label = stringResource(id = R.string.track),
                    options = trackOptions.map { if (isChinese) it.ch_name else it.en_name },
                    selectedOption = trackOptions.find { it.track == selectedTrack }
                        ?.let { if (isChinese) it.ch_name else it.en_name } ?: "",
                    onOptionSelected = { index ->
                        val track = trackOptions.getOrNull(index) ?: return@DropdownMenuWithLabel
                        viewModel.selectTrack(track)
                    }
                )
            }

            // 下拉菜单：层次（选科选完后显示）
            if (trackSelected && levelOptions.size > 1) {
                DropdownMenuWithLabel(
                    label = stringResource(id = R.string.level),
                    options = levelOptions.map { if (isChinese) it.ch_name else it.en_name },
                    selectedOption = levelOptions.find { it.level.toString() == selectedLevel }
                        ?.let { if (isChinese) it.ch_name else it.en_name } ?: "",
                    onOptionSelected = { index ->
                        val level = levelOptions.getOrNull(index) ?: return@DropdownMenuWithLabel
                        viewModel.selectLevel(level)
                    }
                )
            }

            // 查询栏（所有下拉菜单都选完后显示）
            if (levelSelected) {
                OutlinedTextField(
                    value = inputExamScore?: "",
                    onValueChange = { viewModel.changeInputScore(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = stringResource(id = R.string.input_score)) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                Button(
                    onClick = { viewModel.queryRank() },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.query))
                }
                // 结果栏（仅在查询成功后显示）
                resultRankRange?.let {
                    Text(
                        text = stringResource(id = R.string.result) + ": " + it,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 展示排名段数据（双栏列表），添加表头
            if (rankingSegments.isNotEmpty()) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(id = R.string.score_range),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = stringResource(id = R.string.rank_range),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    LazyColumn {
                        items(rankingSegments) { segment ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = segment.first, modifier = Modifier.weight(1f))
                                Text(text = segment.second, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}
