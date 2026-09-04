package com.project.cruise.android.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.project.cruise.android.data.local.pos.PosTransactionDao
import com.project.cruise.android.data.local.pos.PosTransactionEntity

@Database(
    entities = [PosTransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CruiseDatabase : RoomDatabase() {
    abstract fun posTransactionDao(): PosTransactionDao

    companion object {
        @Volatile private var instance: CruiseDatabase? = null

        fun getInstance(context: Context): CruiseDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                CruiseDatabase::class.java,
                "cruise_offline.db"
            ).build().also { instance = it }
        }
    }
}
