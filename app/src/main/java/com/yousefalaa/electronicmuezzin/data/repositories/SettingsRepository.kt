package com.yousefalaa.electronicmuezzin.data.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.yousefalaa.electronicmuezzin.data.models.PrayerAzanSettings
import com.yousefalaa.electronicmuezzin.data.models.PrayerSettings
import com.yousefalaa.electronicmuezzin.data.models.RamadanSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "muezzin_settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    private object Keys {
        // الموقع والحساب
        val CALC_METHOD  = stringPreferencesKey("calc_method")
        val ASR_METHOD   = stringPreferencesKey("asr_method")
        val LATITUDE     = doublePreferencesKey("latitude")
        val LONGITUDE    = doublePreferencesKey("longitude")
        val CITY_NAME    = stringPreferencesKey("city_name")
        val TIMEZONE     = doublePreferencesKey("timezone")
        val SUMMER_OFFSET = intPreferencesKey("summer_offset")
        val LOCATION_SET = booleanPreferencesKey("location_set")

        // أصوات الأذان
        val FAJR_SOUND    = stringPreferencesKey("fajr_sound")
        val DHUHR_SOUND   = stringPreferencesKey("dhuhr_sound")
        val ASR_SOUND     = stringPreferencesKey("asr_sound")
        val MAGHRIB_SOUND = stringPreferencesKey("maghrib_sound")
        val ISHA_SOUND    = stringPreferencesKey("isha_sound")

        // تفعيل الأذان
        val FAJR_ENABLED    = booleanPreferencesKey("fajr_enabled")
        val DHUHR_ENABLED   = booleanPreferencesKey("dhuhr_enabled")
        val ASR_ENABLED     = booleanPreferencesKey("asr_enabled")
        val MAGHRIB_ENABLED = booleanPreferencesKey("maghrib_enabled")
        val ISHA_ENABLED    = booleanPreferencesKey("isha_enabled")

        // تعديل مواقيت الأذان
        val FAJR_OFFSET    = intPreferencesKey("fajr_offset")
        val SUNRISE_OFFSET = intPreferencesKey("sunrise_offset")
        val DHUHR_OFFSET   = intPreferencesKey("dhuhr_offset")
        val ASR_OFFSET     = intPreferencesKey("asr_offset")
        val MAGHRIB_OFFSET = intPreferencesKey("maghrib_offset")
        val ISHA_OFFSET    = intPreferencesKey("isha_offset")

        // مواقيت الإقامة
        val FAJR_IQAMA    = intPreferencesKey("fajr_iqama")
        val DHUHR_IQAMA   = intPreferencesKey("dhuhr_iqama")
        val ASR_IQAMA     = intPreferencesKey("asr_iqama")
        val MAGHRIB_IQAMA = intPreferencesKey("maghrib_iqama")
        val ISHA_IQAMA    = intPreferencesKey("isha_iqama")

        // اقتراب الصلاة - تفعيل
        val FAJR_APPROACH_EN    = booleanPreferencesKey("fajr_approach_en")
        val DHUHR_APPROACH_EN   = booleanPreferencesKey("dhuhr_approach_en")
        val ASR_APPROACH_EN     = booleanPreferencesKey("asr_approach_en")
        val MAGHRIB_APPROACH_EN = booleanPreferencesKey("maghrib_approach_en")
        val ISHA_APPROACH_EN    = booleanPreferencesKey("isha_approach_en")

        // اقتراب الصلاة - دقائق
        val FAJR_APPROACH_MIN    = intPreferencesKey("fajr_approach_min")
        val SUNRISE_APPROACH_MIN = intPreferencesKey("sunrise_approach_min")
        val DHUHR_APPROACH_MIN   = intPreferencesKey("dhuhr_approach_min")
        val ASR_APPROACH_MIN     = intPreferencesKey("asr_approach_min")
        val MAGHRIB_APPROACH_MIN = intPreferencesKey("maghrib_approach_min")
        val ISHA_APPROACH_MIN    = intPreferencesKey("isha_approach_min")

        // شاشة الأذان
        val SHOW_AZAN_SCREEN  = booleanPreferencesKey("show_azan_screen")
        val AUTO_STOP_AZAN    = booleanPreferencesKey("auto_stop_azan")
        val AUTO_STOP_MINUTES = intPreferencesKey("auto_stop_minutes")

        // رمضان
        val RAMADAN_SUHOOR      = booleanPreferencesKey("ramadan_suhoor")
        val RAMADAN_IFTAR       = booleanPreferencesKey("ramadan_iftar")
        val SUHOOR_MIN_BEFORE   = intPreferencesKey("suhoor_min_before")
        val IFTAR_CANNON        = booleanPreferencesKey("iftar_cannon")
        val IFTAR_DUA           = booleanPreferencesKey("iftar_dua")
        val SILENT_TARAWEH      = booleanPreferencesKey("silent_taraweh")
        val TARAWEH_MINUTES     = intPreferencesKey("taraweh_minutes")
        val KHATMAT_COUNT       = intPreferencesKey("khatmat_count")
        val START_FROM_FATIHA   = booleanPreferencesKey("start_from_fatiha")
        val QURAN_BEFORE_MAGHRIB = booleanPreferencesKey("quran_before_maghrib")
    }

    // ── Prayer Settings ──────────────────────────────────────
    val prayerSettings: Flow<PrayerSettings> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { p ->
            PrayerSettings(
                calculationMethod = p[Keys.CALC_METHOD] ?: "EGYPT",
                asrMethod         = p[Keys.ASR_METHOD] ?: "STANDARD",
                latitude          = p[Keys.LATITUDE] ?: 0.0,
                longitude         = p[Keys.LONGITUDE] ?: 0.0,
                cityName          = p[Keys.CITY_NAME] ?: "",
                timeZoneOffset    = p[Keys.TIMEZONE] ?: 2.0,
                summerTimeOffset  = p[Keys.SUMMER_OFFSET] ?: 0
            )
        }

    val isLocationSet: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.LOCATION_SET] ?: false }

    // ── Azan Settings ────────────────────────────────────────
    val azanSettings: Flow<PrayerAzanSettings> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { p ->
            PrayerAzanSettings(
                fajrSound    = p[Keys.FAJR_SOUND] ?: "azan_fajr_makkah",
                dhuhrSound   = p[Keys.DHUHR_SOUND] ?: "azan_makkah_sudais",
                asrSound     = p[Keys.ASR_SOUND] ?: "azan_makkah_sudais",
                maghribSound = p[Keys.MAGHRIB_SOUND] ?: "azan_makkah_sudais",
                ishaSound    = p[Keys.ISHA_SOUND] ?: "azan_makkah_sudais",
                fajrEnabled    = p[Keys.FAJR_ENABLED] ?: true,
                dhuhrEnabled   = p[Keys.DHUHR_ENABLED] ?: true,
                asrEnabled     = p[Keys.ASR_ENABLED] ?: true,
                maghribEnabled = p[Keys.MAGHRIB_ENABLED] ?: true,
                ishaEnabled    = p[Keys.ISHA_ENABLED] ?: true,
                fajrOffset    = p[Keys.FAJR_OFFSET] ?: 0,
                sunriseOffset = p[Keys.SUNRISE_OFFSET] ?: 0,
                dhuhrOffset   = p[Keys.DHUHR_OFFSET] ?: 0,
                asrOffset     = p[Keys.ASR_OFFSET] ?: 0,
                maghribOffset = p[Keys.MAGHRIB_OFFSET] ?: 0,
                ishaOffset    = p[Keys.ISHA_OFFSET] ?: 0,
                fajrIqama    = p[Keys.FAJR_IQAMA] ?: 20,
                dhuhrIqama   = p[Keys.DHUHR_IQAMA] ?: 10,
                asrIqama     = p[Keys.ASR_IQAMA] ?: 10,
                maghribIqama = p[Keys.MAGHRIB_IQAMA] ?: 5,
                ishaIqama    = p[Keys.ISHA_IQAMA] ?: 15,
                fajrApproachEnabled    = p[Keys.FAJR_APPROACH_EN] ?: true,
                dhuhrApproachEnabled   = p[Keys.DHUHR_APPROACH_EN] ?: true,
                asrApproachEnabled     = p[Keys.ASR_APPROACH_EN] ?: true,
                maghribApproachEnabled = p[Keys.MAGHRIB_APPROACH_EN] ?: true,
                ishaApproachEnabled    = p[Keys.ISHA_APPROACH_EN] ?: true,
                fajrApproachMinutes    = p[Keys.FAJR_APPROACH_MIN] ?: 20,
                sunriseApproachMinutes = p[Keys.SUNRISE_APPROACH_MIN] ?: 10,
                dhuhrApproachMinutes   = p[Keys.DHUHR_APPROACH_MIN] ?: 10,
                asrApproachMinutes     = p[Keys.ASR_APPROACH_MIN] ?: 10,
                maghribApproachMinutes = p[Keys.MAGHRIB_APPROACH_MIN] ?: 15,
                ishaApproachMinutes    = p[Keys.ISHA_APPROACH_MIN] ?: 15,
                showAzanScreen  = p[Keys.SHOW_AZAN_SCREEN] ?: true,
                autoStopAzan    = p[Keys.AUTO_STOP_AZAN] ?: false,
                autoStopMinutes = p[Keys.AUTO_STOP_MINUTES] ?: 5
            )
        }

    // ── Ramadan Settings ─────────────────────────────────────
    val ramadanSettings: Flow<RamadanSettings> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { p ->
            RamadanSettings(
                suhoorAlarmEnabled  = p[Keys.RAMADAN_SUHOOR] ?: true,
                iftarAlarmEnabled   = p[Keys.RAMADAN_IFTAR] ?: true,
                suhoorMinutesBefore = p[Keys.SUHOOR_MIN_BEFORE] ?: 30,
                iftarCannonEnabled  = p[Keys.IFTAR_CANNON] ?: true,
                iftarDuaEnabled     = p[Keys.IFTAR_DUA] ?: true,
                silentDuringTaraweh = p[Keys.SILENT_TARAWEH] ?: false,
                tarawehSilentMinutes= p[Keys.TARAWEH_MINUTES] ?: 60,
                khatmatCount        = p[Keys.KHATMAT_COUNT] ?: 1,
                startFromFatiha     = p[Keys.START_FROM_FATIHA] ?: true,
                quranBeforeMaghrib  = p[Keys.QURAN_BEFORE_MAGHRIB] ?: false
            )
        }

    // ── Save Functions ───────────────────────────────────────
    suspend fun saveLocation(lat: Double, lng: Double, city: String, tz: Double) {
        dataStore.edit {
            it[Keys.LATITUDE]     = lat
            it[Keys.LONGITUDE]    = lng
            it[Keys.CITY_NAME]    = city
            it[Keys.TIMEZONE]     = tz
            it[Keys.LOCATION_SET] = true
        }
    }

    suspend fun saveCalculationMethod(method: String) {
        dataStore.edit { it[Keys.CALC_METHOD] = method }
    }

    suspend fun saveAsrMethod(method: String) {
        dataStore.edit { it[Keys.ASR_METHOD] = method }
    }

    suspend fun saveSummerOffset(offset: Int) {
        dataStore.edit { it[Keys.SUMMER_OFFSET] = offset }
    }

    suspend fun saveAzanSound(prayer: String, sound: String) {
        dataStore.edit { p ->
            when (prayer) {
                "الفجر"  -> p[Keys.FAJR_SOUND]    = sound
                "الظهر"  -> p[Keys.DHUHR_SOUND]   = sound
                "العصر"  -> p[Keys.ASR_SOUND]     = sound
                "المغرب" -> p[Keys.MAGHRIB_SOUND] = sound
                "العشاء" -> p[Keys.ISHA_SOUND]    = sound
            }
        }
    }

    suspend fun saveAzanEnabled(prayer: String, enabled: Boolean) {
        dataStore.edit { p ->
            when (prayer) {
                "الفجر"  -> p[Keys.FAJR_ENABLED]    = enabled
                "الظهر"  -> p[Keys.DHUHR_ENABLED]   = enabled
                "العصر"  -> p[Keys.ASR_ENABLED]     = enabled
                "المغرب" -> p[Keys.MAGHRIB_ENABLED] = enabled
                "العشاء" -> p[Keys.ISHA_ENABLED]    = enabled
            }
        }
    }

    suspend fun saveAzanOffset(prayer: String, offset: Int) {
        dataStore.edit { p ->
            when (prayer) {
                "الفجر"   -> p[Keys.FAJR_OFFSET]    = offset
                "الشروق"  -> p[Keys.SUNRISE_OFFSET] = offset
                "الظهر"   -> p[Keys.DHUHR_OFFSET]   = offset
                "العصر"   -> p[Keys.ASR_OFFSET]     = offset
                "المغرب"  -> p[Keys.MAGHRIB_OFFSET] = offset
                "العشاء"  -> p[Keys.ISHA_OFFSET]    = offset
            }
        }
    }

    suspend fun saveIqamaMinutes(prayer: String, minutes: Int) {
        dataStore.edit { p ->
            when (prayer) {
                "الفجر"  -> p[Keys.FAJR_IQAMA]    = minutes
                "الظهر"  -> p[Keys.DHUHR_IQAMA]   = minutes
                "العصر"  -> p[Keys.ASR_IQAMA]     = minutes
                "المغرب" -> p[Keys.MAGHRIB_IQAMA] = minutes
                "العشاء" -> p[Keys.ISHA_IQAMA]    = minutes
            }
        }
    }

    suspend fun saveApproachEnabled(prayer: String, enabled: Boolean) {
        dataStore.edit { p ->
            when (prayer) {
                "الفجر"  -> p[Keys.FAJR_APPROACH_EN]    = enabled
                "الظهر"  -> p[Keys.DHUHR_APPROACH_EN]   = enabled
                "العصر"  -> p[Keys.ASR_APPROACH_EN]     = enabled
                "المغرب" -> p[Keys.MAGHRIB_APPROACH_EN] = enabled
                "العشاء" -> p[Keys.ISHA_APPROACH_EN]    = enabled
            }
        }
    }

    suspend fun saveApproachMinutes(prayer: String, minutes: Int) {
        dataStore.edit { p ->
            when (prayer) {
                "الفجر"  -> p[Keys.FAJR_APPROACH_MIN]    = minutes
                "الشروق" -> p[Keys.SUNRISE_APPROACH_MIN] = minutes
                "الظهر"  -> p[Keys.DHUHR_APPROACH_MIN]   = minutes
                "العصر"  -> p[Keys.ASR_APPROACH_MIN]     = minutes
                "المغرب" -> p[Keys.MAGHRIB_APPROACH_MIN] = minutes
                "العشاء" -> p[Keys.ISHA_APPROACH_MIN]    = minutes
            }
        }
    }

    suspend fun saveAzanScreenSettings(show: Boolean, autoStop: Boolean, stopMinutes: Int) {
        dataStore.edit {
            it[Keys.SHOW_AZAN_SCREEN]  = show
            it[Keys.AUTO_STOP_AZAN]    = autoStop
            it[Keys.AUTO_STOP_MINUTES] = stopMinutes
        }
    }

    suspend fun saveRamadanSettings(s: RamadanSettings) {
        dataStore.edit { p ->
            p[Keys.RAMADAN_SUHOOR]      = s.suhoorAlarmEnabled
            p[Keys.RAMADAN_IFTAR]       = s.iftarAlarmEnabled
            p[Keys.SUHOOR_MIN_BEFORE]   = s.suhoorMinutesBefore
            p[Keys.IFTAR_CANNON]        = s.iftarCannonEnabled
            p[Keys.IFTAR_DUA]           = s.iftarDuaEnabled
            p[Keys.SILENT_TARAWEH]      = s.silentDuringTaraweh
            p[Keys.TARAWEH_MINUTES]     = s.tarawehSilentMinutes
            p[Keys.KHATMAT_COUNT]       = s.khatmatCount
            p[Keys.START_FROM_FATIHA]   = s.startFromFatiha
            p[Keys.QURAN_BEFORE_MAGHRIB]= s.quranBeforeMaghrib
        }
    }

    // legacy - للتوافق مع الكود القديم
    suspend fun saveAzanSettings(settings: PrayerAzanSettings) {
        dataStore.edit { p ->
            p[Keys.FAJR_SOUND]    = settings.fajrSound
            p[Keys.DHUHR_SOUND]   = settings.dhuhrSound
            p[Keys.ASR_SOUND]     = settings.asrSound
            p[Keys.MAGHRIB_SOUND] = settings.maghribSound
            p[Keys.ISHA_SOUND]    = settings.ishaSound
            p[Keys.FAJR_ENABLED]    = settings.fajrEnabled
            p[Keys.DHUHR_ENABLED]   = settings.dhuhrEnabled
            p[Keys.ASR_ENABLED]     = settings.asrEnabled
            p[Keys.MAGHRIB_ENABLED] = settings.maghribEnabled
            p[Keys.ISHA_ENABLED]    = settings.ishaEnabled
        }
    }
}
