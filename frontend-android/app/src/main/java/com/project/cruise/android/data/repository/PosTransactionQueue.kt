package com.project.cruise.android.data.repository

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.project.cruise.android.BuildConfig
import com.project.cruise.android.data.local.CruiseDatabase
import com.project.cruise.android.data.local.pos.PosScanType
import com.project.cruise.android.data.local.pos.PosTransactionEntity
import com.project.cruise.android.worker.PosSyncWorker
import java.util.UUID
import java.util.concurrent.TimeUnit

class PosTransactionQueue(context: Context) {
    private val appContext = context.applicationContext
    private val dao = CruiseDatabase.getInstance(appContext).posTransactionDao()

    suspend fun enqueue(scanType: PosScanType, scannedValue: String): String {
        val localId = UUID.randomUUID().toString()
        dao.insert(
            PosTransactionEntity(
                localId = localId,
                terminalCode = BuildConfig.POS_TERMINAL_CODE,
                scanType = scanType.name,
                scannedValue = scannedValue.trim()
            )
        )
        scheduleSync(appContext)
        return localId
    }

    fun observePendingCount() = dao.observePendingCount()
    fun observeAll() = dao.observeAll()

    companion object {
        private const val UNIQUE_WORK_NAME = "pos-transaction-sync"

        fun scheduleSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = OneTimeWorkRequestBuilder<PosSyncWorker>()
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.SECONDS)
                .build()
            WorkManager.getInstance(context.applicationContext).enqueueUniqueWork(
                UNIQUE_WORK_NAME,
                ExistingWorkPolicy.KEEP,
                request
            )
        }
    }
}
