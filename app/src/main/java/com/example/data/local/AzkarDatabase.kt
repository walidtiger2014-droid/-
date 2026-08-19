package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.AzkarDao
import com.example.data.local.entity.AppSettingsEntity
import com.example.data.local.entity.DhikrProgressEntity
import com.example.data.local.entity.FavoriteEntity
import com.example.data.local.entity.ReminderEntity
import com.example.data.local.entity.TasbeehEntity

@Database(
    entities = [
        FavoriteEntity::class,
        DhikrProgressEntity::class,
        TasbeehEntity::class,
        AppSettingsEntity::class,
        ReminderEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AzkarDatabase : RoomDatabase() {

    abstract fun azkarDao(): AzkarDao

    companion object {
        @Volatile
        private var INSTANCE: AzkarDatabase? = null

        fun getDatabase(context: Context): AzkarDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AzkarDatabase::class.java,
                    "azkar_muslim.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
