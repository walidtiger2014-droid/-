package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.datasource.AzkarDataSource
import com.example.util.HijriCalendarUtil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `verify app name resource`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("أذكار المسلم", appName)
    }

    @Test
    fun `verify azkar categories and dhikr dataset`() {
        val categories = AzkarDataSource.categories
        assertEquals(20, categories.size)

        val allDhikrs = AzkarDataSource.dhikrList
        assertTrue(allDhikrs.size >= 100)

        val morningDhikrs = allDhikrs.filter { it.categoryId == 1 }
        assertEquals(22, morningDhikrs.size)

        val eveningDhikrs = allDhikrs.filter { it.categoryId == 2 }
        assertEquals(21, eveningDhikrs.size)
    }

    @Test
    fun `verify daily dhikr generation`() {
        val dailyDhikr = AzkarDataSource.getDailyDhikr()
        assertNotNull(dailyDhikr)
        assertTrue(dailyDhikr.text.isNotBlank())
    }

    @Test
    fun `verify hijri calendar conversion`() {
        val hijriDate = HijriCalendarUtil.getTodayHijriDate()
        assertNotNull(hijriDate)
        assertTrue(hijriDate.day in 1..30)
        assertTrue(hijriDate.monthNumber in 1..12)
        assertTrue(hijriDate.year >= 1445)
        assertTrue(hijriDate.monthName.isNotBlank())
        assertTrue(hijriDate.dayName.isNotBlank())
    }
}
