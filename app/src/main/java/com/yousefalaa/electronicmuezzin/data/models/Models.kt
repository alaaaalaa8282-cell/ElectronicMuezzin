package com.yousefalaa.electronicmuezzin.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

// ========================
// موديل مواقيت الصلاة
// ========================
data class PrayerTimesModel(
    val fajr: Long,
    val sunrise: Long,
    val dhuhr: Long,
    val asr: Long,
    val maghrib: Long,
    val isha: Long,
    val midnight: Long,
    val date: String = ""
) {
    fun toList(): List<Pair<String, Long>> = listOf(
        "الفجر" to fajr,
        "الشروق" to sunrise,
        "الظهر" to dhuhr,
        "العصر" to asr,
        "المغرب" to maghrib,
        "العشاء" to isha
    )

    fun getNextPrayer(): Pair<String, Long>? {
        val now = System.currentTimeMillis()
        return toList().firstOrNull { it.second > now }
    }
}

// ========================
// إعدادات الموقع والحساب
// ========================
data class PrayerSettings(
    val calculationMethod: String = "EGYPT",
    val asrMethod: String = "STANDARD",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val cityName: String = "",
    val timeZoneOffset: Double = 2.0,
    val language: String = "ar",
    val summerTimeOffset: Int = 0  // التوقيت الصيفي بالدقائق
)

// ========================
// إعدادات الأذان الكاملة
// ========================
data class PrayerAzanSettings(
    // أصوات الأذان
    val fajrSound: String = "azan_fajr_makkah",
    val dhuhrSound: String = "azan_makkah_sudais",
    val asrSound: String = "azan_makkah_sudais",
    val maghribSound: String = "azan_makkah_sudais",
    val ishaSound: String = "azan_makkah_sudais",
    // تفعيل الأذان
    val fajrEnabled: Boolean = true,
    val dhuhrEnabled: Boolean = true,
    val asrEnabled: Boolean = true,
    val maghribEnabled: Boolean = true,
    val ishaEnabled: Boolean = true,
    // تعديل المواقيت بالدقائق (+/-)
    val fajrOffset: Int = 0,
    val sunriseOffset: Int = 0,
    val dhuhrOffset: Int = 0,
    val asrOffset: Int = 0,
    val maghribOffset: Int = 0,
    val ishaOffset: Int = 0,
    // مواقيت الإقامة (بعد الأذان بكم دقيقة)
    val fajrIqama: Int = 20,
    val dhuhrIqama: Int = 10,
    val asrIqama: Int = 10,
    val maghribIqama: Int = 5,
    val ishaIqama: Int = 15,
    // تفعيل تنبيه اقتراب الصلاة
    val fajrApproachEnabled: Boolean = true,
    val dhuhrApproachEnabled: Boolean = true,
    val asrApproachEnabled: Boolean = true,
    val maghribApproachEnabled: Boolean = true,
    val ishaApproachEnabled: Boolean = true,
    // دقائق اقتراب الصلاة
    val fajrApproachMinutes: Int = 20,
    val sunriseApproachMinutes: Int = 10,
    val dhuhrApproachMinutes: Int = 10,
    val asrApproachMinutes: Int = 10,
    val maghribApproachMinutes: Int = 15,
    val ishaApproachMinutes: Int = 15,
    // صوت اقتراب الصلاة
    val fajrApproachSound: String = "approach_fajr",
    val dhuhrApproachSound: String = "approach_prayer",
    val asrApproachSound: String = "approach_prayer",
    val maghribApproachSound: String = "approach_prayer",
    val ishaApproachSound: String = "approach_prayer",
    // إعدادات شاشة الأذان
    val showAzanScreen: Boolean = true,
    val autoStopAzan: Boolean = false,
    val autoStopMinutes: Int = 5,
    // إشعار تنبيه
    val fajrNotification: Boolean = true,
    val dhuhrNotification: Boolean = true,
    val asrNotification: Boolean = true,
    val maghribNotification: Boolean = true,
    val ishaNotification: Boolean = true
)

// ========================
// إعدادات رمضان
// ========================
data class RamadanSettings(
    val suhoorAlarmEnabled: Boolean = true,
    val iftarAlarmEnabled: Boolean = true,
    val suhoorMinutesBefore: Int = 30,
    val showCountdown: Boolean = true,
    val iftarCannonEnabled: Boolean = true,
    val iftarDuaEnabled: Boolean = true,
    val silentDuringTaraweh: Boolean = false,
    val tarawehSilentMinutes: Int = 60,
    val khatmatCount: Int = 1,
    val startFromFatiha: Boolean = true,
    val quranBeforeMaghrib: Boolean = false
)

