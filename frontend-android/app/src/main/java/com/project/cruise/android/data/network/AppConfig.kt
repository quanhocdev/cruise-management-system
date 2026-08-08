package com.project.cruise.android.data.network

import com.project.cruise.android.BuildConfig

object AppConfig {

    const val PORT = 8080

    val HTTP_BASE_URL: String
        get() = "http://${BuildConfig.BASE_IP}:$PORT"
}