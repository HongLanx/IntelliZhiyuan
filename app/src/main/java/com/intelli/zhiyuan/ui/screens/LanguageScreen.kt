package com.intelli.zhiyuan.ui.screens

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.intelli.zhiyuan.R
import com.intelli.zhiyuan.ui.components.TopBar
import com.intelli.zhiyuan.ui.theme.IntelliZhiyuanTheme
import com.intelli.zhiyuan.ui.viewmodel.LanguageViewModel

@Composable
fun LanguageScreen(navController: NavController, viewModel: LanguageViewModel = viewModel()) {

    val activity = LocalContext.current as? Activity ?: return
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    Scaffold(topBar = { TopBar() }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.select_language),
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            LanguageOption(
                text = stringResource(id = R.string.chinese),
                isSelected = selectedLanguage == "zh",
                onClick = {
                    viewModel.changeLanguage(activity, "zh")
                    navController.popBackStack()}
            )

            LanguageOption(
                text = stringResource(id = R.string.english),
                isSelected = selectedLanguage == "en",
                onClick = { viewModel.changeLanguage(activity, "en")
                    navController.popBackStack()}
            )
        }
    }
}

@Composable
fun LanguageOption(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLanguageScreen() {
    IntelliZhiyuanTheme {
        LanguageScreen(rememberNavController())
    }
}
