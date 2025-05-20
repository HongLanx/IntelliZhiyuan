// NetworkModule.kt
package com.intelli.zhiyuan.network

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.intelli.zhiyuan.data.UserPreferences
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.http.*


/** 1. 注册接口：客户端首次上报 userId，服务器返回 token **/
interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse
}
@Serializable
data class RegisterRequest(val userId: String)
@Serializable
data class RegisterResponse(val token: String)

/** 2. 收藏接口（不变） **/
interface FavoriteApi {
    @POST("users/{userId}/favorites")
    suspend fun addFavorite(
        @Path("userId") userId: String,
        @Body body: UidRequest
    )
    @DELETE("users/{userId}/favorites/{uid}")
    suspend fun removeFavorite(
        @Path("userId") userId: String,
        @Path("uid") uid: String
    )
    @GET("users/{userId}/favorites")
    suspend fun listFavorites(@Path("userId") userId: String): List<String>

    @GET("users/{userId}/recommend")
    suspend fun recommend(
        @Path("userId") userId: String
    ): List<String>
}

@Serializable
data class UidRequest(val uid: String)

object NetworkModule {
    private val json = Json { ignoreUnknownKeys = true }

    /** 换成自己服务器的IP和端口 **/
    private const val BASE = "http://127.0.0.1:8080/" 

    private fun provideOkHttp(context: Context) = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val req = chain.request()
            val token = runBlocking { UserPreferences(context).getToken() }
            val builder = req.newBuilder()
            if (!token.isNullOrBlank()) {
                builder.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(builder.build())
        })
        .build()

    fun provideAuthApi(context: Context): AuthApi {
        return Retrofit.Builder()
            .baseUrl(BASE)
            .client(provideOkHttp(context))
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
            .create(AuthApi::class.java)
    }

    fun provideFavoriteApi(context: Context): FavoriteApi {
        return Retrofit.Builder()
            .baseUrl(BASE)
            .client(provideOkHttp(context))
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
            .create(FavoriteApi::class.java)
    }
}
