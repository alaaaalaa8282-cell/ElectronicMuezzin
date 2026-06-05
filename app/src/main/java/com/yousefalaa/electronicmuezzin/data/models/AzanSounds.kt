package com.yousefalaa.electronicmuezzin.data.models

/**
 * كل صوت فيه:
 * - key   : اسم الملف (في res/raw أو filesDir)
 * - label : الاسم الظاهر للمستخدم
 * - url   : رابط التحميل (null = موجود محلياً)
 * - group : المجموعة
 */
data class AzanSound(
    val key  : String,
    val label: String,
    val url  : String? = null,
    val group: String  = ""
)

object AzanSoundCatalog {

    val all: List<AzanSound> = listOf(
        // ── موجودة محلياً في res/raw ─────────────────────
        AzanSound("azan_makkah",   "أذان مكة المكرمة",          null,  "الحرمين"),
        AzanSound("azan_fajr",     "أذان الفجر",                null,  "أذان الفجر"),
        AzanSound("azan_madinah",  "أذان المدينة المنورة",      null,  "الحرمين"),
        AzanSound("azan_egypt",    "أذان مصري",                 null,  "أذانات عربية"),
        AzanSound("azan_short",    "أذان قصير",                 null,  "أخرى"),

        // ── للتحميل ─────────────────────────────────────
        AzanSound("azan_sudais",   "عبدالرحمن السديس",
            "https://www.islamcan.com/audio/adhan/azan1.mp3", "الحرمين"),
        AzanSound("azan_shuraim",  "سعود الشريم",
            "https://www.islamcan.com/audio/adhan/azan2.mp3", "الحرمين"),
        AzanSound("azan_budayr",   "ياسر الدوسري - المدينة",
            "https://www.islamcan.com/audio/adhan/azan3.mp3", "الحرمين"),
        AzanSound("azan_basit",    "عبدالباسط عبدالصمد",
            "https://www.islamcan.com/audio/adhan/azan4.mp3", "مؤذنون"),
        AzanSound("azan_minshawi", "محمد صديق المنشاوي",
            "https://www.islamcan.com/audio/adhan/azan5.mp3", "مؤذنون"),
        AzanSound("azan_husary",   "محمود خليل الحصري",
            "https://www.islamcan.com/audio/adhan/azan7.mp3", "مؤذنون"),
        AzanSound("azan_tablawi",  "محمد الطبلاوي",
            "https://www.islamcan.com/audio/adhan/azan8.mp3", "مؤذنون"),
        AzanSound("silent_mode",   "الوضع الصامت",              null,  "أخرى")
    )

    fun getGrouped(): Map<String, List<AzanSound>> = all.groupBy { it.group }

    fun getByKey(key: String) = all.find { it.key == key }
}

object SalahSoundCatalog {

    val all: List<AzanSound> = listOf(
        AzanSound("",              "الظهور علي الشاشة بدون صوت", null, ""),
        AzanSound("salah_salli",   "صلِّ علي محمد",
            "https://www.islamcan.com/audio/salah/salah1.mp3", ""),
        AzanSound("salah_allahumma","اللهم صلِّ وسلم علي نبينا محمد",
            "https://www.islamcan.com/audio/salah/salah2.mp3", ""),
        AzanSound("salah_ibrahim", "اللهم صلِّ وسلم - علية افضل الصلاة والتسليم",
            "https://www.islamcan.com/audio/salah/salah3.mp3", ""),
        AzanSound("salah_aal",     "اللهم صلِّ علي محمد و ال محمد",
            "https://www.islamcan.com/audio/salah/salah4.mp3", ""),
        AzanSound("salah_rassoul", "الصلاة والسلام عليك يا رسول الله",
            "https://www.islamcan.com/audio/salah/salah5.mp3", ""),
        AzanSound("salah_innallah","إنَّ اللهَ وملئكتهُ يُصَلُّون علَى النَّبِيّ",
            "https://www.islamcan.com/audio/salah/salah6.mp3", "علية افضل الصلاة والتسليم"),
        AzanSound("salah_reminder","نذكركم بالصلاة علي الحبيب",
            "https://www.islamcan.com/audio/salah/salah7.mp3", "")
    )
}

