package com.project.cruise.android.data.auth

import android.content.Context

class TokenManager(context: Context) {

    private val preferences =
        context.getSharedPreferences(
            "auth_preferences",
            Context.MODE_PRIVATE
        )

    fun saveTokens(
        accessToken: String,
        refreshToken: String
    ) {
        preferences.edit()
            .putString("access_token", accessToken)
            .putString("refresh_token", refreshToken)
            .apply()
    }

    fun getAccessToken(): String? {
        return preferences.getString("access_token", null)
    }

    fun getRefreshToken(): String? {
        return preferences.getString("refresh_token", null)
    }

    fun saveAccessToken(accessToken: String) {
        preferences.edit()
            .putString("access_token", accessToken)
            .apply()
    }

    fun clearTokens() {
        preferences.edit()
            .remove("access_token")
            .remove("refresh_token")
            .apply()
    }
}