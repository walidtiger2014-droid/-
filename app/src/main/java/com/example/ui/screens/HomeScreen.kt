package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DhikrCategory
import com.example.data.model.DhikrItem
import com.example.ui.components.DhikrCard
import com.example.ui.components.HijriHeaderCard
import com.example.ui.components.IslamicDivider
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.AzkarUiState
import com.example.ui.viewmodel.ScreenDestination

@Composable
fun HomeScreen(
    uiState: AzkarUiState,
    dailyDhikr: DhikrItem,
    categories: List<DhikrCategory>,
    onOpenCategory: (DhikrCategory) -> Unit,
    onOpenCategoryById: (Int) -> Unit,
    onNavigate: (ScreenDestination) -> Unit,
    onIncrementDhikr: (DhikrItem) -> Unit,
    onResetDhikr: (Int) -> Unit,
    onToggleFavorite: (Int) -> Unit,
    onCopyDhikr: (DhikrItem) -> Unit,
    onShareDhikr: (DhikrItem) -> Unit,
    onPlayAudio: (DhikrItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_content"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // 1. Hijri Header Card
        item {
            HijriHeaderCard(
                onSearchClick = { onNavigate(ScreenDestination.SEARCH) },
                onRemindersClick = { onNavigate(ScreenDestination.REMINDERS) }
            )
        }

        // 2. Quick Access Section (أذكار الصباح، أذكار المساء، أذكار النوم، السبحة، بعد الصلاة)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "الوصول السريع",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Primary Quick Cards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Morning Azkar
                    QuickAccessCard(
                        title = "أذكار الصباح",
                        subtitle = "12 ذكراً",
                        icon = Icons.Default.WbSunny,
                        iconTint = Color(0xFFF59E0B),
                        gradientColors = listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A)),
                        onClick = { onOpenCategoryById(1) },
                        modifier = Modifier.weight(1f),
                        testTag = "quick_morning"
                    )

                    // Evening Azkar
                    QuickAccessCard(
                        title = "أذكار المساء",
                        subtitle = "12 ذكراً",
                        icon = Icons.Default.NightsStay,
                        iconTint = Color(0xFF6366F1),
                        gradientColors = listOf(Color(0xFFE0E7FF), Color(0xFFC7D2FE)),
                        onClick = { onOpenCategoryById(2) },
                        modifier = Modifier.weight(1f),
                        testTag = "quick_evening"
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Secondary Quick Row (Sleep, Prayer, Tasbeeh)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Sleep
                    QuickMiniCard(
                        title = "أذكار النوم",
                        icon = Icons.Default.Bedtime,
                        tint = Color(0xFF8B5CF6),
                        onClick = { onOpenCategoryById(3) },
                        modifier = Modifier.weight(1f),
                        testTag = "quick_sleep"
                    )

                    // After Prayer
                    QuickMiniCard(
                        title = "بعد الصلاة",
                        icon = Icons.Default.Mosque,
                        tint = Color(0xFF10B981),
                        onClick = { onOpenCategoryById(5) },
                        modifier = Modifier.weight(1f),
                        testTag = "quick_prayer"
                    )

                    // Tasbeeh
                    QuickMiniCard(
                        title = "السبحة",
                        icon = Icons.Default.RadioButtonChecked,
                        tint = Color(0xFFEC4899),
                        onClick = { onNavigate(ScreenDestination.TASBEEH) },
                        modifier = Modifier.weight(1f),
                        testTag = "quick_tasbeeh"
                    )
                }
            }
        }

        // 3. Daily Dhikr Card (ذكر اليوم)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ذكر اليوم المبارك",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Text(
                        text = "يتجدد يومياً",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val currentCount = uiState.progressMap[dailyDhikr.id] ?: 0
                val isFav = uiState.favoriteIds.contains(dailyDhikr.id)

                DhikrCard(
                    dhikr = dailyDhikr,
                    currentCount = currentCount,
                    isFavorite = isFav,
                    isPlayingAudio = uiState.currentlyPlayingDhikrId == dailyDhikr.id,
                    fontSizeScale = uiState.settings.fontSizeScale,
                    onIncrement = { onIncrementDhikr(dailyDhikr) },
                    onReset = { onResetDhikr(dailyDhikr.id) },
                    onToggleFavorite = { onToggleFavorite(dailyDhikr.id) },
                    onCopy = { onCopyDhikr(dailyDhikr) },
                    onShare = { onShareDhikr(dailyDhikr) },
                    onPlayAudio = { onPlayAudio(dailyDhikr) }
                )
            }
        }

        // 4. Explore All Categories Carousel / Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "أقسام الأذكار",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Surface(
                        onClick = { onNavigate(ScreenDestination.CATEGORIES) },
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Transparent
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "عرض الكل (20)",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Horizontal Row of Categories
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(categories) { category ->
                        CategoryChipCard(
                            category = category,
                            onClick = { onOpenCategory(category) }
                        )
                    }
                }
            }
        }

        // 5. Islamic Divider & Quote footer
        item {
            IslamicDivider(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "﴿ أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ ﴾",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = GoldAccent
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "سورة الرعد - الآية 28",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun QuickAccessCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Card(
        modifier = modifier
            .height(95.dp)
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = iconTint.copy(alpha = 0.15f),
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickMiniCard(
    title: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun CategoryChipCard(
    category: DhikrCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(140.dp)
            .height(100.dp)
            .clickable { onClick() }
            .testTag("category_chip_${category.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${category.id}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Column {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = "${category.itemsCount} أذكار",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
