// ZhiyuanNavGraph.kt
package com.intelli.zhiyuan.ui.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.intelli.zhiyuan.ui.screens.BasicRecommendScreen
import com.intelli.zhiyuan.ui.screens.DetailInfoScreen
import com.intelli.zhiyuan.ui.screens.HomeScreen
import com.intelli.zhiyuan.ui.screens.LanguageScreen
import com.intelli.zhiyuan.ui.screens.ProfileInfoScreen
import com.intelli.zhiyuan.ui.screens.ProfileScreen
import com.intelli.zhiyuan.ui.screens.RankQueryScreen
import com.intelli.zhiyuan.ui.screens.BasicInfoScreen
import com.intelli.zhiyuan.ui.screens.IntelliApplyScreen
import com.intelli.zhiyuan.ui.screens.SpecialsInfoScreen
import com.intelli.zhiyuan.ui.viewmodel.DetailInfoViewModel
import com.intelli.zhiyuan.ui.viewmodel.DetailInfoViewModelFactory

@Composable
fun ZhiyuanNavGraph(
    navController: NavHostController,
    startDestination: String = ZhiyuanDestinations.HOME_ROUTE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(ZhiyuanDestinations.HOME_ROUTE) {
            HomeScreen(
                {navController.navigate(ZhiyuanDestinations.BASIC_INFO_ROUTE)},
                {navController.navigate(ZhiyuanDestinations.RANK_QUERY_ROUTE)},
                {navController.navigate(ZhiyuanDestinations.SPECIALS_INFO_ROUTE)},
                {navController.navigate(ZhiyuanDestinations.BASIC_RECOMMEND_ROUTE)},
                {navController.navigate(ZhiyuanDestinations.INTELLI_APPLY_ROUTE)}
            )
        }
        composable(ZhiyuanDestinations.PROFILE_ROUTE) {
            ProfileScreen(
                onNavigateToProfileInfo = {navController.navigate(ZhiyuanDestinations.PROFILE_INFO_ROUTE)},
                onNavigateToLanguage = { navController.navigate(ZhiyuanDestinations.LANGUAGE_ROUTE) }
            )
        }
        composable(ZhiyuanDestinations.LANGUAGE_ROUTE) {
            LanguageScreen(navController)
        }
        composable(ZhiyuanDestinations.BASIC_INFO_ROUTE) {
            BasicInfoScreen(navController)
        }
        composable(ZhiyuanDestinations.RANK_QUERY_ROUTE) {
            RankQueryScreen()
        }
        composable(ZhiyuanDestinations.PROFILE_INFO_ROUTE) {
            ProfileInfoScreen()
        }
        composable(ZhiyuanDestinations.SPECIALS_INFO_ROUTE) {
            SpecialsInfoScreen()
        }
        composable(ZhiyuanDestinations.BASIC_RECOMMEND_ROUTE) {
            BasicRecommendScreen()
        }
        composable(
            route = "detail/{uid}",
            arguments = listOf(navArgument("uid") { type = NavType.StringType })
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid")!!
            val factory = DetailInfoViewModelFactory(
                application = LocalContext.current.applicationContext as Application,
                uid = uid
            )
            val viewModel: DetailInfoViewModel = viewModel(factory = factory)
            DetailInfoScreen(viewModel = viewModel)
        }
        composable(ZhiyuanDestinations.INTELLI_APPLY_ROUTE) {
            IntelliApplyScreen()
        }


    }
}
