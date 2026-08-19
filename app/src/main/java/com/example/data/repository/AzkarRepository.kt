package com.example.data.repository

import com.example.data.datasource.AzkarDataSource
import com.example.data.local.dao.AzkarDao
import com.example.data.local.entity.AppSettingsEntity
import com.example.data.local.entity.DhikrProgressEntity
import com.example.data.local.entity.FavoriteEntity
import com.example.data.local.entity.ReminderEntity
import com.example.data.local.entity.TasbeehEntity
import com.example.data.model.DhikrCategory
import com.example.data.model.DhikrItem
import com.example.data.model.TasbeehPreset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class AzkarRepository(private val dao: AzkarDao) {

    val categories: List<DhikrCategory> = AzkarDataSource.categories
    val tasbeehPresets: List<TasbeehPreset> = AzkarDataSource.tasbeehPresets

    fun getCategoryById(categoryId: Int): DhikrCategory? {
        return categories.find { it.id == categoryId }
    }

    fun getDhikrsByCategory(categoryId: Int): List<DhikrItem> {
        return AzkarDataSource.dhikrList.filter { it.categoryId == categoryId }
            .sortedBy { it.order }
    }

    fun getAllDhikrs(): List<DhikrItem> {
        return AzkarDataSource.dhikrList
    }

    fun getDailyDhikr(): DhikrItem {
        return AzkarDataSource.getDailyDhikr()
    }

    fun search(query: String): List<DhikrItem> {
        if (query.isBlank()) return emptyList()
        val normalizedQuery = normalizeArabic(query.trim())
        return AzkarDataSource.dhikrList.filter { item ->
            normalizeArabic(item.text).contains(normalizedQuery, ignoreCase = true) ||
            normalizeArabic(item.categoryName).contains(normalizedQuery, ignoreCase = true) ||
            normalizeArabic(item.fadl).contains(normalizedQuery, ignoreCase = true) ||
            normalizeArabic(item.source).contains(normalizedQuery, ignoreCase = true)
        }
    }

    private fun normalizeArabic(text: String): String {
        return text
            .replace(Regex("[\u064B-\u065F\u0670]"), "") // Remove Tashkeel (Harakat)
            .replace("أ", "ا")
            .replace("إ", "ا")
            .replace("آ", "ا")
            .replace("ى", "ي")
            .replace("ة", "ه")
            .replace("ء", "ا")
    }

    // --- Favorites ---
    val favoriteIds: Flow<List<Int>> = dao.getAllFavoriteIds()

    suspend fun toggleFavorite(dhikrId: Int) {
        val currentFavs = favoriteIds.firstOrNull() ?: emptyList()
        if (currentFavs.contains(dhikrId)) {
            dao.removeFavorite(dhikrId)
        } else {
            dao.addFavorite(FavoriteEntity(dhikrId))
        }
    }

    suspend fun removeFavorite(dhikrId: Int) {
        dao.removeFavorite(dhikrId)
    }

    // --- Dhikr Progress ---
    val progressMap: Flow<Map<Int, Int>> = dao.getAllProgress().map { list ->
        list.associate { it.dhikrId to it.currentCount }
    }

    suspend fun incrementProgress(dhikrId: Int, target: Int): Int {
        val currentProgress = dao.getProgress(dhikrId).firstOrNull()
        val current = currentProgress?.currentCount ?: 0
        val newCount = current + 1
        dao.saveProgress(DhikrProgressEntity(dhikrId, newCount))
        return newCount
    }

    suspend fun markCompleted(dhikrId: Int, target: Int) {
        dao.saveProgress(DhikrProgressEntity(dhikrId, target))
    }

    suspend fun resetProgress(dhikrId: Int) {
        dao.resetProgress(dhikrId)
    }

    suspend fun resetAllProgress() {
        dao.resetAllProgress()
    }

    suspend fun resetCategoryProgress(categoryId: Int) {
        val categoryDhikrs = getDhikrsByCategory(categoryId)
        categoryDhikrs.forEach { dhikr ->
            dao.resetProgress(dhikr.id)
        }
    }

    // --- Tasbeeh ---
    val tasbeehState: Flow<TasbeehEntity> = dao.getTasbeehState().map {
        it ?: TasbeehEntity(
            id = 1,
            activePresetId = 1,
            customText = "سُبْحَانَ اللَّهِ",
            currentCount = 0,
            targetCount = 33,
            totalLifetimeCount = 0L
        )
    }

    suspend fun incrementTasbeeh() {
        val current = dao.getTasbeehState().firstOrNull() ?: TasbeehEntity()
        val nextCount = current.currentCount + 1
        val nextTotal = current.totalLifetimeCount + 1
        dao.saveTasbeehState(
            current.copy(
                currentCount = nextCount,
                totalLifetimeCount = nextTotal
            )
        )
    }

    suspend fun resetTasbeehCounter() {
        val current = dao.getTasbeehState().firstOrNull() ?: TasbeehEntity()
        dao.saveTasbeehState(current.copy(currentCount = 0))
    }

    suspend fun updateTasbeehPreset(presetId: Int, title: String, target: Int) {
        val current = dao.getTasbeehState().firstOrNull() ?: TasbeehEntity()
        dao.saveTasbeehState(
            current.copy(
                activePresetId = presetId,
                customText = title,
                targetCount = target,
                currentCount = 0
            )
        )
    }

    suspend fun setTasbeehTarget(target: Int) {
        val current = dao.getTasbeehState().firstOrNull() ?: TasbeehEntity()
        dao.saveTasbeehState(current.copy(targetCount = target))
    }

    // --- Settings ---
    val appSettings: Flow<AppSettingsEntity> = dao.getSettings().map {
        it ?: AppSettingsEntity()
    }

    suspend fun saveSettings(settings: AppSettingsEntity) {
        dao.saveSettings(settings)
    }

    // --- Reminders ---
    val reminders: Flow<List<ReminderEntity>> = dao.getAllReminders().map { list ->
        if (list.isEmpty()) {
            val defaults = listOf(
                ReminderEntity("morning", true, 7, 0),
                ReminderEntity("evening", true, 18, 0),
                ReminderEntity("sleep", false, 22, 30),
                ReminderEntity("random", false, 14, 0, 3)
            )
            dao.saveReminders(defaults)
            defaults
        } else {
            list
        }
    }

    suspend fun saveReminder(reminder: ReminderEntity) {
        dao.saveReminder(reminder)
    }
}
