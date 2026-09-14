package com.project.cruise.android.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.project.cruise.android.data.local.pos.PosTransactionDao
import com.project.cruise.android.data.local.pos.PosTransactionEntity

@Database(
    entities = [PosTransactionEntity::class],
    version = 2,
    exportSchema = false
)
abstract class CruiseDatabase : RoomDatabase() {
    abstract fun posTransactionDao(): PosTransactionDao

    companion object {
        @Volatile private var instance: CruiseDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE pos_transactions ADD COLUMN operatorRole TEXT NOT NULL DEFAULT 'FINANCE'"
                )
                database.execSQL(
                    "ALTER TABLE pos_transactions ADD COLUMN operation TEXT NOT NULL DEFAULT 'IDENTIFY'"
                )
            }
        }

        fun getInstance(context: Context): CruiseDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                CruiseDatabase::class.java,
                "cruise_offline.db"
            ).addMigrations(MIGRATION_1_2)
                .build()
                .also { instance = it }
        }
    }
}
