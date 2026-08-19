package com.example.util

import java.util.Calendar
import java.util.Locale
import kotlin.math.floor

object HijriCalendarUtil {

    private val hijriMonths = listOf(
        "محرّم", "صفر", "ربيع الأول", "ربيع الآخر",
        "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
        "رمضان", "شوّال", "ذو القعدة", "ذو الحجة"
    )

    private val gregorianMonths = listOf(
        "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
        "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
    )

    private val arabicDays = listOf(
        "الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت"
    )

    data class HijriDate(
        val day: Int,
        val monthNumber: Int,
        val monthName: String,
        val year: Int,
        val dayName: String,
        val gregorianFormatted: String
    )

    fun getTodayHijriDate(): HijriDate {
        val cal = Calendar.getInstance()
        val gYear = cal.get(Calendar.YEAR)
        val gMonth = cal.get(Calendar.MONTH) + 1
        val gDay = cal.get(Calendar.DAY_OF_MONTH)
        val dayOfWeekIndex = cal.get(Calendar.DAY_OF_WEEK) - 1

        val dayName = arabicDays.getOrElse(dayOfWeekIndex) { "اليوم" }
        val gMonthName = gregorianMonths.getOrElse(gMonth - 1) { "" }
        val gregorianFormatted = "$gDay $gMonthName $gYear م"

        // Kuwaiti / Julian Day algorithm conversion to Islamic Hijri
        var m = gMonth
        var y = gYear
        if (m < 3) {
            y -= 1
            m += 12
        }

        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        val jd = floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + gDay + b - 1524.5

        val z = jd + 0.5
        val i = floor(z)
        val f = z - i
        val aJd = i
        val alpha = floor((aJd - 1867216.25) / 36524.25)
        val bJd = aJd + 1 + alpha - floor(alpha / 4.0)
        val cJd = bJd + 1524
        val dJd = floor((cJd - 122.1) / 365.25)
        val eJd = floor(365.25 * dJd)
        val gJd = floor((cJd - eJd) / 30.6001)

        val l = jd - 1948440 + 10632
        val n = floor((l - 1) / 10631.0)
        val lPrime = l - 10631 * n + 354
        val j = (floor((10985 - lPrime) / 5316.0)) * (floor((50 * lPrime) / 17719.0)) + (floor(lPrime / 5670.0)) * (floor((43 * lPrime) / 15238.0))
        val lDoublePrime = lPrime - (floor((30 - j) / 15.0)) * (floor((17719 * j) / 50.0)) - (floor(j / 16.0)) * (floor((15238 * j) / 43.0)) + 29
        val hMonth = floor((24 * lDoublePrime) / 709.0).toInt()
        val hDay = (lDoublePrime - floor((709 * hMonth) / 24.0)).toInt()
        val hYear = (30 * n + j - 30).toInt()

        val safeMonth = ((hMonth - 1 + 12) % 12)
        val safeMonthName = hijriMonths.getOrElse(safeMonth) { "رمضان" }
        val safeDay = if (hDay in 1..30) hDay else 1

        return HijriDate(
            day = safeDay,
            monthNumber = safeMonth + 1,
            monthName = safeMonthName,
            year = if (hYear > 1400) hYear else 1448,
            dayName = dayName,
            gregorianFormatted = gregorianFormatted
        )
    }

    fun getTimeBasedGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 4..11 -> "صباح عامرٌ بذكر الله ☀️"
            in 12..16 -> "طاب يومك بطاعة الله 🌿"
            in 17..21 -> "مساء عامرٌ بالسكينة والطمأنينة 🌙"
            else -> "ليلة هانئة مع ذكر الله وتلاوة آياته ✨"
        }
    }
}
