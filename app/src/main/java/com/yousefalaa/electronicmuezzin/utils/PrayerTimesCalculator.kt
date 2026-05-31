package com.yousefalaa.electronicmuezzin.utils

import java.util.Calendar
import java.util.TimeZone
import kotlin.math.*

/**
 * حساب مواقيت الصلاة بدون إنترنت
 * يعتمد على خوارزمية ISNA/مصر
 */
object PrayerTimesCalculator {

    // طرق الحساب
    enum class CalculationMethod(
        val fajrAngle: Double,
        val ishaAngle: Double,
        val ishaMinutes: Int = 0,
        val nameAr: String
    ) {
        EGYPT(19.5, 17.5, nameAr = "طريقة مصر"),
        MWL(18.0, 17.0, nameAr = "رابطة العالم الإسلامي"),
        ISNA(15.0, 15.0, nameAr = "أمريكا الشمالية"),
        MAKKAH(18.5, 0.0, ishaMinutes = 90, nameAr = "مكة المكرمة"),
        KARACHI(18.0, 18.0, nameAr = "كراتشي"),
        TEHRAN(17.7, 14.0, nameAr = "طهران"),
        JAFARI(16.0, 14.0, nameAr = "جعفري"),
    }

    // مذاهب العصر
    enum class AsrMethod(val shadow: Int, val nameAr: String) {
        STANDARD(1, "الجمهور"),   // الشافعي والمالكي والحنبلي
        HANAFI(2, "الحنفي")
    }

    data class PrayerTimes(
        val fajr: Long,
        val sunrise: Long,
        val dhuhr: Long,
        val asr: Long,
        val maghrib: Long,
        val isha: Long,
        val midnight: Long
    )

    fun calculate(
        latitude: Double,
        longitude: Double,
        timeZone: Double,
        method: CalculationMethod = CalculationMethod.EGYPT,
        asrMethod: AsrMethod = AsrMethod.STANDARD,
        calendar: Calendar = Calendar.getInstance()
    ): PrayerTimes {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val jd = julianDay(year, month, day)
        val d = jd - 2451545.0

        val g = 357.529 + 0.98560028 * d
        val q = 280.459 + 0.98564736 * d
        val L = q + 1.915 * sin(g.toRad()) + 0.020 * sin((2 * g).toRad())
        val e = 23.439 - 0.00000036 * d
        val RA = atan2(cos(e.toRad()) * sin(L.toRad()), cos(L.toRad())).toDeg() / 15
        val eqt = q / 15 - fixHour(RA)
        val decl = asin(sin(e.toRad()) * sin(L.toRad())).toDeg()

        // وقت الزوال
        val transit = 12.0 + timeZone - longitude / 15.0 - eqt

        // الشروق والغروب
        val sunriseHour = computeTime(transit, decl, latitude, -0.8333)
        val sunsetHour = 24.0 - computeTime(24.0 - transit, -decl, -latitude, -0.8333)

        // الفجر والعشاء
        val fajrHour = computeTime(transit, decl, latitude, -method.fajrAngle)
        val ishaHour = if (method.ishaMinutes > 0) {
            sunsetHour + method.ishaMinutes / 60.0
        } else {
            24.0 - computeTime(24.0 - transit, -decl, -latitude, -method.ishaAngle)
        }

        // العصر
        val asrHour = computeAsr(transit, decl, latitude, asrMethod.shadow.toDouble())

        // منتصف الليل
        val midnightHour = sunsetHour + (24.0 - sunriseHour + sunsetHour) / 2.0

        val baseMs = getDateStart(year, month, day)

        return PrayerTimes(
            fajr = baseMs + (fajrHour * 3600000).toLong(),
            sunrise = baseMs + (sunriseHour * 3600000).toLong(),
            dhuhr = baseMs + (transit * 3600000).toLong(),
            asr = baseMs + (asrHour * 3600000).toLong(),
            maghrib = baseMs + (sunsetHour * 3600000).toLong(),
            isha = baseMs + (ishaHour * 3600000).toLong(),
            midnight = baseMs + (midnightHour * 3600000).toLong()
        )
    }

