package com.project.cruise.android.data.network.core

import com.project.cruise.android.BuildConfig

object AppConfig {

    private const val PORT = 8080

    val HTTP_BASE_URL: String
        get() = "http://${BuildConfig.BASE_IP}:$PORT"
}