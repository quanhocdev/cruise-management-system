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
    suspend fun findByClientRequestId(
        clientRequestId: String
    ): PosCommandEntity?

    @Query("""
        SELECT * FROM pos_commands
        WHERE status = 'PENDING_SYNC'
        ORDER BY createdAt ASC
    """)
    suspend fun findWaitingForSync(): List<PosCommandEntity>

    @Query("""
        UPDATE pos_commands
        SET status = 'SYNCING',
            attemptCount = attemptCount + 1,
            updatedAt = :updatedAt,
            lastError = NULL
        WHERE localId = :localId
    """)
    suspend fun markSyncing(
        localId: String,
        updatedAt: Long = System.currentTimeMillis()
    )

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
}