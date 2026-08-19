package com.example

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.LayoutDirection
import com.example.data.datasource.AzkarDataSource
import com.example.ui.components.DhikrCard
import com.example.ui.theme.AzkarMuslimTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class GreetingScreenshotTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun dhikr_card_screenshot() {
        val sampleDhikr = AzkarDataSource.dhikrList.first()

        composeTestRule.setContent {
            AzkarMuslimTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    DhikrCard(
                        dhikr = sampleDhikr,
                        currentCount = 1,
                        isFavorite = true,
                        isPlayingAudio = false,
                        fontSizeScale = 1.0f,
                        onIncrement = {},
                        onReset = {},
                        onToggleFavorite = {},
                        onCopy = {},
                        onShare = {},
                        onPlayAudio = {}
                    )
                }
            }
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/dhikr_card.png")
    }
}
