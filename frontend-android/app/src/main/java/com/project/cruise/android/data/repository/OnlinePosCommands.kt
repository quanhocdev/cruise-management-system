package com.project.cruise.android.data.repository

import android.content.Context
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.project.cruise.android.data.auth.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class OnlinePosCommands(context: Context) {
    private val tokenManager = TokenManager(context.applicationContext)
    private val preferences = context.applicationContext.getSharedPreferences("pos_command_keys", Context.MODE_PRIVATE)
    private val keys = PendingCommandKeys(object : PendingCommandKeys.Storage {
        override fun read(key: String): String? = preferences.getString(key, null)
        override fun write(key: String, value: String) {
            check(preferences.edit().putString(key, value).commit()) { "Không thể lưu giao dịch an toàn trên thiết bị." }
        }
        override fun remove(key: String) {
            check(preferences.edit().remove(key).commit()) { "Không thể cập nhật trạng thái giao dịch." }
        }
    })

    suspend fun <T> execute(operation: String, payload: Any, send: suspend (String) -> T): T = withContext(Dispatchers.IO) {
        mutex.withLock {
            val token = requireNotNull(tokenManager.getAccessToken()) { "Vui lòng đăng nhập lại." }
            val claims = JsonParser().parse(String(Base64.decode(token.split('.')[1], Base64.URL_SAFE), Charsets.UTF_8)).asJsonObject
            val actor = (claims.get("userId") ?: claims.get("sub"))?.asString
                ?: error("Không xác định được tài khoản đăng nhập.")
            val requestId = keys.begin(actor, operation, Gson().toJson(payload))
            try {
                val result = send(requestId)
                keys.complete(actor, operation)
                result
            } catch (ex: HttpException) {
                // Validation/auth failures have no committed command; ambiguous failures keep the key.
                if (ex.code() in setOf(400, 401, 403, 404, 422)) keys.complete(actor, operation)
                throw ex
            }
        }
    }

    companion object { private val mutex = Mutex() }
}
