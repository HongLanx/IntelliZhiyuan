package com.intelli.zhiyuan.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.intelli.zhiyuan.R
import com.intelli.zhiyuan.data.model.DetailedInfo
import com.intelli.zhiyuan.data.model.ProvinceDataEntity
import com.intelli.zhiyuan.data.model.SpecialDataEntity
import com.intelli.zhiyuan.ui.components.DropdownMenuWithLabel
import com.intelli.zhiyuan.ui.components.TopBar
import com.intelli.zhiyuan.ui.components.UiState
import com.intelli.zhiyuan.ui.viewmodel.DetailInfoViewModel
import com.intelli.zhiyuan.ui.viewmodel.SpecialGroupItem
import com.intelli.zhiyuan.util.loadUniversityLogo
import kotlinx.coroutines.launch

//private enum class MainTab {
//    SpecialFeature, HistoryScores, MajorScores
//}
private enum class MainTab(@StringRes val titleRes: Int) {
    SpecialFeature(R.string.tab_special_feature),
    HistoryScores(R.string.tab_history_scores),
    MajorScores(R.string.tab_major_scores)
}

@Composable
fun DetailInfoScreen(
    viewModel: DetailInfoViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    when (uiState) {
        is UiState.Loading -> {
            // 全屏居中加载圈
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is UiState.Success -> {
            ShowUi(viewModel)
        }

        is UiState.Error -> TODO()
    }
}



@OptIn(ExperimentalPagerApi::class)
@Composable
private fun ShowUi(viewModel: DetailInfoViewModel) {
    val info by viewModel.detailedInfo.collectAsState()
    val evalList by viewModel.evaluationList.collectAsState()
    val natList by viewModel.nationalFeatured.collectAsState()
    val provList by viewModel.provincialFeatured.collectAsState()
    val firstList by viewModel.firstClass.collectAsState()
    val isChinese by viewModel.isChinese.collectAsState()
    val uriHandler = LocalUriHandler.current

    var showSGDialog by remember { mutableStateOf(false) }
    var currentGroupId by remember { mutableStateOf("") }

    // pager state
    val pagerState = rememberPagerState(initialPage = 1)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // —— 顶部信息展示区 ——
            info?.let { d ->
                InfoHeader(d, isChinese, uriHandler)
            }

            // —— TabRow ——
            val tabs = MainTab.entries.toTypedArray()
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        text = { Text(stringResource(tab.titleRes)) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(index) }
                        }
                    )
                }
            }

            // —— 内容区 ——
            HorizontalPager(
                count = tabs.size,
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (tabs[page]) {
                    MainTab.SpecialFeature -> SpecialFeatureContent(
                        evalList, natList, provList, firstList
                    )
                    MainTab.HistoryScores -> HistoryScoresContent(
                        viewModel = viewModel,
                        onGroupClick = { sgId ->
                            currentGroupId = sgId
                            viewModel.loadSpecialGroupItems(sgId)
                            showSGDialog = true
                        }
                    )
                    MainTab.MajorScores -> MajorScoresContent(viewModel = viewModel)
                }
            }

            // —— Special Group 弹窗 ——
            if (showSGDialog) {
                SpecialGroupDialog(
                    items = viewModel.specialGroupItems.collectAsState().value,
                    onDismiss = { showSGDialog = false }
                )
            }
        }
    }
}

@Composable
private fun InfoHeader(
    d: DetailedInfo,
    isChinese: Boolean,
    uriHandler: androidx.compose.ui.platform.UriHandler
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isChinese) d.chineseName else d.englishName,
                style = MaterialTheme.typography.headlineSmall
            )
            val loc = if (isChinese) d.chineseLocation else d.englishLocation
            val extra = when {
                d.is985 -> "·985"
                d.is211 -> "·211"
                else    -> ""
            }
            Text(text = "$loc $extra", style = MaterialTheme.typography.bodyMedium)
            d.schoolWebsite
                ?.takeIf { it.isNotBlank() }
                ?.let { url ->
                    Text(
                        text = url,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clickable { uriHandler.openUri(url) },
                        color = Color.Blue
                    )
                }
        }
        AsyncImage(
            model = if (d.hasLogo) loadUniversityLogo(d.uid) else loadUniversityLogo("default"),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
private fun SpecialFeatureContent(
    evalList: List<Pair<String, String>>,
    natList: List<String>,
    provList: List<String>,
    firstList: List<String>
) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            SectionTableTwoColumn(
                header = stringResource(R.string.evaluation_table),
                rows = evalList
            )
        }
        item {
            SectionTableSingleColumn(
                header = stringResource(R.string.national_featured),
                rows = natList
            )
        }
        item {
            SectionTableSingleColumn(
                header = stringResource(R.string.provincial_featured),
                rows = provList
            )
        }
        item {
            SectionTableSingleColumn(
                header = stringResource(R.string.first_class),
                rows = firstList
            )
        }
    }
}