// ========================
// الذكر
// ========================
@Entity(tableName = "adhkar")
data class Dhikr(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    val count: Int = 1,
    val category: DhikrCategory,
    val reference: String = "",
    val benefit: String = ""
)

enum class DhikrCategory(val nameAr: String) {
    MORNING("أذكار الصباح"),
    EVENING("أذكار المساء"),
    AFTER_PRAYER("أذكار بعد الصلاة"),
    SLEEP("أذكار النوم"),
    WAKE("أذكار الاستيقاظ"),
    EATING("أذكار الطعام"),
    QURAN("أذكار قرآنية"),
    GENERAL("أذكار عامة")
}

// ========================
// ختم القرآن
// ========================
@Entity(tableName = "quran_khatma")
data class QuranKhatma(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val startDate: Long = System.currentTimeMillis(),
    val targetDays: Int = 30,
    val currentJuz: Int = 1,
    val currentPage: Int = 1,
    val isCompleted: Boolean = false,
    val completedDate: Long? = null
) {
    val progressPercent: Float get() = (currentPage / 604f) * 100f
}

// ========================
// أسماء الصلوات والأصوات
// ========================
object PrayerNames {
    val arabic = listOf("الفجر", "الشروق", "الظهر", "العصر", "المغرب", "العشاء")
    val emojis = listOf("🌙", "🌅", "☀️", "🌤️", "🌇", "🌙")

    fun getAzanSounds(): LinkedHashMap<String, String> = linkedMapOf(
        "azan_makkah_sudais"     to "علي بن أحمد الملا - مكة",
        "azan_makkah_shuraim"    to "سعود الشريم - مكة",
        "azan_makkah_maher"      to "ماهر المعيقلي - مكة",
        "azan_makkah_sudais2"    to "عبدالرحمن السديس - مكة",
        "azan_madinah_budayr"    to "ياسر الدوسري - المدينة",
        "azan_madinah_qahtani"   to "علي الحذيفي - المدينة",
        "azan_fajr_makkah"       to "أذان الفجر - مكة المكرمة",
        "azan_fajr_madinah"      to "أذان الفجر - المدينة المنورة",
        "azan_abdulbasit"        to "عبدالباسط عبدالصمد",
        "azan_minshawi"          to "محمد صديق المنشاوي",
        "azan_husary"            to "محمود خليل الحصري",
        "azan_tablawi"           to "محمد الطبلاوي",
        "azan_egypt_classic"     to "أذان مصري كلاسيكي",
        "azan_egypt_radio"       to "إذاعة القرآن الكريم",
        "azan_short"             to "أذان قصير",
        "silent_mode"            to "الوضع الصامت"
    )

    fun getGroupedSounds(): Map<String, List<Pair<String, String>>> = mapOf(
        "الحرمين الشريفين" to listOf(
            "azan_makkah_sudais"  to "علي بن أحمد الملا",
            "azan_makkah_shuraim" to "سعود الشريم",
            "azan_makkah_maher"   to "ماهر المعيقلي",
            "azan_makkah_sudais2" to "عبدالرحمن السديس",
            "azan_madinah_budayr" to "ياسر الدوسري",
            "azan_madinah_qahtani" to "علي الحذيفي"
        ),
        "أذان الفجر" to listOf(
            "azan_fajr_makkah"   to "فجر مكة",
            "azan_fajr_madinah"  to "فجر المدينة"
        ),
        "مؤذنون مشهورون" to listOf(
            "azan_abdulbasit" to "عبدالباسط عبدالصمد",
            "azan_minshawi"   to "محمد صديق المنشاوي",
            "azan_husary"     to "محمود خليل الحصري",
            "azan_tablawi"    to "محمد الطبلاوي"
        ),
        "أذانات عربية" to listOf(
            "azan_egypt_classic" to "مصري كلاسيكي",
            "azan_egypt_radio"   to "إذاعة القرآن"
        ),
        "أخرى" to listOf(
            "azan_short"  to "أذان قصير",
            "silent_mode" to "الوضع الصامت"
        )
    )
}
