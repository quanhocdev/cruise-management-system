package com.project.cruise.android.data.repository

import com.google.gson.JsonParser

internal object PosCommandErrors {
    fun message(body: String?): String? {
        val code = runCatching { JsonParser().parse(body).asJsonObject.get("code")?.asString }.getOrNull()
        return when (code) {
            "CREDIT_LIMIT_NOT_CONFIGURED" -> "Chưa cấu hình hạn mức chi tiêu. Vui lòng liên hệ bộ phận tài chính."
            "CREDIT_LIMIT_EXCEEDED" -> "Khoản phí vượt hạn mức chi tiêu còn lại. Vui lòng liên hệ bộ phận tài chính."
            else -> null
        }
    }
}
