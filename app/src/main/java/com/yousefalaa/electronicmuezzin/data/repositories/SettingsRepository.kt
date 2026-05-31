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

    // Keys
    private object Keys {
        val CALC_METHOD = stringPreferencesKey("calc_method")
        val ASR_METHOD = stringPreferencesKey("asr_method")
        val LATITUDE = doublePreferencesKey("latitude")
        val LONGITUDE = doublePreferencesKey("longitude")
        val CITY_NAME = stringPreferencesKey("city_name")
        val TIMEZONE = doublePreferencesKey("timezone")
        val LANGUAGE = stringPreferencesKey("language")

        // Azan settings per prayer
        val FAJR_SOUND = stringPreferencesKey("fajr_sound")
        val DHUHR_SOUND = stringPreferencesKey("dhuhr_sound")
        val ASR_SOUND = stringPreferencesKey("asr_sound")
        val MAGHRIB_SOUND = stringPreferencesKey("maghrib_sound")
        val ISHA_SOUND = stringPreferencesKey("isha_sound")

        val FAJR_ENABLED = booleanPreferencesKey("fajr_enabled")
        val DHUHR_ENABLED = booleanPreferencesKey("dhuhr_enabled")
        val ASR_ENABLED = booleanPreferencesKey("asr_enabled")
        val MAGHRIB_ENABLED = booleanPreferencesKey("maghrib_enabled")
        val ISHA_ENABLED = booleanPreferencesKey("isha_enabled")

        val FAJR_NOTIF = booleanPreferencesKey("fajr_notif")
        val DHUHR_NOTIF = booleanPreferencesKey("dhuhr_notif")
        val ASR_NOTIF = booleanPreferencesKey("asr_notif")
        val MAGHRIB_NOTIF = booleanPreferencesKey("maghrib_notif")
        val ISHA_NOTIF = booleanPreferencesKey("isha_notif")

        // Ramadan
        val RAMADAN_SUHOOR = booleanPreferencesKey("ramadan_suhoor")
        val RAMADAN_IFTAR = booleanPreferencesKey("ramadan_iftar")
        val SUHOOR_MINUTES_BEFORE = intPreferencesKey("suhoor_minutes_before")

        // Adhkar reminders
        val MORNING_ADHKAR_ENABLED = booleanPreferencesKey("morning_adhkar_enabled")
        val EVENING_ADHKAR_ENABLED = booleanPreferencesKey("evening_adhkar_enabled")
        val MORNING_ADHKAR_TIME = stringPreferencesKey("morning_adhkar_time")
        val EVENING_ADHKAR_TIME = stringPreferencesKey("evening_adhkar_time")

        // Quran khatma
        val QURAN_REMINDER_ENABLED = booleanPreferencesKey("quran_reminder_enabled")
        val QURAN_REMINDER_TIME = stringPreferencesKey("quran_reminder_time")

        // First launch
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
        val LOCATION_SET = booleanPreferencesKey("location_set")
    }

    val prayerSettings: Flow<PrayerSettings> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs ->
            PrayerSettings(
                calculationMethod = prefs[Keys.CALC_METHOD] ?: "EGYPT",
                asrMethod = prefs[Keys.ASR_METHOD] ?: "STANDARD",
                latitude = prefs[Keys.LATITUDE] ?: 0.0,
                longitude = prefs[Keys.LONGITUDE] ?: 0.0,
                cityName = prefs[Keys.CITY_NAME] ?: "",
                timeZoneOffset = prefs[Keys.TIMEZONE] ?: 2.0,
                language = prefs[Keys.LANGUAGE] ?: "ar"
            )
        }

    val azanSettings: Flow<PrayerAzanSettings> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs ->
            PrayerAzanSettings(
                fajrSound = prefs[Keys.FAJR_SOUND] ?: "azan_fajr",
                dhuhrSound = prefs[Keys.DHUHR_SOUND] ?: "azan_makkah",
                asrSound = prefs[Keys.ASR_SOUND] ?: "azan_makkah",
                maghribSound = prefs[Keys.MAGHRIB_SOUND] ?: "azan_makkah",
                ishaSound = prefs[Keys.ISHA_SOUND] ?: "azan_makkah",
                fajrEnabled = prefs[Keys.FAJR_ENABLED] ?: true,
                dhuhrEnabled = prefs[Keys.DHUHR_ENABLED] ?: true,
                asrEnabled = prefs[Keys.ASR_ENABLED] ?: true,
                maghribEnabled = prefs[Keys.MAGHRIB_ENABLED] ?: true,
                ishaEnabled = prefs[Keys.ISHA_ENABLED] ?: true,
                fajrNotification = prefs[Keys.FAJR_NOTIF] ?: true,
                dhuhrNotification = prefs[Keys.DHUHR_NOTIF] ?: true,
                asrNotification = prefs[Keys.ASR_NOTIF] ?: true,
                maghribNotification = prefs[Keys.MAGHRIB_NOTIF] ?: true,
                ishaNotification = prefs[Keys.ISHA_NOTIF] ?: true
            )
        }

    val ramadanSettings: Flow<RamadanSettings> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs ->
            RamadanSettings(
                suhoorAlarmEnabled = prefs[Keys.RAMADAN_SUHOOR] ?: true,
                iftarAlarmEnabled = prefs[Keys.RAMADAN_IFTAR] ?: true,
                suhoorMinutesBefore = prefs[Keys.SUHOOR_MINUTES_BEFORE] ?: 30
            )
        }

    val isFirstLaunch: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs -> prefs[Keys.FIRST_LAUNCH] ?: true }

    val isLocationSet: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs -> prefs[Keys.LOCATION_SET] ?: false }

    suspend fun saveLocation(lat: Double, lng: Double, city: String, tz: Double) {
        dataStore.edit { prefs ->
            prefs[Keys.LATITUDE] = lat
            prefs[Keys.LONGITUDE] = lng
            prefs[Keys.CITY_NAME] = city
            prefs[Keys.TIMEZONE] = tz
            prefs[Keys.LOCATION_SET] = true
            prefs[Keys.FIRST_LAUNCH] = false
        }
    }

    suspend fun saveCalculationMethod(method: String) {
        dataStore.edit { it[Keys.CALC_METHOD] = method }
    }

    suspend fun saveAsrMethod(method: String) {
        dataStore.edit { it[Keys.ASR_METHOD] = method }
    }

    suspend fun saveAzanSettings(settings: PrayerAzanSettings) {
        dataStore.edit { prefs ->
            prefs[Keys.FAJR_SOUND] = settings.fajrSound
            prefs[Keys.DHUHR_SOUND] = settings.dhuhrSound
            prefs[Keys.ASR_SOUND] = settings.asrSound
            prefs[Keys.MAGHRIB_SOUND] = settings.maghribSound
            prefs[Keys.ISHA_SOUND] = settings.ishaSound
            prefs[Keys.FAJR_ENABLED] = settings.fajrEnabled
            prefs[Keys.DHUHR_ENABLED] = settings.dhuhrEnabled
            prefs[Keys.ASR_ENABLED] = settings.asrEnabled
            prefs[Keys.MAGHRIB_ENABLED] = settings.maghribEnabled
            prefs[Keys.ISHA_ENABLED] = settings.ishaEnabled
            prefs[Keys.FAJR_NOTIF] = settings.fajrNotification
            prefs[Keys.DHUHR_NOTIF] = settings.dhuhrNotification
            prefs[Keys.ASR_NOTIF] = settings.asrNotification
            prefs[Keys.MAGHRIB_NOTIF] = settings.maghribNotification
            prefs[Keys.ISHA_NOTIF] = settings.ishaNotification
        }
    }

    suspend fun saveRamadanSettings(settings: RamadanSettings) {
        dataStore.edit { prefs ->
            prefs[Keys.RAMADAN_SUHOOR] = settings.suhoorAlarmEnabled
            prefs[Keys.RAMADAN_IFTAR] = settings.iftarAlarmEnabled
            prefs[Keys.SUHOOR_MINUTES_BEFORE] = settings.suhoorMinutesBefore
        }
    }
}
