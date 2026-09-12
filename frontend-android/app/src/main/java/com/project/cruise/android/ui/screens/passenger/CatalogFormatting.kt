//package com.project.cruise.android.ui.screens.passenger
//
//import java.net.URI
//
//// Only remote HTTP(S) images; never let server data open local files/content URIs.
//internal fun catalogImageUrl(value: String?): String? {
//    val url = value?.trim()?.takeIf { it.isNotEmpty() } ?: return null
//    return runCatching {
//        val uri = URI(url)
//        url.takeIf { uri.scheme in setOf("http", "https") && !uri.host.isNullOrBlank() && uri.userInfo == null }
//    }.getOrNull()
//}
//
//internal fun catalogDuration(value: Int?): String =
//    value?.takeIf { it > 0 }?.let { "$it phút" } ?: "Đang cập nhật"
//
//internal fun catalogStatus(value: String?): String =
//    if (value == "OUT_OF_STOCK") "Hết hàng (theo lần đồng bộ)" else activityStatus(value)