@Composable
private fun SectionTableTwoColumn(
    header: String,
    rows: List<Pair<String, String>>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        // 表头
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(header, style = MaterialTheme.typography.titleMedium)
        }
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)

        if (rows.isEmpty()) {
            // 空表时显示一行“无”
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.none),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
        } else {
            // 内容行
            rows.forEach { (name, rank) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = name,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = rank,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Composable
private fun SectionTableSingleColumn(
    header: String,
    rows: List<String>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        // 表头
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(header, style = MaterialTheme.typography.titleMedium)
        }
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)

        if (rows.isEmpty()) {
            // 空表时显示一行“无”
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.none),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
        } else {
            // 内容行
            rows.forEach { item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Composable
private fun HistoryScoresContent(viewModel: DetailInfoViewModel,
                                 onGroupClick: (String) -> Unit) {
    val provinces by viewModel.historyProvinceOptions.collectAsState()
    val years by viewModel.historyYearOptions.collectAsState()
    val types by viewModel.historyTypeOptions.collectAsState()
    val data by viewModel.historyData.collectAsState()
    val isChinese by viewModel.isChinese.collectAsState()
    val selectedHistoryProvince by viewModel.selectedHistoryProvince.collectAsState()
    val selectedHistoryYear by viewModel.selectedHistoryYear.collectAsState()
    val selectedHistoryType by viewModel.selectedHistoryType.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 1. 省份下拉
        DropdownMenuWithLabel(
            label = stringResource(R.string.province),
            options = provinces.map { if (isChinese) it.chName else it.enName },
            selectedOption = provinces
                .firstOrNull { it.provinceId == viewModel.selectedHistoryProvince.value?.provinceId }
                ?.let { if (isChinese) it.chName else it.enName }
                ?: "",
            onOptionSelected = { idx ->
                viewModel.selectHistoryProvince(provinces[idx].provinceId)
            }
        )
        Spacer(modifier = Modifier.height(8.dp))

        // 2. 年份下拉
        if (selectedHistoryProvince != null) {
            DropdownMenuWithLabel(
                label = stringResource(R.string.year),
                options = years,
                selectedOption = viewModel.selectedHistoryYear.value ?: "",
                onOptionSelected = { idx ->
                    viewModel.selectHistoryYear(years[idx])
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 3. 类型下拉
        if (selectedHistoryYear != null) {
            DropdownMenuWithLabel(
                label = stringResource(R.string.type),
                options = types.map { if (isChinese) it.chName else it.enName },
                selectedOption = types
                    .firstOrNull { it.id == viewModel.selectedHistoryType.value?.id }
                    ?.let { if (isChinese) it.chName else it.enName }
                    ?: "",
                onOptionSelected = { idx ->
                    viewModel.selectHistoryType(types[idx].id)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 4. 数据表
        if (selectedHistoryType != null) {
            HistoryScoresTable(
                data = data,
                isChinese = isChinese,
                viewModel = viewModel,
                onGroupClick = onGroupClick
            )
        }
    }
}

@Composable
private fun HistoryScoresTable(
    data: List<ProvinceDataEntity>,
    isChinese: Boolean,
    viewModel: DetailInfoViewModel,
    onGroupClick: (String) -> Unit
) {
    val showSG = data.any { it.sgName.isNotBlank() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        // 表头
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                stringResource(R.string.batch),
                Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                stringResource(R.string.enrollment_type),
                Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                stringResource(R.string.min_section),
                Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            if (showSG) {
                Text(
                    stringResource(R.string.special_group),
                    Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                stringResource(R.string.filter_requirement),
                Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)

        if (data.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.none), textAlign = TextAlign.Center)
            }
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
        } else {
            data.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp)
                ) {
                    Text(
                        viewModel.getBatchName(row.batchId, isChinese),
                        Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        viewModel.getEnrollmentTypeName(row.enrollmentTypeId, isChinese),
                        Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "${row.min}/${row.minSection}",
                        Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    if (showSG) {
                        Text(
                            text = row.sgName.ifBlank { stringResource(R.string.none) },
                            modifier = Modifier
                                .weight(1f)
                                .clickable(enabled = row.specialGroup != "0" && row.specialGroup.isNotBlank()) {
                                    onGroupClick(row.specialGroup)
                                },
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Blue
                        )
                    }
                    Text(
                        viewModel.getFilterDescription(row.filterId, isChinese),
                        Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Composable
private fun SpecialGroupDialog(
    items: List<SpecialGroupItem>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .heightIn(max = 400.dp) // 限制最大高度
        ) {
            Column {
                // 标题
                Text(
                    text = stringResource(R.string.special_group_details),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp)
                )
                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)

                // 表头
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.special_name),
                        modifier = Modifier
                            .weight(3f)
                            .padding(horizontal = 8.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stringResource(R.string.special_code),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)

                // 内容列表，可滚动
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (items.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.none),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    } else {
                        items(items) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = item.name,
                                    modifier = Modifier
                                        .weight(3f)
                                        .padding(horizontal = 8.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = item.code,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 8.dp),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }

                // 关闭按钮
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.close))
                    }
                }
            }
        }
    }
}

