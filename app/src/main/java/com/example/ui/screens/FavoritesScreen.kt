package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DhikrItem
import com.example.ui.components.DhikrCard
import com.example.ui.viewmodel.AzkarUiState
import com.example.ui.viewmodel.ScreenDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    favoriteDhikrs: List<DhikrItem>,
    uiState: AzkarUiState,
    onNavigateToCategories: () -> Unit,
    onIncrementDhikr: (DhikrItem) -> Unit,
    onResetDhikr: (Int) -> Unit,
    onToggleFavorite: (Int) -> Unit,
    onCopyDhikr: (DhikrItem) -> Unit,
    onShareDhikr: (DhikrItem) -> Unit,
    onPlayAudio: (DhikrItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("favorites_screen")
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "الأذكار المفضلة",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        if (favoriteDhikrs.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(90.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.FavoriteBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(44.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "لا توجد أذكار في المفضلة بعد",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "يمكنك إضافة أي ذكر إلى مفضلتك بالضغط على أيقونة القلب ❤️ لسهولة الوصول إليه في أي وقت.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onNavigateToCategories,
                        modifier = Modifier.testTag("btn_explore_from_favorites")
                    ) {
                        Text("تصفح الأذكار")
                    }
                }
            }
        } else {
            Text(
                text = "${favoriteDhikrs.size} أذكار محفوظة",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp)
            ) {
                items(
                    items = favoriteDhikrs,
                    key = { it.id }
                ) { dhikr ->
                    val currentCount = uiState.progressMap[dhikr.id] ?: 0
                    val isPlaying = uiState.currentlyPlayingDhikrId == dhikr.id

                    DhikrCard(
                        dhikr = dhikr,
                        currentCount = currentCount,
                        isFavorite = true,
                        isPlayingAudio = isPlaying,
                        fontSizeScale = uiState.settings.fontSizeScale,
                        onIncrement = { onIncrementDhikr(dhikr) },
                        onReset = { onResetDhikr(dhikr.id) },
                        onToggleFavorite = { onToggleFavorite(dhikr.id) },
                        onCopy = { onCopyDhikr(dhikr) },
                        onShare = { onShareDhikr(dhikr) },
                        onPlayAudio = { onPlayAudio(dhikr) }
                    )
                }
            }
        }
    }
}
