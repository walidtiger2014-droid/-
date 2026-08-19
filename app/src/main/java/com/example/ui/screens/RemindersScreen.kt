package com.example.ui.screens

import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ReminderEntity
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    reminders: List<ReminderEntity>,
    onToggleReminder: (String, Boolean) -> Unit,
    onUpdateTime: (String, Int, Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
            .testTag("reminders_screen")
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "التنبيهات والإشعارات",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("btn_back_from_reminders")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "رجوع"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        // Intro Note
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "تذكيرك بأوقات الأذكار يضمن استمرارك ومداومتك على الورد اليومي دون انقطاع بإذن الله.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Reminder Cards
        val morningReminder = reminders.find { it.id == "morning" } ?: ReminderEntity("morning", true, 7, 0)
        val eveningReminder = reminders.find { it.id == "evening" } ?: ReminderEntity("evening", true, 18, 0)
        val sleepReminder = reminders.find { it.id == "sleep" } ?: ReminderEntity("sleep", false, 22, 30)
        val randomReminder = reminders.find { it.id == "random" } ?: ReminderEntity("random", false, 14, 0)

        ReminderItemCard(
            title = "أذكار الصباح",
            description = "تنبيه يومي مع إشراقة الصباح",
            icon = Icons.Default.WbSunny,
            iconTint = Color(0xFFF59E0B),
            reminder = morningReminder,
            onToggle = { isChecked -> onToggleReminder("morning", isChecked) },
            onPickTime = {
                val dialog = TimePickerDialog(
                    context,
                    { _, hour, minute -> onUpdateTime("morning", hour, minute) },
                    morningReminder.hour,
                    morningReminder.minute,
                    false
                )
                dialog.show()
            },
            testTag = "reminder_morning"
        )

        ReminderItemCard(
            title = "أذكار المساء",
            description = "تنبيه يومي قبل غروب الشمس",
            icon = Icons.Default.Star,
            iconTint = Color(0xFF6366F1),
            reminder = eveningReminder,
            onToggle = { isChecked -> onToggleReminder("evening", isChecked) },
            onPickTime = {
                val dialog = TimePickerDialog(
                    context,
                    { _, hour, minute -> onUpdateTime("evening", hour, minute) },
                    eveningReminder.hour,
                    eveningReminder.minute,
                    false
                )
                dialog.show()
            },
            testTag = "reminder_evening"
        )

        ReminderItemCard(
            title = "أذكار النوم",
            description = "تنبيه بحصن المسلم قبل المنام",
            icon = Icons.Default.Bedtime,
            iconTint = Color(0xFF8B5CF6),
            reminder = sleepReminder,
            onToggle = { isChecked -> onToggleReminder("sleep", isChecked) },
            onPickTime = {
                val dialog = TimePickerDialog(
                    context,
                    { _, hour, minute -> onUpdateTime("sleep", hour, minute) },
                    sleepReminder.hour,
                    sleepReminder.minute,
                    false
                )
                dialog.show()
            },
            testTag = "reminder_sleep"
        )

        ReminderItemCard(
            title = "تذكير عشوائي بذكر اليوم",
            description = "رسالة قصيرة ترطب لسانك بذكر الله",
            icon = Icons.Default.NotificationsActive,
            iconTint = Color(0xFF10B981),
            reminder = randomReminder,
            onToggle = { isChecked -> onToggleReminder("random", isChecked) },
            onPickTime = {
                val dialog = TimePickerDialog(
                    context,
                    { _, hour, minute -> onUpdateTime("random", hour, minute) },
                    randomReminder.hour,
                    randomReminder.minute,
                    false
                )
                dialog.show()
            },
            testTag = "reminder_random"
        )
    }
}

@Composable
private fun ReminderItemCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconTint: Color,
    reminder: ReminderEntity,
    onToggle: (Boolean) -> Unit,
    onPickTime: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val formattedTime = formatArabicTime(reminder.hour, reminder.minute)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = iconTint.copy(alpha = 0.15f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Time badge
                    Surface(
                        onClick = onPickTime,
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable { onPickTime() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "تعديل الوقت",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = formattedTime,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Switch(
                checked = reminder.isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.testTag("switch_${testTag}")
            )
        }
    }
}

private fun formatArabicTime(hour: Int, minute: Int): String {
    val period = if (hour < 12) "صباحاً" else "مساءً"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    val minStr = String.format(Locale.US, "%02d", minute)
    return "$displayHour:$minStr $period"
}
