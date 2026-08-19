package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.AppSettingsEntity
import com.example.data.local.entity.DhikrProgressEntity
import com.example.data.local.entity.FavoriteEntity
import com.example.data.local.entity.ReminderEntity
import com.example.data.local.entity.TasbeehEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AzkarDao {

    // --- Favorites ---
    @Query("SELECT dhikrId FROM favorites")
    fun getAllFavoriteIds(): Flow<List<Int>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE dhikrId = :dhikrId)")
    fun isFavorite(dhikrId: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE dhikrId = :dhikrId")
    suspend fun removeFavorite(dhikrId: Int)

    // --- Dhikr Progress / Counters ---
    @Query("SELECT * FROM dhikr_progress")
    fun getAllProgress(): Flow<List<DhikrProgressEntity>>

    @Query("SELECT * FROM dhikr_progress WHERE dhikrId = :dhikrId")
    fun getProgress(dhikrId: Int): Flow<DhikrProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: DhikrProgressEntity)

    @Query("DELETE FROM dhikr_progress WHERE dhikrId = :dhikrId")
    suspend fun resetProgress(dhikrId: Int)

    @Query("DELETE FROM dhikr_progress")
    suspend fun resetAllProgress()

    // --- Tasbeeh State ---
    @Query("SELECT * FROM tasbeeh_state WHERE id = 1")
    fun getTasbeehState(): Flow<TasbeehEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTasbeehState(state: TasbeehEntity)

    // --- App Settings ---
    @Query("SELECT * FROM app_settings WHERE id = 1")
    fun getSettings(): Flow<AppSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: AppSettingsEntity)

    // --- Reminders ---
    @Query("SELECT * FROM reminders")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReminder(reminder: ReminderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReminders(reminders: List<ReminderEntity>)
}
