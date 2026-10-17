package com.project.cruise.android.data.repository

import java.security.MessageDigest
import java.util.UUID

/** Persists the request key before sending so a timeout or restart can retry safely. */
class PendingCommandKeys(private val storage: Storage) {
    interface Storage {
        fun read(key: String): String?
        fun write(key: String, value: String)
        fun remove(key: String)
    }

    fun begin(actor: String, operation: String, payload: String): String {
        require(actor.isNotBlank() && operation.isNotBlank())
        val slot = digest("$actor\n$operation")
        val hash = digest(payload)
        val pending = storage.read(slot)
        if (pending != null) {
            val parts = pending.split(':', limit = 2)
            check(parts.size == 2 && parts[0] == hash) {
                "Giao dịch trước chưa rõ kết quả. Hãy thử lại đúng khách, dịch vụ và số lượng trước khi đổi giao dịch."
            }
            return parts[1]
        }
        return UUID.randomUUID().toString().also { storage.write(slot, "$hash:$it") }
    }

    fun complete(actor: String, operation: String) = storage.remove(digest("$actor\n$operation"))

    private fun digest(value: String) = MessageDigest.getInstance("SHA-256")
        .digest(value.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }
}
