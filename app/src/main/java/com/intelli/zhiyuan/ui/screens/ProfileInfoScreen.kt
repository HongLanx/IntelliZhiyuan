package com.intelli.zhiyuan.ui.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.intelli.zhiyuan.ui.viewmodel.ProfileInfoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.intelli.zhiyuan.R
import com.intelli.zhiyuan.data.model.profileinfo.ProfileInfoCard
import com.intelli.zhiyuan.ui.components.DropdownMenuWithLabel
import com.intelli.zhiyuan.ui.components.DropdownMenuWithLabelSubject

const val PISTAG ="PROFILEINFOSCREEN"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileInfoScreen(viewModel: ProfileInfoViewModel = viewModel()) {
    val cards by viewModel.cards.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.profile_info)) },
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            LazyColumn {
                items(cards) { card ->
                    ProfileInfoCardView(
                        card = card,
                        onToggleSelection = { viewModel.toggleCardSelection(card) },
                        onDelete = { viewModel.deleteCard(it) },
                        viewModel = viewModel)
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateProfileDialog(
            onDismiss = { showCreateDialog = false },
            viewModel = viewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfileDialog(onDismiss: () -> Unit, viewModel: ProfileInfoViewModel) {
    val provinceOptions by viewModel.provinceOptions.collectAsState()
    val yearOptions by viewModel.yearOptions.collectAsState()
    val levelOptions by viewModel.levelOptions.collectAsState()
    val isChinese by viewModel.isChinese.collectAsState()

    val selectedProvince = viewModel.selectedProvinceId.collectAsState().value
    val selectedYear = viewModel.selectedYear.collectAsState().value
//    val selectedSubject = viewModel.selectedSubject.collectAsState().value
//    val selectedFirstSubject = viewModel.selectedFirstSubject.collectAsState().value
//    val selectedSecondSubjects by viewModel.selectedSecondSubjects.collectAsState()
    val selectedLevel = viewModel.selectedLevel.collectAsState().value
    val selectedSubjectCode = viewModel.selectedSubjectCode.collectAsState().value
    val selectedSubjectDescription = viewModel.selectedSubjectDescription.collectAsState().value
    val selectedFirstSubjectDescription = viewModel.selectedFirstSubjectDescription.collectAsState().value
    val selectedSecondSubjectsDescription = viewModel.selectedSecondSubjectsDescription.collectAsState().value

    val isPreReform = viewModel.isPreReform.collectAsState().value
    val isPostReform3Plus3 = viewModel.isPostReform3Plus3.collectAsState().value
    val isPostReform3Plus3Zhejiang = viewModel.isPostReform3Plus3Zhejiang.collectAsState().value
    val isPostReform3Plus1Plus2 = viewModel.isPostReform3Plus1Plus2.collectAsState().value

    val examScore = viewModel.examScore.collectAsState().value
    var textFieldState by remember { mutableStateOf(TextFieldValue("")) }


    // Subject selection下拉选项（改革前）
    val subjectOptionsPreReform = listOf(
        stringResource(R.string.natural_science),
        stringResource(R.string.social_science))
    // 对于后改革，我们简化为单选（例如针对3+3）或分为两部分（3+1+2）
    val subjectOptionsPostReform3Plus3 = listOf(
        stringResource(R.string.physics),
        stringResource(R.string.chemistry),
        stringResource(R.string.biology),
        stringResource(R.string.history),
        stringResource(R.string.politics),
        stringResource(R.string.geography))
    val subjectOptionsPostReform3Plus3Zhejiang = subjectOptionsPostReform3Plus3 + stringResource(R.string.technology)
    val subjectOptionsPostReform3Plus1Radio = listOf(
        stringResource(R.string.physics),
        stringResource(R.string.history))
    val subjectOptionsPostReform3Plus1Multi = listOf(
        stringResource(R.string.chemistry),
        stringResource(R.string.biology),
        stringResource(R.string.geography),
        stringResource(R.string.politics))
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.create_profile)) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // 省份下拉菜单
                DropdownMenuWithLabel(
                    label = stringResource(id = R.string.province),
                    options = provinceOptions.map { if (isChinese) it.ch_name else it.en_name },
                    selectedOption = provinceOptions.find { it.province_id == selectedProvince }?.let { if (isChinese) it.ch_name else it.en_name } ?: "",
                    onOptionSelected = { index ->
                        val province = provinceOptions.getOrNull(index) ?: return@DropdownMenuWithLabel
                        viewModel.selectProvince(province.province_id)
                    }
                )
                // 年份下拉菜单
                if (selectedProvince != null) {
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
                // 选科下拉菜单
                if (selectedProvince != null && selectedYear != null) {
                    // 根据年份判断模式，简化：若年份转换为 Int 小于 2017，使用 pre-reform；否则后改革
                    if (isPreReform) {
                        DropdownMenuWithLabel(
                            label = stringResource(id = R.string.subject),
                            options = subjectOptionsPreReform,
                            selectedOption = selectedSubjectDescription,
                            onOptionSelected = { index ->
                                viewModel.selectSubjectPreRefrom(index == (0 ?: false))
                            }
                        )
                    } else {
                        // 后改革，根据省份进一步判断模式（这里简化：如果省份为"浙江"，采用3+3模式；否则3+1+2模式）
//                        val provinceName = provinceOptions.find { it.province_id == selectedProvince }?.ch_name ?: ""
//                        val mode = if (provinceName == "浙江") "3+3" else "3+1+2"
                        if (isPostReform3Plus3) {
                            DropdownMenuWithLabelSubject(
                                label = stringResource(id = R.string.subject),
                                options = subjectOptionsPostReform3Plus3,
                                selectedOption = selectedSubjectDescription,
                                onOptionSelected = { index ->
                                    viewModel.toggleSubject(index)
                                },
                                true,
                                selectedSubjectCode = selectedSubjectCode

                            )
                        }
                        else if (isPostReform3Plus3Zhejiang){
                            DropdownMenuWithLabelSubject(
                                label = stringResource(id = R.string.subject),
                                options = subjectOptionsPostReform3Plus3Zhejiang,
                                selectedOption = selectedSubjectDescription,
                                onOptionSelected = { index ->
                                    viewModel.toggleSubject(index)
                                },
                                true,
                                selectedSubjectCode = selectedSubjectCode
                            )
                        }
                        else {
                            DropdownMenuWithLabel(
                                label = stringResource(id = R.string.first_subject),
                                options = subjectOptionsPostReform3Plus1Radio,
                                selectedOption = selectedFirstSubjectDescription,
                                onOptionSelected = { index ->
                                    viewModel.selectFirstSubject(index)
                                }
                            )
                            DropdownMenuWithLabelSubject(
                                label = stringResource(id = R.string.second_subject),
                                options = subjectOptionsPostReform3Plus1Multi,
                                selectedOption = selectedSecondSubjectsDescription,
                                onOptionSelected = { index ->
                                    viewModel.toggleSecondSubject(index)
                                },
                                keepMenuAfterSelection = true,
                                selectedSubjectCode = String(
                                    charArrayOf(
                                        selectedSubjectCode[1],
                                        selectedSubjectCode[2],
                                        selectedSubjectCode[5],
                                        selectedSubjectCode[4]
                                    )
                                )
                            )
                        }
                    }
                }
                // 层次下拉菜单
                if (selectedProvince != null && selectedYear != null && selectedSubjectCode.toCharArray().count { it == '1' } == 3) {
                    if (levelOptions.size!=1){
                        DropdownMenuWithLabel(
                            label = stringResource(id = R.string.level),
                            options = levelOptions.map { if (isChinese) it.ch_name else it.en_name },
                            selectedOption = levelOptions.find { it.level.toString() == selectedLevel }?.let { if (isChinese) it.ch_name else it.en_name } ?: "",
                            onOptionSelected = { index ->
                                val level = levelOptions.getOrNull(index) ?: return@DropdownMenuWithLabel
                                viewModel.selectLevel(level)
                            }
                        )
                    }
                    else
                    {
                        val level = levelOptions.first()
                        viewModel.selectLevel(level)
                    }
                }
                if (selectedProvince != null && selectedYear != null && selectedSubjectCode.toCharArray().count { it == '1' } == 3 && selectedLevel != null)
                {
                    OutlinedTextField(
                        value = textFieldState,
                        onValueChange = {
                            textFieldState = it
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
//                            .padding(8.dp),
                        placeholder = { Text(text = stringResource(id = R.string.exam_score)) }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                viewModel.setScore(textFieldState)
                viewModel.createNewCard()
                onDismiss()
            }) {
                Text(text = stringResource(id = R.string.save))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileInfoCardView(card: ProfileInfoCard,
                        onToggleSelection: () -> Unit,
                        onDelete: (ProfileInfoCard) -> Unit,
                        viewModel: ProfileInfoViewModel) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(card.provinceId, viewModel.isChinese) {
        viewModel.loadProvinceName(card.provinceId)
    }
    LaunchedEffect(card.level, viewModel.isChinese) {
        viewModel.loadLevelName(card.level)
    }

    // 从 StateFlow 里拿值
    val provinceMap by viewModel.provinceNames.collectAsState()
    val levelMap by viewModel.levelNames.collectAsState()

    val provinceName = provinceMap[card.provinceId] ?: ""
    val levelName = levelMap[card.level] ?: ""

    val description = viewModel.transferSubjectCodeToDescription(card.subjectSelectionCode, examMode = card.examMode)



    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
//            .clickable { /* 处理点击事件 */ }
            .combinedClickable(
                onClick = { /* 处理正常点击 */ },
                onLongClick = { showDeleteDialog = true }
            ),

        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Row {
                    Text(text = "${stringResource(id = R.string.province)}: $provinceName", modifier = Modifier.weight(1f))
                    Text(text = "${stringResource(id = R.string.year)}: ${card.examYear}", modifier = Modifier.weight(1f))
                    Text(text = "${stringResource(id = R.string.exam_score)}: ${card.examScore}", modifier = Modifier.weight(1f))
                }
                Row {
                    Text(text = "${stringResource(id = R.string.subject)}: $description", modifier = Modifier.weight(1f))
                    Text(text = "${stringResource(id = R.string.level)}: $levelName", modifier = Modifier.weight(1f))
                }
                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = { Text(stringResource(R.string.delete_card)) },
                        text = { Text(stringResource(R.string.confirm_delete_card)) },
                        confirmButton = {
                            Button(onClick = {
                                onDelete(card)
                                showDeleteDialog = false
                            }) {
                                Text(stringResource(R.string.delete))
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDeleteDialog = false }) {
                                Text(stringResource(R.string.cancel))
                            }
                        }
                    )
                }
            }
            IconButton(onClick = onToggleSelection) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = stringResource(R.string.select_card),
                    tint = if (card.selected) Color.Green else Color.Gray
                )
            }
        }
    }
}
