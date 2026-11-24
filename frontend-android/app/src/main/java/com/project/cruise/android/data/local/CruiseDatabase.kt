package com.project.cruise.android.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.project.cruise.android.data.local.pos.PosCommandDao
import com.project.cruise.android.data.local.pos.PosCommandEntity
import com.project.cruise.android.data.local.pos.PosTransactionDao
import com.project.cruise.android.data.local.pos.PosTransactionEntity

@Database(
    entities = [
        PosTransactionEntity::class,
        PosCommandEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class CruiseDatabase : RoomDatabase() {

    abstract fun posTransactionDao(): PosTransactionDao

    abstract fun posCommandDao(): PosCommandDao

    companion object {
        @Volatile
        private var instance: CruiseDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    ALTER TABLE pos_transactions
                    ADD COLUMN operatorRole TEXT NOT NULL DEFAULT 'FINANCE'
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    ALTER TABLE pos_transactions
                    ADD COLUMN operation TEXT NOT NULL DEFAULT 'IDENTIFY'
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS pos_commands (
                        localId TEXT NOT NULL,
                        clientRequestId TEXT NOT NULL,
                        operation TEXT NOT NULL,
                        payloadJson TEXT NOT NULL,
                        status TEXT NOT NULL,
                        attemptCount INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        lastError TEXT,
                        PRIMARY KEY(localId)
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE UNIQUE INDEX IF NOT EXISTS
                    index_pos_commands_clientRequestId
                    ON pos_commands(clientRequestId)
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS
                    index_pos_commands_status_createdAt
                    ON pos_commands(status, createdAt)
                    """.trimIndent()
                )
            }
        }

        fun getInstance(context: Context): CruiseDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    CruiseDatabase::class.java,
                    "cruise_offline.db"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3
                    )
                    .build()
                    .also {
                        instance = it
                    }
            }
    }
}