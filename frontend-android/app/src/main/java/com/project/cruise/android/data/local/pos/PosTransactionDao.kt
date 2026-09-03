package com.project.cruise.android.data.local.pos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PosTransactionDao {
    @Query("SELECT * FROM pos_transactions WHERE localId = :localId LIMIT 1")
    suspend fun findByLocalId(localId: String): PosTransactionEntity?
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(transaction: PosTransactionEntity)

    @Update
    suspend fun update(transaction: PosTransactionEntity)

    @Query("SELECT * FROM pos_transactions WHERE status IN ('PENDING_SYNC', 'FAILED') ORDER BY createdAt ASC")
    suspend fun findWaitingForSync(): List<PosTransactionEntity>

    @Query("SELECT COUNT(*) FROM pos_transactions WHERE status IN ('PENDING_SYNC', 'FAILED', 'SYNCING')")
    fun observePendingCount(): Flow<Int>

    @Query("SELECT * FROM pos_transactions ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<PosTransactionEntity>>
}
