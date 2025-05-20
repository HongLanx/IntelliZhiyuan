package com.intelli.zhiyuan.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.intelli.zhiyuan.R
import com.intelli.zhiyuan.ui.components.TopBar
import com.intelli.zhiyuan.ui.theme.IntelliZhiyuanTheme
import com.intelli.zhiyuan.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onNavigateToProfileInfo: () -> Unit,
    onNavigateToLanguage: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val selectedTab by viewModel.selectedTab.collectAsState()

    val profileItems = listOf(
        ProfileItemData(stringResource(id = R.string.profile_info), Icons.Default.AccountCircle, onNavigateToProfileInfo),
        ProfileItemData(stringResource(id = R.string.language), Icons.Default.LocationOn, onNavigateToLanguage)
    )

    Scaffold(
        topBar = { TopBar() }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(profileItems) { item ->
                ProfileItem(item)
            }
        }
    }
}

data class ProfileItemData(val name: String, val icon: ImageVector, val onClick: () -> Unit)

@Composable
fun ProfileItem(item: ProfileItemData) {
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
            Icon(imageVector = item.icon, contentDescription = item.name, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = item.name, fontSize = 20.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    IntelliZhiyuanTheme {
        ProfileScreen({},onNavigateToLanguage = {})
    }
}