    private fun computeTime(t: Double, decl: Double, lat: Double, angle: Double): Double {
        val d = acos(
            (sin(angle.toRad()) - sin(lat.toRad()) * sin(decl.toRad())) /
                    (cos(lat.toRad()) * cos(decl.toRad()))
        ).toDeg() / 15
        return t - d
    }

    private fun computeAsr(t: Double, decl: Double, lat: Double, shadow: Double): Double {
        val a = atan(1.0 / (shadow + tan(abs(lat - decl).toRad()))).toDeg()
        val d = acos(
            (sin(a.toRad()) - sin(lat.toRad()) * sin(decl.toRad())) /
                    (cos(lat.toRad()) * cos(decl.toRad()))
        ).toDeg() / 15
        return t + d
    }

    private fun julianDay(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) { y--; m += 12 }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun getDateStart(year: Int, month: Int, day: Int): Long {
        val cal = Calendar.getInstance(TimeZone.getDefault())
        cal.set(year, month - 1, day, 0, 0, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun fixHour(h: Double): Double {
        var v = h
        while (v < 0) v += 24
        while (v >= 24) v -= 24
        return v
    }

    private fun Double.toRad() = this * Math.PI / 180.0
    private fun Double.toDeg() = this * 180.0 / Math.PI
}

// حساب التقويم الهجري
object HijriCalendar {

    data class HijriDate(val day: Int, val month: Int, val year: Int) {
        fun toArabicString(): String {
            val months = listOf(
                "محرم", "صفر", "ربيع الأول", "ربيع الثاني",
                "جمادى الأولى", "جمادى الثانية", "رجب", "شعبان",
                "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
            )
            return "$day ${months[month - 1]} $year هـ"
        }

        fun isRamadan() = month == 9
    }

    fun fromGregorian(year: Int, month: Int, day: Int): HijriDate {
        var y = year
        var m = month
        var d = day

        if (m < 3) { y -= 1; m += 12 }
        val a = (y / 100).toInt()
        val b = 2 - a + (a / 4).toInt()
        var jd = (365.25 * (y + 4716)).toInt() + (30.6001 * (m + 1)).toInt() + d + b - 1524

        // تحويل إلى هجري
        var l = jd - 1948440 + 10632
        val n = ((l - 1) / 10631).toInt()
        l -= 10631 * n + 354
        val j = (((10985 - l) / 5316).toInt()) * ((50 * l) / 17719).toInt() +
                ((l / 5670).toInt()) * ((43 * l) / 15238).toInt()
        l -= (((30 - j) / 15).toInt()) * ((17719 * j) / 50).toInt() +
                ((j / 16).toInt()) * ((15238 * j) / 43).toInt()
        val hYear = 30 * n + j - 30
        val hMonth = ((l - 29) / 29.5).toInt() + 1
        val hDay = l - ((29.5 * (hMonth - 1)).toInt())

        return HijriDate(hDay.coerceAtLeast(1), hMonth.coerceIn(1, 12), hYear)
    }

    fun today(): HijriDate {
        val cal = Calendar.getInstance()
        return fromGregorian(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }
}

// حساب اتجاه القبلة
object QiblaCalculator {
    // إحداثيات الكعبة المشرفة
    private const val KAABA_LAT = 21.4225
    private const val KAABA_LNG = 39.8262

    fun getQiblaDirection(lat: Double, lng: Double): Double {
        val latRad = Math.toRadians(lat)
        val lngRad = Math.toRadians(lng)
        val kaabaLatRad = Math.toRadians(KAABA_LAT)
        val kaabaLngRad = Math.toRadians(KAABA_LNG)

        val dLng = kaabaLngRad - lngRad
        val y = sin(dLng) * cos(kaabaLatRad)
        val x = cos(latRad) * sin(kaabaLatRad) - sin(latRad) * cos(kaabaLatRad) * cos(dLng)
        var bearing = Math.toDegrees(atan2(y, x))
        if (bearing < 0) bearing += 360
        return bearing
    }

    fun getDistanceToMakkah(lat: Double, lng: Double): Double {
        val R = 6371.0
        val lat1 = Math.toRadians(lat)
        val lat2 = Math.toRadians(KAABA_LAT)
        val dLat = Math.toRadians(KAABA_LAT - lat)
        val dLng = Math.toRadians(KAABA_LNG - lng)
        val a = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLng / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }
}
