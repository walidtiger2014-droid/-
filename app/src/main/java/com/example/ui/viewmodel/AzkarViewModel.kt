package com.example.ui.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.datasource.AzkarDataSource
import com.example.data.local.AzkarDatabase
import com.example.data.local.entity.AppSettingsEntity
import com.example.data.local.entity.ReminderEntity
import com.example.data.local.entity.TasbeehEntity
import com.example.data.model.AppThemeMode
import com.example.data.model.ColorPalette
import com.example.data.model.DhikrCategory
import com.example.data.model.DhikrItem
import com.example.data.model.FontSizeScale
import com.example.data.model.TasbeehPreset
import com.example.data.repository.AzkarRepository
import com.example.util.AudioTtsHelper
import com.example.util.NotificationHelper
import com.example.util.SoundAndHapticUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenDestination {
    HOME,
    CATEGORIES,
    CATEGORY_DETAIL,
    TASBEEH,
    FAVORITES,
    SEARCH,
    REMINDERS,
    SETTINGS
}

data class AzkarUiState(
    val currentScreen: ScreenDestination = ScreenDestination.HOME,
    val selectedCategory: DhikrCategory? = null,
    val searchQuery: String = "",
    val searchResults: List<DhikrItem> = emptyList(),
    val favoriteIds: Set<Int> = emptySet(),
    val progressMap: Map<Int, Int> = emptyMap(),
    val tasbeeh: TasbeehEntity = TasbeehEntity(),
    val settings: AppSettingsEntity = AppSettingsEntity(),
    val reminders: List<ReminderEntity> = emptyList(),
    val currentlyPlayingDhikrId: Int? = null
)

class AzkarViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AzkarRepository
    val soundAndHaptics = SoundAndHapticUtil(application)
    private val ttsHelper = AudioTtsHelper(application)

    private val _currentScreen = MutableStateFlow(ScreenDestination.HOME)
    private val _selectedCategory = MutableStateFlow<DhikrCategory?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _currentlyPlayingDhikrId = MutableStateFlow<Int?>(null)

    val categories: List<DhikrCategory>
    val tasbeehPresets: List<TasbeehPreset>

    val uiState: StateFlow<AzkarUiState>

    init {
        val db = AzkarDatabase.getDatabase(application)
        repository = AzkarRepository(db.azkarDao())
        categories = repository.categories
        tasbeehPresets = repository.tasbeehPresets

        NotificationHelper.createNotificationChannel(application)

        uiState = combine(
            _currentScreen,
            _selectedCategory,
            _searchQuery,
            repository.favoriteIds,
            repository.progressMap,
            repository.tasbeehState,
            repository.appSettings,
            repository.reminders,
            _currentlyPlayingDhikrId
        ) { values ->
            val screen = values[0] as ScreenDestination
            val category = values[1] as? DhikrCategory
            val query = values[2] as String
            val favIds = (values[3] as List<Int>).toSet()
            val progMap = values[4] as Map<Int, Int>
            val tasbeeh = values[5] as TasbeehEntity
            val settings = values[6] as AppSettingsEntity
            val reminders = values[7] as List<ReminderEntity>
            val playingId = values[8] as? Int

            val searchResults = if (query.isNotBlank()) repository.search(query) else emptyList()

            AzkarUiState(
                currentScreen = screen,
                selectedCategory = category,
                searchQuery = query,
                searchResults = searchResults,
                favoriteIds = favIds,
                progressMap = progMap,
                tasbeeh = tasbeeh,
                settings = settings,
                reminders = reminders,
                currentlyPlayingDhikrId = playingId
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AzkarUiState()
        )
    }

    // --- Navigation ---
    fun navigateTo(screen: ScreenDestination) {
        _currentScreen.value = screen
    }

    fun openCategory(category: DhikrCategory) {
        _selectedCategory.value = category
        _currentScreen.value = ScreenDestination.CATEGORY_DETAIL
    }

    fun openCategoryById(categoryId: Int) {
        val category = categories.find { it.id == categoryId }
        if (category != null) {
            openCategory(category)
        }
    }

    fun navigateBack() {
        when (_currentScreen.value) {
            ScreenDestination.CATEGORY_DETAIL -> _currentScreen.value = ScreenDestination.CATEGORIES
            ScreenDestination.SEARCH -> _currentScreen.value = ScreenDestination.HOME
            ScreenDestination.REMINDERS -> _currentScreen.value = ScreenDestination.SETTINGS
            else -> _currentScreen.value = ScreenDestination.HOME
        }
    }

    // --- Dhikr Actions ---
    fun getDhikrsForCategory(categoryId: Int): List<DhikrItem> {
        return repository.getDhikrsByCategory(categoryId)
    }

    fun getFavoriteDhikrs(): List<DhikrItem> {
        val favIds = uiState.value.favoriteIds
        return AzkarDataSource.dhikrList.filter { favIds.contains(it.id) }
    }

    fun getDailyDhikr(): DhikrItem {
        return repository.getDailyDhikr()
    }

    fun incrementDhikr(dhikr: DhikrItem) {
        viewModelScope.launch {
            val settings = uiState.value.settings
            soundAndHaptics.vibrateClick(settings.hapticsEnabled)
            soundAndHaptics.playClickSound(settings.soundEnabled)

            val newCount = repository.incrementProgress(dhikr.id, dhikr.targetCount)
            if (newCount == dhikr.targetCount) {
                soundAndHaptics.vibrateTargetReached(settings.hapticsEnabled)
                soundAndHaptics.playCompletionSound(settings.soundEnabled)
            }
        }
    }

    fun markDhikrCompleted(dhikr: DhikrItem) {
        viewModelScope.launch {
            val settings = uiState.value.settings
            soundAndHaptics.vibrateTargetReached(settings.hapticsEnabled)
            soundAndHaptics.playCompletionSound(settings.soundEnabled)
            repository.markCompleted(dhikr.id, dhikr.targetCount)
        }
    }

    fun resetDhikr(dhikrId: Int) {
        viewModelScope.launch {
            repository.resetProgress(dhikrId)
        }
    }

    fun resetCategoryDhikrs(categoryId: Int) {
        viewModelScope.launch {
            repository.resetCategoryProgress(categoryId)
            Toast.makeText(getApplication(), "تمت إعادة ضبط عدادات هذا القسم", Toast.LENGTH_SHORT).show()
        }
    }

    fun toggleFavorite(dhikrId: Int) {
        viewModelScope.launch {
            repository.toggleFavorite(dhikrId)
            val isFavNow = !uiState.value.favoriteIds.contains(dhikrId)
            val msg = if (isFavNow) "تمت الإضافة إلى المفضلة" else "تم الحذف من المفضلة"
            Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    fun copyDhikr(dhikr: DhikrItem) {
        val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val shareText = buildString {
            appendLine("« ${dhikr.text} »")
            if (dhikr.source.isNotBlank()) appendLine("المصدر: ${dhikr.source}")
            if (dhikr.fadl.isNotBlank()) appendLine("الفضل: ${dhikr.fadl}")
            appendLine("— تطبيق أذكار المسلم")
        }
        val clip = ClipData.newPlainText("Dhikr", shareText)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(getApplication(), "تم نسخ نص الذكر", Toast.LENGTH_SHORT).show()
    }

    fun shareDhikr(dhikr: DhikrItem) {
        val shareText = buildString {
            appendLine("« ${dhikr.text} »")
            if (dhikr.source.isNotBlank()) appendLine("\nالمصدر: ${dhikr.source}")
            if (dhikr.fadl.isNotBlank()) appendLine("الفضل: ${dhikr.fadl}")
            appendLine("\nتطبيق أذكار المسلم - رفيقك اليومي لذكر الله")
        }
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val shareIntent = Intent.createChooser(sendIntent, "مشاركة الذكر المبارك").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        getApplication<Application>().startActivity(shareIntent)
    }

    fun shareApp() {
        val shareText = "حمّل تطبيق «أذكار المسلم» واستمتع بالأذكار اليومية الموثوقة والسبحة الإلكترونية والتنبيهات بدون إنترنت.\nنسأل الله أن ينفع به.\n\nhttps://play.google.com/store/apps/details?id=com.aistudio.azkarmuslim.app"
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val shareIntent = Intent.createChooser(sendIntent, "مشاركة تطبيق أذكار المسلم").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        getApplication<Application>().startActivity(shareIntent)
    }

    fun playAudio(dhikr: DhikrItem) {
        if (_currentlyPlayingDhikrId.value == dhikr.id) {
            ttsHelper.stop()
            _currentlyPlayingDhikrId.value = null
        } else {
            _currentlyPlayingDhikrId.value = dhikr.id
            ttsHelper.speak(dhikr.text) {
                _currentlyPlayingDhikrId.value = null
            }
        }
    }

    // --- Search ---
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    // --- Tasbeeh ---
    fun incrementTasbeeh() {
        viewModelScope.launch {
            val settings = uiState.value.settings
            soundAndHaptics.vibrateClick(settings.hapticsEnabled)
            soundAndHaptics.playClickSound(settings.soundEnabled)

            val current = uiState.value.tasbeeh
            val newCount = current.currentCount + 1

            repository.incrementTasbeeh()

            if (newCount == current.targetCount) {
                soundAndHaptics.vibrateTargetReached(settings.hapticsEnabled)
                soundAndHaptics.playCompletionSound(settings.soundEnabled)
            }
        }
    }

    fun resetTasbeeh() {
        viewModelScope.launch {
            repository.resetTasbeehCounter()
        }
    }

    fun selectTasbeehPreset(preset: TasbeehPreset) {
        viewModelScope.launch {
            repository.updateTasbeehPreset(preset.id, preset.arabicText, preset.defaultTarget)
        }
    }

    fun setTasbeehTarget(target: Int) {
        viewModelScope.launch {
            repository.setTasbeehTarget(target)
        }
    }

    // --- Settings & Reminders ---
    fun updateThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            val current = uiState.value.settings
            repository.saveSettings(current.copy(themeMode = mode.name))
        }
    }

    fun updateColorPalette(palette: ColorPalette) {
        viewModelScope.launch {
            val current = uiState.value.settings
            repository.saveSettings(current.copy(colorPalette = palette.name))
        }
    }

    fun updateFontSize(scale: FontSizeScale) {
        viewModelScope.launch {
            val current = uiState.value.settings
            repository.saveSettings(current.copy(fontSizeScale = scale.scale))
        }
    }

    fun toggleHaptics(enabled: Boolean) {
        viewModelScope.launch {
            val current = uiState.value.settings
            repository.saveSettings(current.copy(hapticsEnabled = enabled))
        }
    }

    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch {
            val current = uiState.value.settings
            repository.saveSettings(current.copy(soundEnabled = enabled))
        }
    }

    fun toggleAnimations(enabled: Boolean) {
        viewModelScope.launch {
            val current = uiState.value.settings
            repository.saveSettings(current.copy(animationsEnabled = enabled))
        }
    }

    fun toggleReminder(reminderId: String, isEnabled: Boolean) {
        viewModelScope.launch {
            val reminder = uiState.value.reminders.find { it.id == reminderId } ?: return@launch
            val updated = reminder.copy(isEnabled = isEnabled)
            repository.saveReminder(updated)

            val context = getApplication<Application>()
            if (isEnabled) {
                val (title, text) = when (reminderId) {
                    "morning" -> "أذكار الصباح ☀️" to "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ... ابدأ يومك بذكر الله"
                    "evening" -> "أذكار المساء 🌙" to "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ... اختم يومك بالسكينة والاستغفار"
                    "sleep" -> "أذكار النوم 🛏️" to "بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي... حصن نفسك قبل النوم"
                    else -> "ذكر وتذكير 🌿" to "«سُبْحَانَ اللَّهِ وَبِحَمْدِهِ» رطب لسانك بذكر الله"
                }
                NotificationHelper.scheduleDailyReminder(
                    context, reminderId, updated.hour, updated.minute, title, text
                )
            } else {
                NotificationHelper.cancelReminder(context, reminderId)
            }
        }
    }

    fun updateReminderTime(reminderId: String, hour: Int, minute: Int) {
        viewModelScope.launch {
            val reminder = uiState.value.reminders.find { it.id == reminderId } ?: return@launch
            val updated = reminder.copy(hour = hour, minute = minute)
            repository.saveReminder(updated)

            if (updated.isEnabled) {
                toggleReminder(reminderId, true)
            }
        }
    }

    fun resetAllAppSettings() {
        viewModelScope.launch {
            repository.saveSettings(AppSettingsEntity())
            repository.resetAllProgress()
            repository.resetTasbeehCounter()
            Toast.makeText(getApplication(), "تمت استعادة الإعدادات الافتراضية", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsHelper.shutdown()
    }
}
