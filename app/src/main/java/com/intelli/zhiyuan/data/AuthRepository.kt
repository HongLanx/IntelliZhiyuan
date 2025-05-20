// AuthRepository.kt
package com.intelli.zhiyuan.data

import android.content.Context
import android.util.Log
import com.intelli.zhiyuan.network.AuthApi
import com.intelli.zhiyuan.network.RegisterRequest
import com.intelli.zhiyuan.network.RegisterResponse
import retrofit2.HttpException
import java.io.IOException

class AuthRepository(
    private val context: Context,
    private val authApi: AuthApi
) {
    private val prefs = UserPreferences(context)
    private val ARTAG = "AuthRepository"


    /**
     * 确保本地有 token。
     * 如果本地没 token，就尝试注册（上报 userId 获取 token），
     * 任何异常都 catch 掉，保证不抛出。
     */
    suspend fun ensureRegistered() {
        val existing = prefs.getToken()
        if (existing.isNullOrBlank()) {
            val userId = prefs.getOrCreateUserId()
            try {
                val resp: RegisterResponse = authApi.register(RegisterRequest(userId))
                prefs.setToken(resp.token)
                Log.d(ARTAG, "Registered successfully, token saved")
            } catch (e: HttpException) {
                Log.w(ARTAG, "Registration HTTP error (status ${e.code()}), proceeding offline", e)
            } catch (e: IOException) {
                Log.w(ARTAG, "Registration network error, proceeding offline", e)
            } catch (e: Exception) {
                Log.w(ARTAG, "Unexpected registration error, proceeding offline", e)
            }
        } else {
            Log.d(ARTAG, "Token already exists, skipping registration")
        }
    }
}