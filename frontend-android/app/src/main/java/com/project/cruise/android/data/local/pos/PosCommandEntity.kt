package com.project.cruise.android.data.local.pos

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pos_commands",
    indices = [
        Index(value = ["clientRequestId"], unique = true),
        Index(value = ["status", "createdAt"])
    ]
)
data class PosCommandEntity(
    @PrimaryKey
    val localId: String,

    val clientRequestId: String,

    val operation: String,

    // Lưu toàn bộ request JSON để retry đúng nội dung ban đầu.
    val payloadJson: String,

    val status: String = PosCommandStatus.PENDING_SYNC.name,

    val attemptCount: Int = 0,

    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long = System.currentTimeMillis(),

    val lastError: String? = null
)

enum class PosCommandStatus {
    PENDING_SYNC,
    SYNCING,
    CONFIRMED,
    FAILED
}