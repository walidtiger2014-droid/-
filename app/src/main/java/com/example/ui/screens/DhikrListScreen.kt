package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DhikrCategory
import com.example.data.model.DhikrItem
import com.example.ui.components.DhikrCard
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.AzkarUiState

enum class DhikrViewMode {
    LIST,
    STEP_BY_STEP
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DhikrListScreen(
    category: DhikrCategory,
    dhikrs: List<DhikrItem>,
    uiState: AzkarUiState,
    onBack: () -> Unit,
    onResetCategory: (Int) -> Unit,
    onIncrementDhikr: (DhikrItem) -> Unit,
    onMarkDhikrCompleted: (DhikrItem) -> Unit,
    onResetDhikr: (Int) -> Unit,
    onToggleFavorite: (Int) -> Unit,
    onCopyDhikr: (DhikrItem) -> Unit,
    onShareDhikr: (DhikrItem) -> Unit,
    onPlayAudio: (DhikrItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var viewMode by remember { mutableStateOf(DhikrViewMode.LIST) }
    var currentStepIndex by remember { mutableIntStateOf(0) }
    var showResetDialog by remember { mutableStateOf(false) }
    var localFontScale by remember { mutableStateOf(uiState.settings.fontSizeScale) }

    val totalCount = dhikrs.size
    val safeIndex = if (totalCount > 0) currentStepIndex.coerceIn(0, totalCount - 1) else 0

    val completedCount = dhikrs.count { dhikr ->
        val current = uiState.progressMap[dhikr.id] ?: 0
        current >= dhikr.targetCount
    }
    val progressRatio = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f

    // Category Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = "إعادة ضبط عدادات القسم",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Right
                )
            },
            text = {
                Text(
                    text = "هل تريد تصفير تقدم قراءة جميع أذكار «${category.name}» والبدء من جديد؟",
                    textAlign = TextAlign.Right
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetCategory(category.id)
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("نعم، إعادة الضبط")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("dhikr_list_screen")
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    )
                    Text(
                        text = "$completedCount من $totalCount مكتمل",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("btn_back_from_category")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع"
                    )
                }
            },
            actions = {
                // View Mode Toggle (List vs Single Step)
                IconButton(
                    onClick = {
                        viewMode = if (viewMode == DhikrViewMode.LIST) DhikrViewMode.STEP_BY_STEP else DhikrViewMode.LIST
                    },
                    modifier = Modifier.testTag("btn_toggle_view_mode")
                ) {
                    Icon(
                        imageVector = if (viewMode == DhikrViewMode.LIST) Icons.Default.ViewCarousel else Icons.AutoMirrored.Filled.List,
                        contentDescription = if (viewMode == DhikrViewMode.LIST) "التبديل إلى وضع التمرير المفرد" else "التبديل إلى وضع القائمة",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Font Size Cycle
                IconButton(
                    onClick = {
                        localFontScale = when {
                            localFontScale < 1.0f -> 1.0f
                            localFontScale < 1.2f -> 1.25f
                            localFontScale < 1.4f -> 1.45f
                            else -> 0.9f
                        }
                    },
                    modifier = Modifier.testTag("btn_cycle_font_size")
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatSize,
                        contentDescription = "تغيير حجم الخط",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Reset Category
                IconButton(
                    onClick = { showResetDialog = true },
                    modifier = Modifier.testTag("btn_reset_category")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "إعادة ضبط جميع أذكار هذا القسم",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        // Progress Header Banner
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (completedCount == totalCount && totalCount > 0) "🎉 تم إتمام جميع الأذكار بنجاح، تقبل الله طاعتكم" else category.description,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = if (completedCount == totalCount && totalCount > 0) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progressRatio },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (completedCount == totalCount && totalCount > 0) SuccessGreen else MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surface
                    )
                }

                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${(progressRatio * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (completedCount == totalCount && totalCount > 0) SuccessGreen else MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        // Main Body: List View OR Step-by-Step Reader View
        if (dhikrs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "لا توجد أذكار في هذا القسم حالياً",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else if (viewMode == DhikrViewMode.LIST) {
            // Full Scrollable Cards List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 4.dp, bottom = 90.dp)
            ) {
                items(
                    items = dhikrs,
                    key = { it.id }
                ) { dhikr ->
                    val currentCount = uiState.progressMap[dhikr.id] ?: 0
                    val isFav = uiState.favoriteIds.contains(dhikr.id)
                    val isPlaying = uiState.currentlyPlayingDhikrId == dhikr.id

                    DhikrCard(
                        dhikr = dhikr,
                        currentCount = currentCount,
                        isFavorite = isFav,
                        isPlayingAudio = isPlaying,
                        fontSizeScale = localFontScale,
                        onIncrement = { onIncrementDhikr(dhikr) },
                        onMarkCompleted = { onMarkDhikrCompleted(dhikr) },
                        onReset = { onResetDhikr(dhikr.id) },
                        onToggleFavorite = { onToggleFavorite(dhikr.id) },
                        onCopy = { onCopyDhikr(dhikr) },
                        onShare = { onShareDhikr(dhikr) },
                        onPlayAudio = { onPlayAudio(dhikr) }
                    )
                }
            }
        } else {
            // Single Dhikr Step-by-Step Reader View (السابق / التالي)
            val currentDhikr = dhikrs[safeIndex]
            val currentCount = uiState.progressMap[currentDhikr.id] ?: 0
            val isFav = uiState.favoriteIds.contains(currentDhikr.id)
            val isPlaying = uiState.currentlyPlayingDhikrId == currentDhikr.id
            val isCompleted = currentCount >= currentDhikr.targetCount

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Index indicator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "الذكر ${safeIndex + 1} من $totalCount",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    if (currentDhikr.title.isNotBlank()) {
                        Text(
                            text = currentDhikr.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Dhikr Card Detail
                AnimatedContent(
                    targetState = currentDhikr,
                    transitionSpec = {
                        (fadeIn() + slideInHorizontally { width -> width / 3 })
                            .togetherWith(fadeOut() + slideOutHorizontally { width -> -width / 3 })
                    },
                    label = "stepReaderTransition"
                ) { dhikrItem ->
                    DhikrCard(
                        dhikr = dhikrItem,
                        currentCount = currentCount,
                        isFavorite = isFav,
                        isPlayingAudio = isPlaying,
                        fontSizeScale = localFontScale,
                        onIncrement = { onIncrementDhikr(dhikrItem) },
                        onMarkCompleted = { onMarkDhikrCompleted(dhikrItem) },
                        onReset = { onResetDhikr(dhikrItem.id) },
                        onToggleFavorite = { onToggleFavorite(dhikrItem.id) },
                        onCopy = { onCopyDhikr(dhikrItem) },
                        onShare = { onShareDhikr(dhikrItem) },
                        onPlayAudio = { onPlayAudio(dhikrItem) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Navigation Controls (السابق والتالي)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Button
                    OutlinedButton(
                        onClick = {
                            if (currentStepIndex > 0) currentStepIndex--
                        },
                        enabled = currentStepIndex > 0,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_prev_dhikr")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "الذكر السابق",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "السابق", fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Next Button
                    Button(
                        onClick = {
                            if (currentStepIndex < totalCount - 1) currentStepIndex++
                        },
                        enabled = currentStepIndex < totalCount - 1,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCompleted) SuccessGreen else MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_next_dhikr")
                    ) {
                        Text(text = "التالي", fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "الذكر التالي",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
