// MainActivity.kt
package com.intelli.zhiyuan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.intelli.zhiyuan.data.AuthRepository
import com.intelli.zhiyuan.data.local.DetailedInfoDatabase
import com.intelli.zhiyuan.data.local.ProvinceDataDatabase
import com.intelli.zhiyuan.data.local.SpecialDataDatabase
import com.intelli.zhiyuan.data.local.SpecialGroupDataDatabase
import com.intelli.zhiyuan.data.local.ranking.RankingDatabase
import com.intelli.zhiyuan.data.local.specialsinfo.SpecialsInfoDatabase
import com.intelli.zhiyuan.data.repository.FavoriteRepository
import com.intelli.zhiyuan.network.NetworkModule
import com.intelli.zhiyuan.ui.navigation.ZhiyuanApp
import com.intelli.zhiyuan.ui.theme.IntelliZhiyuanTheme
import com.intelli.zhiyuan.ui.util.applySavedLocale
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    lateinit var favoriteRepository: FavoriteRepository

    private lateinit var authRepository: AuthRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        applySavedLocale(this@MainActivity)
        // 1. 初始化 AuthRepository 并保证注册
        authRepository = AuthRepository(this, NetworkModule.provideAuthApi(this))
        runBlocking { authRepository.ensureRegistered() }

//        // 2. 初始化 FavoriteRepository
//        favoriteRepository = FavoriteRepository(
//            context = this)

        RankingDatabase.getDatabase(this)
        DetailedInfoDatabase.getDatabase(this)
        SpecialGroupDataDatabase.getDatabase(this)
        ProvinceDataDatabase.getDatabase(this)
        SpecialDataDatabase.getDatabase(this)
        SpecialsInfoDatabase.getDatabase(this)   // 如果有专门的 specials.db
        setContent {
            IntelliZhiyuanTheme {
                ZhiyuanApp()
            }
        }

    }
}
// 预览主界面
@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    ZhiyuanApp()
}

