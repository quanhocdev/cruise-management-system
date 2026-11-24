
package com.project.cruise.android.data.local.pos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PosCommandDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(command: PosCommandEntity)

    @Query("""
        SELECT * FROM pos_commands
        WHERE localId = :localId
        LIMIT 1
    """)
    suspend fun findByLocalId(localId: String): PosCommandEntity?

    @Query("""
        SELECT * FROM pos_commands
        WHERE clientRequestId = :clientRequestId
        LIMIT 1
    """)
    suspend fun findByClientRequestId(clientRequestId: String): PosCommandEntity?

    /**
     * Lấy các lệnh đang chờ đồng bộ.
     */
    @Query("""
        SELECT * FROM pos_commands
        WHERE status = 'PENDING_SYNC'
          AND operation = 'PRODUCT_USAGE'
        ORDER BY createdAt ASC
    """)
    suspend fun findWaitingForSync(): List<PosCommandEntity>

    /**
     * Chuyển PENDING_SYNC sang SYNCING một cách atomic.
     * Trả về số dòng được cập nhật.
     */
    @Query("""
        UPDATE pos_commands
        SET status = 'SYNCING',
            attemptCount = attemptCount + 1,
            updatedAt = :updatedAt,
            lastError = NULL
        WHERE localId = :localId
          AND status = 'PENDING_SYNC'
    """)
    suspend fun claimForSync(
        localId: String,
        updatedAt: Long = System.currentTimeMillis()
    ): Int

    /**
     * Khôi phục các lệnh SYNCING bị bỏ dở do ứng dụng
     * hoặc tiến trình bị dừng đột ngột.
     */
    @Query("""
        UPDATE pos_commands
        SET status = 'PENDING_SYNC',
            updatedAt = :updatedAt,
            lastError = 'Khôi phục lệnh đồng bộ bị gián đoạn'
        WHERE status = 'SYNCING'
          AND operation = 'PRODUCT_USAGE'
    """)
    suspend fun recoverInterruptedCommands(
        updatedAt: Long = System.currentTimeMillis()
    ): Int

    @Query("""
        UPDATE pos_commands
        SET status = 'PENDING_SYNC',
            updatedAt = :updatedAt,
            lastError = :error
        WHERE localId = :localId
    """)
    suspend fun markPending(
        localId: String,
        error: String?,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("""
        UPDATE pos_commands
        SET status = 'CONFIRMED',
            updatedAt = :updatedAt,
            lastError = NULL
        WHERE localId = :localId
    """)
    suspend fun markConfirmed(
        localId: String,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("""
        UPDATE pos_commands
        SET status = 'FAILED',
            updatedAt = :updatedAt,
            lastError = :error
        WHERE localId = :localId
    """)
    suspend fun markFailed(
        localId: String,
        error: String?,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("""
        SELECT COUNT(*) FROM pos_commands
        WHERE status IN ('PENDING_SYNC', 'SYNCING', 'FAILED')
    """)
    fun observePendingCount(): Flow<Int>

    @Query("""
        SELECT * FROM pos_commands
        ORDER BY createdAt DESC
    """)
    fun observeAll(): Flow<List<PosCommandEntity>>


    @Query("""
    UPDATE pos_commands
    SET status = 'PENDING_SYNC',
        updatedAt = :updatedAt,
        lastError = 'Khôi phục lệnh đồng bộ bị gián đoạn'
    WHERE status = 'SYNCING'
      AND operation = 'PRODUCT_USAGE'
      AND updatedAt < :staleBefore
""")
    suspend fun recoverStaleCommands(
        staleBefore: Long,
        updatedAt: Long = System.currentTimeMillis()
    ): Int

}
