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

    fun getCurrentOrNextPrayer(): Triple<String, Long, Boolean> {
        val now = System.currentTimeMillis()
        val prayers = toList()
        for (i in prayers.indices) {
            if (prayers[i].second > now) {
                return Triple(prayers[i].first, prayers[i].second, false)
            }
        }
        return Triple(prayers[0].first, prayers[0].second + 86400000L, false)
    }
}

// ========================
// إعدادات الصلاة
// ========================
data class PrayerSettings(
    val calculationMethod: String = "EGYPT",
    val asrMethod: String = "STANDARD",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val cityName: String = "",
    val timeZoneOffset: Double = 2.0,
    val language: String = "ar"
)

// ========================
// إعدادات الأذان لكل صلاة
// ========================
data class PrayerAzanSettings(
    val fajrSound: String = "azan_fajr",
    val dhuhrSound: String = "azan_makkah",
    val asrSound: String = "azan_makkah",
    val maghribSound: String = "azan_makkah",
    val ishaSound: String = "azan_makkah",
    val fajrEnabled: Boolean = true,
    val dhuhrEnabled: Boolean = true,
    val asrEnabled: Boolean = true,
    val maghribEnabled: Boolean = true,
    val ishaEnabled: Boolean = true,
    val fajrNotification: Boolean = true,
    val dhuhrNotification: Boolean = true,
    val asrNotification: Boolean = true,
    val maghribNotification: Boolean = true,
    val ishaNotification: Boolean = true
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
// تسبيح
// ========================
data class TasbihSession(
    val dhikrText: String,
    val target: Int = 33,
    val current: Int = 0
)

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
// رمضان
// ========================
data class RamadanSettings(
    val suhoorAlarmEnabled: Boolean = true,
    val iftarAlarmEnabled: Boolean = true,
    val suhoorMinutesBefore: Int = 30,
    val showCountdown: Boolean = true
)

// ========================
// أسماء الصلوات
// ========================
object PrayerNames {
    val arabic = listOf("الفجر", "الشروق", "الظهر", "العصر", "المغرب", "العشاء")
    val icons = listOf("🌙", "☀️", "☀️", "🌤️", "🌅", "🌙")

    fun getAzanSounds() = mapOf(
        "azan_fajr" to "أذان الفجر",
        "azan_makkah" to "أذان مكة المكرمة",
        "azan_madinah" to "أذان المدينة المنورة",
        "azan_egypt" to "أذان مصري",
        "azan_short" to "أذان قصير",
        "notification_only" to "تنبيه فقط"
    )
}
