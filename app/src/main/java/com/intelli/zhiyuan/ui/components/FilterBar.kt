package com.intelli.zhiyuan.ui.components

import SpecialsInfoViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.intelli.zhiyuan.R

@Composable
fun FilterBar(viewModel: SpecialsInfoViewModel,isChinese:Boolean) {
    // 这里仅用示例数据，实际应从数据库获取各级选项
    val levelOnes by viewModel.levelOneList.collectAsState()


    val levelTwos by viewModel.levelTwoList.collectAsState()


    val levelThrees by viewModel.levelThreeList.collectAsState()


    val selectedLevelOne by viewModel.selectedLevelOne.collectAsState()
    val selectedLevelTwo by viewModel.selectedLevelTwo.collectAsState()
    val selectedLevelThree by viewModel.selectedLevelThree.collectAsState()



    Column {
        DropdownMenuWithLabel(
            label = stringResource(R.string.level_one),
            options = levelOnes.map { if (isChinese) it.name_ch else it.name_en },
            selectedOption = levelOnes.find { it.level_id == selectedLevelOne }
                ?.let { if (isChinese) it.name_ch else it.name_en } ?: "",
            onOptionSelected = { index ->
                val levelOne = levelOnes.getOrNull(index) ?: return@DropdownMenuWithLabel
                viewModel.selectLevelOne(levelOne.level_id) }
        )
        if (viewModel.selectedLevelOne.collectAsState(initial = null).value != null) {
            DropdownMenuWithLabel(
                label = stringResource(R.string.level_two),
                options = levelTwos.map { if (isChinese) it.name_ch else it.name_en },
                selectedOption = levelTwos.find { it.level_id == selectedLevelTwo }
                    ?.let { if (isChinese) it.name_ch else it.name_en } ?: "",
                onOptionSelected = {  index ->
                    val levelTwo = levelTwos.getOrNull(index) ?: return@DropdownMenuWithLabel
                    viewModel.selectLevelTwo(levelTwo.level_id) }
            )
        }
        if (viewModel.selectedLevelTwo.collectAsState(initial = null).value != null) {
            DropdownMenuWithLabel(
                label = stringResource(R.string.level_three),
                options = levelThrees.map { if (isChinese) it.name_ch else it.name_en },
                selectedOption = levelThrees.find { it.level_id == selectedLevelThree }
                    ?.let { if (isChinese) it.name_ch else it.name_en } ?: "",
                onOptionSelected = { index ->
                    val levelThree = levelThrees.getOrNull(index) ?: return@DropdownMenuWithLabel
                    viewModel.selectLevelThree(levelThree.level_id) }
            )
        }
    }
}

//@Composable
//fun FilterRow(title: String, options: List<String>, selectedOption: String?, onOptionSelected: (Int) -> Unit) {
//    Column(modifier = Modifier.padding(8.dp)) {
//        Text(text = title, style = MaterialTheme.typography.titleSmall)
//        Row(
//            modifier = Modifier
//                .fillMaxWidth(),
//            horizontalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            options.forEachIndexed() { index, option ->
//                FilterChip(
//                    selected = option == selectedOption,
//                    onClick = { onOptionSelected(index) },
//                    label = { Text(option) },
//                    modifier = Modifier.background(
//                        if (option == selectedOption) Color.DarkGray else Color.Transparent
//                    )
//
//                )
//            }
//        }
//    }
//}