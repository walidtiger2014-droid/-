package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.ContextCompat
import com.example.data.model.AppThemeMode
import com.example.data.model.ColorPalette
import com.example.ui.components.AzkarBottomNav
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.DhikrListScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.RemindersScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TasbeehScreen
import com.example.ui.theme.AzkarMuslimTheme
import com.example.ui.viewmodel.AzkarViewModel
import com.example.ui.viewmodel.ScreenDestination

class MainActivity : ComponentActivity() {

    private val viewModel: AzkarViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsState()

            val currentThemeMode = try {
                AppThemeMode.valueOf(uiState.settings.themeMode)
            } catch (e: Exception) {
                AppThemeMode.SYSTEM
            }

            val currentColorPalette = try {
                ColorPalette.valueOf(uiState.settings.colorPalette)
            } catch (e: Exception) {
                ColorPalette.EMERALD
            }

            // Request Notification Permission for Android 13+
            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { /* Handled gracefully */ }
            )

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            AzkarMuslimTheme(
                themeMode = currentThemeMode,
                colorPalette = currentColorPalette
            ) {
                // Force RTL Layout Direction for full Arabic experience
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    AzkarAppContent(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AzkarAppContent(viewModel: AzkarViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle Back Press
    BackHandler(enabled = uiState.currentScreen != ScreenDestination.HOME) {
        viewModel.navigateBack()
    }

    val showBottomNav = uiState.currentScreen != ScreenDestination.SEARCH

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomNav) {
                AzkarBottomNav(
                    currentScreen = uiState.currentScreen,
                    onNavigate = { destination -> viewModel.navigateTo(destination) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = uiState.currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    ScreenDestination.HOME -> {
                        HomeScreen(
                            uiState = uiState,
                            dailyDhikr = viewModel.getDailyDhikr(),
                            categories = viewModel.categories,
                            onOpenCategory = { cat -> viewModel.openCategory(cat) },
                            onOpenCategoryById = { id -> viewModel.openCategoryById(id) },
                            onNavigate = { dest -> viewModel.navigateTo(dest) },
                            onIncrementDhikr = { dhikr -> viewModel.incrementDhikr(dhikr) },
                            onResetDhikr = { id -> viewModel.resetDhikr(id) },
                            onToggleFavorite = { id -> viewModel.toggleFavorite(id) },
                            onCopyDhikr = { dhikr -> viewModel.copyDhikr(dhikr) },
                            onShareDhikr = { dhikr -> viewModel.shareDhikr(dhikr) },
                            onPlayAudio = { dhikr -> viewModel.playAudio(dhikr) }
                        )
                    }
                    ScreenDestination.CATEGORIES -> {
                        CategoriesScreen(
                            categories = viewModel.categories,
                            onOpenCategory = { cat -> viewModel.openCategory(cat) },
                            onSearchClick = { viewModel.navigateTo(ScreenDestination.SEARCH) }
                        )
                    }
                    ScreenDestination.CATEGORY_DETAIL -> {
                        val category = uiState.selectedCategory ?: viewModel.categories.first()
                        val dhikrs = viewModel.getDhikrsForCategory(category.id)
                        DhikrListScreen(
                            category = category,
                            dhikrs = dhikrs,
                            uiState = uiState,
                            onBack = { viewModel.navigateBack() },
                            onResetCategory = { catId -> viewModel.resetCategoryDhikrs(catId) },
                            onIncrementDhikr = { dhikr -> viewModel.incrementDhikr(dhikr) },
                            onMarkDhikrCompleted = { dhikr -> viewModel.markDhikrCompleted(dhikr) },
                            onResetDhikr = { id -> viewModel.resetDhikr(id) },
                            onToggleFavorite = { id -> viewModel.toggleFavorite(id) },
                            onCopyDhikr = { dhikr -> viewModel.copyDhikr(dhikr) },
                            onShareDhikr = { dhikr -> viewModel.shareDhikr(dhikr) },
                            onPlayAudio = { dhikr -> viewModel.playAudio(dhikr) }
                        )
                    }
                    ScreenDestination.TASBEEH -> {
                        TasbeehScreen(
                            tasbeeh = uiState.tasbeeh,
                            presets = viewModel.tasbeehPresets,
                            settings = uiState.settings,
                            onIncrement = { viewModel.incrementTasbeeh() },
                            onReset = { viewModel.resetTasbeeh() },
                            onSelectPreset = { preset -> viewModel.selectTasbeehPreset(preset) },
                            onSetTarget = { target -> viewModel.setTasbeehTarget(target) },
                            onToggleSound = { enabled -> viewModel.toggleSound(enabled) },
                            onToggleHaptic = { enabled -> viewModel.toggleHaptics(enabled) }
                        )
                    }
                    ScreenDestination.FAVORITES -> {
                        FavoritesScreen(
                            favoriteDhikrs = viewModel.getFavoriteDhikrs(),
                            uiState = uiState,
                            onNavigateToCategories = { viewModel.navigateTo(ScreenDestination.CATEGORIES) },
                            onIncrementDhikr = { dhikr -> viewModel.incrementDhikr(dhikr) },
                            onResetDhikr = { id -> viewModel.resetDhikr(id) },
                            onToggleFavorite = { id -> viewModel.toggleFavorite(id) },
                            onCopyDhikr = { dhikr -> viewModel.copyDhikr(dhikr) },
                            onShareDhikr = { dhikr -> viewModel.shareDhikr(dhikr) },
                            onPlayAudio = { dhikr -> viewModel.playAudio(dhikr) }
                        )
                    }
                    ScreenDestination.SEARCH -> {
                        SearchScreen(
                            searchQuery = uiState.searchQuery,
                            searchResults = uiState.searchResults,
                            uiState = uiState,
                            onQueryChange = { q -> viewModel.onSearchQueryChanged(q) },
                            onClearQuery = { viewModel.clearSearch() },
                            onBack = { viewModel.navigateBack() },
                            onIncrementDhikr = { dhikr -> viewModel.incrementDhikr(dhikr) },
                            onResetDhikr = { id -> viewModel.resetDhikr(id) },
                            onToggleFavorite = { id -> viewModel.toggleFavorite(id) },
                            onCopyDhikr = { dhikr -> viewModel.copyDhikr(dhikr) },
                            onShareDhikr = { dhikr -> viewModel.shareDhikr(dhikr) },
                            onPlayAudio = { dhikr -> viewModel.playAudio(dhikr) }
                        )
                    }
                    ScreenDestination.REMINDERS -> {
                        RemindersScreen(
                            reminders = uiState.reminders,
                            onToggleReminder = { id, enabled -> viewModel.toggleReminder(id, enabled) },
                            onUpdateTime = { id, hour, min -> viewModel.updateReminderTime(id, hour, min) },
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                    ScreenDestination.SETTINGS -> {
                        SettingsScreen(
                            settings = uiState.settings,
                            onUpdateTheme = { mode -> viewModel.updateThemeMode(mode) },
                            onUpdatePalette = { pal -> viewModel.updateColorPalette(pal) },
                            onUpdateFontSize = { scale -> viewModel.updateFontSize(scale) },
                            onToggleHaptic = { enabled -> viewModel.toggleHaptics(enabled) },
                            onToggleSound = { enabled -> viewModel.toggleSound(enabled) },
                            onNavigate = { dest -> viewModel.navigateTo(dest) },
                            onShareApp = { viewModel.shareApp() },
                            onResetAllData = { viewModel.resetAllAppSettings() }
                        )
                    }
                }
            }
        }
    }
}
