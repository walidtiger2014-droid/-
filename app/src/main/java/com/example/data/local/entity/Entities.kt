package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val dhikrId: Int,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "dhikr_progress")
data class DhikrProgressEntity(
    @PrimaryKey val dhikrId: Int,
    val currentCount: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "tasbeeh_state")
data class TasbeehEntity(
    @PrimaryKey val id: Int = 1,
    val activePresetId: Int = 1,
    val customText: String = "",
    val currentCount: Int = 0,
    val targetCount: Int = 33,
    val totalLifetimeCount: Long = 0L
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val themeMode: String = "SYSTEM",
    val colorPalette: String = "EMERALD",
    val fontSizeScale: Float = 1.0f,
    val hapticsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val animationsEnabled: Boolean = true
)

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: String,
    val isEnabled: Boolean,
    val hour: Int,
    val minute: Int,
    val intervalHours: Int = 0
)
