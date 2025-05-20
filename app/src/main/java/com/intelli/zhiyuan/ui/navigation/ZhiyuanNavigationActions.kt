// com/intelli/zhiyuan/ui/navigation/ZhiyuanNavigationActions.kt
package com.intelli.zhiyuan.ui.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

class ZhiyuanNavigationActions(navController: NavHostController) {
    val navigateToHome: () -> Unit = {
        navController.navigate(ZhiyuanDestinations.HOME_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToProfile: () -> Unit = {
        navController.navigate(ZhiyuanDestinations.PROFILE_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
//    val navigateToLanguage: () -> Unit = {
//        navController.navigate(ZhiyuanDestinations.LANGUAGE_ROUTE) {
//            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
//            launchSingleTop = true
//            restoreState = true
//        }
//    }
//    val navigateToScoreQuery: () -> Unit = {
//        navController.navigate(ZhiyuanDestinations.SCORE_QUERY_ROUTE) {
//            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
//            launchSingleTop = true
//            restoreState = true
//        }
//    }
//    val navigateToRankQuery: () -> Unit = {
//        navController.navigate(ZhiyuanDestinations.RANK_QUERY_ROUTE) {
//            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
//            launchSingleTop = true
//            restoreState = true
//        }
//    }
}
