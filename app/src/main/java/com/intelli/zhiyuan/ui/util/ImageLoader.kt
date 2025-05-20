package com.intelli.zhiyuan.util

import android.content.Context
import android.util.Log
import java.io.IOException
const val TAG ="ImageLoader"
/**
 * 根据学校 uid 构造 logo 图片路径。
 * 假设 logo 图片存放在 assets/logo 目录下，文件名格式为 logo_{uid}.jpg，
 * 若不存在，则使用 logo_default.jpg。
 */
fun loadUniversityLogo(uid: String): String {
    val logoPath = "logo/logo_$uid.jpg"

    return try {
        // 尝试打开指定 UID 的 logo
        "file:///android_asset/$logoPath"
    } catch (e: IOException) {
        // 找不到时返回默认图片
        Log.e(TAG,"IO Exception when trying to find university logo uid $uid")
        "file:///android_asset/logo/logo_default.jpg"
    }
}