// MajorScoresContent.kt
@Composable
private fun MajorScoresContent(viewModel: DetailInfoViewModel) {
    val isChinese               by viewModel.isChinese.collectAsState()
    val provinces               by viewModel.majorProvinceOptions.collectAsState()
    val years                   by viewModel.majorYearOptions.collectAsState()
    val types                   by viewModel.majorTypeOptions.collectAsState()
    val batches                 by viewModel.majorBatchOptions.collectAsState()
    val data                    by viewModel.majorData.collectAsState()
    val selProv                 by viewModel.selectedMajorProvince.collectAsState()
    val selYear                 by viewModel.selectedMajorYear.collectAsState()
    val selType                 by viewModel.selectedMajorType.collectAsState()
    val selBatch                by viewModel.selectedMajorBatch.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 1. 省份
        DropdownMenuWithLabel(
            label = stringResource(R.string.province),
            options = provinces.map { if (isChinese) it.chName else it.enName },
            selectedOption = provinces
                .firstOrNull { it.provinceId == viewModel.selectedMajorProvince.value?.provinceId }
                ?.let { if (isChinese) it.chName else it.enName }
                ?: "",
            onOptionSelected = { idx ->
                viewModel.selectMajorProvince(provinces[idx].provinceId)
            }
        )
        Spacer(Modifier.height(8.dp))
        // 2. 年份
        if (selProv != null) {
            DropdownMenuWithLabel(
                label = stringResource(R.string.year),
                options = years,
                selectedOption = viewModel.selectedMajorYear.value ?: "",
                onOptionSelected = { idx ->
                    viewModel.selectMajorYear(years[idx])
                }
            )
            Spacer(Modifier.height(8.dp))
        }
        // 3. 类型
        if (selYear != null) {
            DropdownMenuWithLabel(
                label = stringResource(R.string.type),
                options = types.map { if (isChinese) it.chName else it.enName },
                selectedOption = types
                    .firstOrNull { it.id == viewModel.selectedMajorType.value?.id }
                    ?.let { if (isChinese) it.chName else it.enName }
                    ?: "",
                onOptionSelected = { idx ->
                    viewModel.selectMajorType(types[idx].id)
                }
            )
            Spacer(Modifier.height(8.dp))
        }
        // 4. 批次
        if (selType != null) {
            DropdownMenuWithLabel(
                label = stringResource(R.string.batch),
                options = batches.map { if (isChinese) it.chName else it.enName },
                selectedOption = batches
                    .firstOrNull { it.id == viewModel.selectedMajorBatch.value?.id }
                    ?.let { if (isChinese) it.chName else it.enName }
                    ?: "",
                onOptionSelected = { idx ->
                    viewModel.selectMajorBatch(batches[idx].id)
                }
            )
            Spacer(Modifier.height(16.dp))
        }
        // 5. 表格
        if (selBatch != null) {
            MajorScoresTable(data = data, isChinese = isChinese, viewModel = viewModel)
        }
    }
}

@Composable
private fun MajorScoresTable(
    data: List<SpecialDataEntity>,
    isChinese: Boolean,
    viewModel: DetailInfoViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        // 表头
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(stringResource(R.string.special_name), Modifier.weight(1f), textAlign = TextAlign.Center)
            Text(stringResource(R.string.min_section),   Modifier.weight(1f), textAlign = TextAlign.Center)
            Text(stringResource(R.string.filter_requirement),
                Modifier.weight(1f), textAlign = TextAlign.Center)
        }
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)

        if (data.isEmpty()) {
            Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(12.dp),
                contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.none), textAlign = TextAlign.Center)
            }
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
        } else {
            data.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp)
                ) {
                    Text(
                        viewModel.getSpecialName(row.specialId.toString(), isChinese),
                        Modifier.weight(1f), textAlign = TextAlign.Center
                    )
                    Text(
                        "${row.min}/${row.minSection}",
                        Modifier.weight(1f), textAlign = TextAlign.Center
                    )
                    Text(
                        viewModel.getFilterDescription(row.filterId?:0, isChinese),
                        Modifier.weight(1f), textAlign = TextAlign.Center
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}


