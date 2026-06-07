package com.yousefalaa.electronicmuezzin.data.models

data class AzanSound(
    val key  : String,
    val label: String,
    val url  : String? = null,
    val group: String  = ""
)

object AzanSoundCatalog {
    val all: List<AzanSound> = listOf(
        // ── موجودة في res/raw ─────────────────────────────
        AzanSound("azan_makkah",  "أذان مكة المكرمة",     null, "الحرمين الشريفين"),
        AzanSound("azan_madinah", "أذان المدينة المنورة", null, "الحرمين الشريفين"),
        AzanSound("azan_fajr",    "أذان الفجر",           null, "أذان الفجر"),
        AzanSound("azan_egypt",   "أذان مصري",            null, "أذانات عربية"),
        AzanSound("azan_short",   "أذان قصير",            null, "أخرى"),
        // ── للتحميل ───────────────────────────────────────
        AzanSound("azan_sudais",   "عبدالرحمن السديس",
            "https://download.quranicaudio.com/quran/abdurrahmaan_as-sudais_and_su_ud_ash-shuraim/001.mp3",
            "الحرمين الشريفين"),
        AzanSound("azan_shuraim",  "سعود الشريم",
            "https://islamcan.com/audio/adhan/azan2.mp3", "الحرمين الشريفين"),
        AzanSound("azan_basit",    "عبدالباسط عبدالصمد",
            "https://islamcan.com/audio/adhan/azan4.mp3", "مؤذنون مشهورون"),
        AzanSound("azan_minshawi", "محمد صديق المنشاوي",
            "https://islamcan.com/audio/adhan/azan5.mp3", "مؤذنون مشهورون"),
        AzanSound("silent_mode",   "الوضع الصامت", null, "أخرى")
    )

    fun getByKey(key: String) = all.find { it.key == key }

    // الأصوات الافتراضية التي تتطابق مع الملفات الموجودة فعلاً
    const val DEFAULT_SOUND     = "azan_makkah"
    const val DEFAULT_FAJR      = "azan_fajr"
    const val DEFAULT_MADINAH   = "azan_madinah"
}

object SalahSoundCatalog {
    val all: List<AzanSound> = listOf(
        AzanSound("",              "الظهور علي الشاشة بدون صوت", null, ""),
        AzanSound("azan_short",    "صوت قصير (محلي)",  null, ""),
        AzanSound("salah_salli",   "صلِّ علي محمد",
            "https://islamcan.com/audio/salah/salah1.mp3", ""),
        AzanSound("salah_allahumma","اللهم صلِّ وسلم علي نبينا محمد",
            "https://islamcan.com/audio/salah/salah2.mp3", ""),
        AzanSound("salah_ibrahim", "اللهم صلِّ - افضل الصلاة والتسليم",
            "https://islamcan.com/audio/salah/salah3.mp3", "علية افضل الصلاة والتسليم"),
        AzanSound("salah_innallah","إنَّ اللهَ وملئكتهُ يُصَلُّون علَى النَّبِيّ",
            "https://islamcan.com/audio/salah/salah6.mp3", "علية افضل الصلاة والتسليم"),
        AzanSound("salah_reminder","نذكركم بالصلاة علي الحبيب",
            "https://islamcan.com/audio/salah/salah7.mp3", "")
    )
}
