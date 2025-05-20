// ZhiyuanApp.kt
package com.intelli.zhiyuan.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.intelli.zhiyuan.ui.components.BottomNav

@Composable
fun ZhiyuanApp() {
    val navController = rememberNavController()
    val navigationActions = ZhiyuanNavigationActions(navController)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ZhiyuanDestinations.HOME_ROUTE
    Scaffold(
        bottomBar = {
            BottomNav(
                selectedTab = currentRoute,
                onTabSelected = { selected ->
                    when (selected) {
                        ZhiyuanDestinations.HOME_ROUTE -> navigationActions.navigateToHome()
                        ZhiyuanDestinations.PROFILE_ROUTE -> navigationActions.navigateToProfile()
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            ZhiyuanNavGraph(navController = navController)
        }
    }
}
