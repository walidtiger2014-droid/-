package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AppSettingsEntity
import com.example.data.local.entity.TasbeehEntity
import com.example.data.model.TasbeehPreset
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SuccessGreen
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbeehScreen(
    tasbeeh: TasbeehEntity,
    presets: List<TasbeehPreset>,
    settings: AppSettingsEntity,
    onIncrement: () -> Unit,
    onReset: () -> Unit,
    onSelectPreset: (TasbeehPreset) -> Unit,
    onSetTarget: (Int) -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onToggleHaptic: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCustomTargetDialog by remember { mutableStateOf(false) }
    var customTargetInput by remember { mutableStateOf("") }

    val currentCount = tasbeeh.currentCount
    val targetCount = tasbeeh.targetCount
    val progress = if (targetCount > 0) (currentCount.toFloat() / targetCount.toFloat()).coerceIn(0f, 1f) else 0f
    val isCompleted = currentCount >= targetCount && targetCount > 0

    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
            .testTag("tasbeeh_screen")
    ) {
        // Top App Bar with sound/vibration shortcuts
        TopAppBar(
            title = {
                Text(
                    text = "السبحة الإلكترونية",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            },
            actions = {
                IconButton(
                    onClick = { onToggleHaptic(!settings.hapticsEnabled) },
                    modifier = Modifier.testTag("btn_tasbeeh_haptic")
                ) {
                    Icon(
                        imageVector = Icons.Default.Vibration,
                        contentDescription = "الاهتزاز",
                        tint = if (settings.hapticsEnabled) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                }
                IconButton(
                    onClick = { onToggleSound(!settings.soundEnabled) },
                    modifier = Modifier.testTag("btn_tasbeeh_sound")
                ) {
                    Icon(
                        imageVector = if (settings.soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "الصوت",
                        tint = if (settings.soundEnabled) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        // Preset Dhikr Selector Carousel
        Text(
            text = "اختر صيغة التسبيح",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(presets) { preset ->
                val isSelected = tasbeeh.activePresetId == preset.id
                Surface(
                    onClick = { onSelectPreset(preset) },
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) primaryColor else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) primaryColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("preset_chip_${preset.id}")
                ) {
                    Text(
                        text = preset.arabicText,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        ),
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Active Dhikr Title
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = tasbeeh.customText.ifBlank { "سُبْحَانَ اللَّهِ" },
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = primaryColor,
                        textAlign = TextAlign.Center
                    )
                )
                if (isCompleted) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "✨ ما شاء الله، بلغت الهدف المحدد!",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Big Circular Interactive Tasbeeh Ring / Tap Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            // Interactive Dial & Beads
            Surface(
                onClick = onIncrement,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                border = BorderStroke(3.dp, if (isCompleted) SuccessGreen.copy(alpha = 0.8f) else primaryColor.copy(alpha = 0.3f)),
                modifier = Modifier
                    .size(260.dp)
                    .testTag("tasbeeh_counter_dial")
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Circular Progress Arc
                    Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                        val strokeWidth = 8.dp.toPx()
                        val diameter = size.minDimension - strokeWidth
                        val radius = diameter / 2f
                        val center = Offset(size.width / 2f, size.height / 2f)

                        // Track
                        drawCircle(
                            color = trackColor,
                            radius = radius,
                            center = center,
                            style = Stroke(width = strokeWidth)
                        )

                        // Progress Arc
                        drawArc(
                            color = if (isCompleted) SuccessGreen else primaryColor,
                            startAngle = -90f,
                            sweepAngle = progress * 360f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // Decorative Bead Dots around perimeter (33 beads visual)
                        val totalDots = 33
                        for (i in 0 until totalDots) {
                            val angleRad = Math.toRadians((i * (360.0 / totalDots) - 90.0))
                            val dotRadius = radius + 6.dp.toPx()
                            val dotX = center.x + dotRadius * cos(angleRad).toFloat()
                            val dotY = center.y + dotRadius * sin(angleRad).toFloat()
                            val isReached = (i.toFloat() / totalDots.toFloat()) <= progress

                            drawCircle(
                                color = if (isReached) GoldAccent else trackColor.copy(alpha = 0.8f),
                                radius = if (isReached) 3.5.dp.toPx() else 2.dp.toPx(),
                                center = Offset(dotX, dotY)
                            )
                        }
                    }

                    // Inside Tap Content (Counts + Prompt)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "$currentCount",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 52.sp,
                                color = if (isCompleted) SuccessGreen else primaryColor
                            )
                        )
                        Text(
                            text = "من $targetCount",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "اضغط للتسبيح 👆",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Target Goals Selector (33, 100, 300, 1000, مخصص) + Reset Action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "الهدف:",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(33, 100, 300, 1000).forEach { target ->
                    val isTargetSelected = targetCount == target
                    Surface(
                        onClick = { onSetTarget(target) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isTargetSelected) primaryColor else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.testTag("target_goal_$target")
                    ) {
                        Text(
                            text = "$target",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isTargetSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isTargetSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                // Custom Target Button
                Surface(
                    onClick = { showCustomTargetDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    color = if (listOf(33, 100, 300, 1000).none { it == targetCount }) primaryColor else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.testTag("target_goal_custom")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "هدف مخصص",
                        tint = if (listOf(33, 100, 300, 1000).none { it == targetCount }) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reset Counter and Lifetime Stats Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reset Button
            Button(
                onClick = onReset,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("btn_reset_tasbeeh")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "تصفير العداد", style = MaterialTheme.typography.labelLarge)
            }

            // Total Lifetime Counter Badge
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "المجموع الكلي",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${tasbeeh.totalLifetimeCount} تسبيحة",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                    )
                }
            }
        }
    }

    // Custom Target Dialog
    if (showCustomTargetDialog) {
        AlertDialog(
            onDismissRequest = { showCustomTargetDialog = false },
            title = { Text(text = "تحديد هدف مخصص للتسبيح", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column {
                    Text(
                        text = "أدخل عدد التسبيحات المطلوب:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = customTargetInput,
                        onValueChange = { customTargetInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("الهدف") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_custom_target")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val num = customTargetInput.toIntOrNull()
                        if (num != null && num > 0) {
                            onSetTarget(num)
                            showCustomTargetDialog = false
                            customTargetInput = ""
                        }
                    },
                    modifier = Modifier.testTag("btn_confirm_custom_target")
                ) {
                    Text("حفظ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomTargetDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
