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

    /**
     * قائمة أصوات الأذان الكاملة
     * المفتاح = اسم الملف في res/raw (بدون امتداد)
     * القيمة = الاسم العربي الذي يظهر للمستخدم
     */
    fun getAzanSounds(): LinkedHashMap<String, String> = linkedMapOf(
        // ─── الحرمين الشريفين ───
        "azan_makkah_sudais"        to "مكة المكرمة - الشيخ السديس",
        "azan_makkah_shuraim"       to "مكة المكرمة - الشيخ الشريم",
        "azan_makkah_ali_mullah"    to "مكة المكرمة - الشيخ علي ملا",
        "azan_makkah_ajmi"          to "مكة المكرمة - الشيخ العجمي",
        "azan_makkah_ghamdi"        to "مكة المكرمة - الشيخ الغامدي",
        "azan_madinah_budayr"       to "المدينة المنورة - الشيخ البديّر",
        "azan_madinah_qahtani"      to "المدينة المنورة - الشيخ القحطاني",
        // ─── أذان الفجر ───
        "azan_fajr_makkah"          to "أذان الفجر - مكة المكرمة",
        "azan_fajr_madinah"         to "أذان الفجر - المدينة المنورة",
        "azan_fajr_classic"         to "أذان الفجر - كلاسيكي",
        // ─── مؤذنون مشهورون ───
        "azan_abdulbasit"           to "عبد الباسط عبد الصمد",
        "azan_minshawi"             to "محمد صديق المنشاوي",
        "azan_husary"               to "محمود خليل الحصري",
        "azan_tablawi"              to "محمد الطبلاوي",
        "azan_ajmy"                 to "أحمد العجمي",
        "azan_afasy"                to "مشاري راشد العفاسي",
        // ─── أذانات دول عربية ───
        "azan_egypt_classic"        to "أذان مصري - كلاسيكي",
        "azan_egypt_radio"          to "أذان مصري - إذاعة القرآن",
        "azan_turkey"               to "أذان تركي",
        "azan_iraq"                 to "أذان عراقي",
        "azan_morocco"              to "أذان مغربي",
        "azan_levant"               to "أذان شامي",
        // ─── أذانات قصيرة وبسيطة ───
        "azan_short"                to "أذان قصير",
        "azan_simple"               to "أذان بسيط",
        // ─── تنبيه فقط ───
        "notification_only"         to "🔔 تنبيه بدون أذان"
    )

    /** الأصوات الافتراضية لكل صلاة */
    val defaultSounds = mapOf(
        "الفجر"   to "azan_fajr_makkah",
        "الظهر"   to "azan_makkah_sudais",
        "العصر"   to "azan_makkah_sudais",
        "المغرب"  to "azan_makkah_sudais",
        "العشاء"  to "azan_makkah_sudais"
    )

    /** تجميع الأصوات في مجموعات للعرض في الإعدادات */
    fun getGroupedSounds(): Map<String, List<Pair<String, String>>> = mapOf(
        "الحرمين الشريفين" to listOf(
            "azan_makkah_sudais"     to "مكة - السديس",
            "azan_makkah_shuraim"   to "مكة - الشريم",
            "azan_makkah_ali_mullah" to "مكة - علي ملا",
            "azan_makkah_ajmi"      to "مكة - العجمي",
            "azan_makkah_ghamdi"    to "مكة - الغامدي",
            "azan_madinah_budayr"   to "المدينة - البديّر",
            "azan_madinah_qahtani"  to "المدينة - القحطاني"
        ),
        "أذان الفجر" to listOf(
            "azan_fajr_makkah"   to "فجر مكة",
            "azan_fajr_madinah"  to "فجر المدينة",
            "azan_fajr_classic"  to "فجر كلاسيكي"
        ),
        "مؤذنون مشهورون" to listOf(
            "azan_abdulbasit" to "عبد الباسط",
            "azan_minshawi"   to "المنشاوي",
            "azan_husary"     to "الحصري",
            "azan_tablawi"    to "الطبلاوي",
            "azan_ajmy"       to "أحمد العجمي",
            "azan_afasy"      to "العفاسي"
        ),
        "أذانات عربية" to listOf(
            "azan_egypt_classic" to "مصري كلاسيكي",
            "azan_egypt_radio"   to "إذاعة القرآن",
            "azan_turkey"        to "تركي",
            "azan_iraq"          to "عراقي",
            "azan_morocco"       to "مغربي",
            "azan_levant"        to "شامي"
        ),
        "أخرى" to listOf(
            "azan_short"        to "أذان قصير",
            "azan_simple"       to "بسيط",
            "notification_only" to "🔔 تنبيه فقط"
        )
    )
}
