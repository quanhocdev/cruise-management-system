package com.project.cruise.android.data.local.pos

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pos_transactions")
data class PosTransactionEntity(
    @PrimaryKey val localId: String,
    val terminalCode: String,
    val scanType: String,
    val scannedValue: String,
    val status: String = PosSyncStatus.PENDING_SYNC.name,
    val attemptCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastError: String? = null
)

enum class PosSyncStatus {
    PENDING_SYNC,
    SYNCING,
    SYNCED,
    FAILED,
    CANCELLED
}

enum class PosScanType {
    QR,
    NFC
}
