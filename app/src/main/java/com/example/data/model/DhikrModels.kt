package com.example.data.model

data class DhikrItem(
    val id: Int,
    val categoryId: Int,
    val categoryName: String,
    val title: String = "",
    val text: String,
    val fadl: String = "",
    val source: String = "",
    val targetCount: Int = 1,
    val audio: String? = null,
    val order: Int = 0
) {
    val repeatCount: Int get() = targetCount
    val category: String get() = when (categoryId) {
        1 -> "morning"
        2 -> "evening"
        else -> "category_$categoryId"
    }
}

data class DhikrCategory(
    val id: Int,
    val name: String,
    val description: String,
    val iconName: String,
    val itemsCount: Int
)

data class TasbeehPreset(
    val id: Int,
    val arabicText: String,
    val virtue: String,
    val defaultTarget: Int = 33
)

enum class AppThemeMode(val title: String) {
    SYSTEM("حسب إعدادات النظام"),
    LIGHT("الوضع الفاتح"),
    DARK("الوضع الداكن")
}

enum class ColorPalette(val title: String, val hexPrimary: Long) {
    EMERALD("الزمرد الإسلامي", 0xFF0F6E50),
    GOLD("العنبر والذهب", 0xFFB8860B),
    MIDNIGHT("الليل الكحلي", 0xFF1E3A5F),
    TEAL("الفيروز الهادئ", 0xFF007A78)
}

enum class FontSizeScale(val scale: Float, val label: String) {
    SMALL(0.88f, "صغير"),
    MEDIUM(1.0f, "متوسط"),
    LARGE(1.18f, "كبير"),
    EXTRA_LARGE(1.35f, "كبير جداً")
}

data class ReminderConfig(
    val id: String,
    val title: String,
    val description: String,
    val isEnabled: Boolean,
    val hour: Int,
    val minute: Int,
    val intervalHours: Int = 0
)
