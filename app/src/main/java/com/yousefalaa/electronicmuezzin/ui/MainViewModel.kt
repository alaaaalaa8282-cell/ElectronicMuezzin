package com.yousefalaa.electronicmuezzin.ui

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.yousefalaa.electronicmuezzin.data.models.*
import com.yousefalaa.electronicmuezzin.data.repositories.SettingsRepository
import com.yousefalaa.electronicmuezzin.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepo: SettingsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _prayerTimes = MutableStateFlow<PrayerTimesModel?>(null)
    val prayerTimes: StateFlow<PrayerTimesModel?> = _prayerTimes.asStateFlow()

    private val _currentTime = MutableStateFlow(System.currentTimeMillis())
    val currentTime: StateFlow<Long> = _currentTime.asStateFlow()

    private val _hijriDate = MutableStateFlow(HijriCalendar.today())
    val hijriDate: StateFlow<HijriCalendar.HijriDate> = _hijriDate.asStateFlow()

    private val _nextPrayer = MutableStateFlow<Pair<String, Long>?>(null)
    val nextPrayer: StateFlow<Pair<String, Long>?> = _nextPrayer.asStateFlow()

    val prayerSettings = settingsRepo.prayerSettings.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), PrayerSettings()
    )

    val azanSettings = settingsRepo.azanSettings.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), PrayerAzanSettings()
    )

    val isLocationSet = settingsRepo.isLocationSet.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    init {
        startClock()
        // راقب أي تغيير في الإعدادات وأعد الحساب والجدولة
        viewModelScope.launch {
            combine(
                settingsRepo.prayerSettings,
                settingsRepo.azanSettings
            ) { ps, az -> ps to az }
            .collect { (ps, az) ->
                if (ps.latitude != 0.0 && ps.longitude != 0.0) {
                    calculateAndSchedule(ps, az)
                }
            }
        }
    }

    private fun startClock() {
        viewModelScope.launch {
            while (isActive) {
                _currentTime.value = System.currentTimeMillis()
                _hijriDate.value = HijriCalendar.today()
                _prayerTimes.value?.let { times ->
                    _nextPrayer.value = times.getNextPrayer()
                }
                delay(1000L)
            }
        }
    }

    private fun calculateAndSchedule(settings: PrayerSettings, az: PrayerAzanSettings) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val method    = PrayerTimesCalculator.CalculationMethod.valueOf(settings.calculationMethod)
                val asrMethod = PrayerTimesCalculator.AsrMethod.valueOf(settings.asrMethod)
                val tz        = getTimeZoneOffset()

                val calc = PrayerTimesCalculator.calculate(
                    settings.latitude, settings.longitude, tz, method, asrMethod
                )

                // طبّق تعديلات الأوفست (دقائق + أو -)
                val model = PrayerTimesModel(
                    fajr     = calc.fajr     + az.fajrOffset    * 60_000L,
                    sunrise  = calc.sunrise  + az.sunriseOffset * 60_000L,
                    dhuhr    = calc.dhuhr    + az.dhuhrOffset   * 60_000L,
                    asr      = calc.asr      + az.asrOffset     * 60_000L,
                    maghrib  = calc.maghrib  + az.maghribOffset * 60_000L,
                    isha     = calc.isha     + az.ishaOffset    * 60_000L,
                    midnight = calc.midnight
                )

                withContext(Dispatchers.Main) {
                    _prayerTimes.value = model
                    _nextPrayer.value  = model.getNextPrayer()
                }

                // جدول الأذانات
                AlarmScheduler.schedulePrayerAlarms(context, model, az)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // استدعاء خارجي لو احتجنا
    fun calculatePrayerTimes(settings: PrayerSettings) {
        calculateAndSchedule(settings, azanSettings.value)
    }

    fun fetchLocationAndSave() {
        viewModelScope.launch {
            try {
                val fusedClient = LocationServices.getFusedLocationProviderClient(context)
                fusedClient.lastLocation.addOnSuccessListener { location ->
                    location?.let { loc ->
                        viewModelScope.launch {
                            val cityName = getCityName(loc.latitude, loc.longitude)
                            val tz       = getTimeZoneOffset()
                            settingsRepo.saveLocation(loc.latitude, loc.longitude, cityName, tz)
                        }
                    }
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    private fun getCityName(lat: Double, lng: Double): String {
        return try {
            val geocoder  = Geocoder(context, Locale("ar"))
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            addresses?.firstOrNull()?.let {
                it.locality ?: it.adminArea ?: it.countryName
            } ?: "موقعي"
        } catch (e: Exception) {
            "موقعي"
        }
    }

    private fun getTimeZoneOffset(): Double {
        val tz     = TimeZone.getDefault()
        val offset = tz.getOffset(System.currentTimeMillis())
        return offset / 3_600_000.0
    }

    fun getQiblaDirection(): Double {
        val s = prayerSettings.value
        return QiblaCalculator.getQiblaDirection(s.latitude, s.longitude)
    }

    fun getDistanceToMakkah(): Double {
        val s = prayerSettings.value
        return QiblaCalculator.getDistanceToMakkah(s.latitude, s.longitude)
    }

    // ── للتوافق مع الكود القديم ──────────────────────────
    fun updateCalcMethod(method: String) {
        viewModelScope.launch { settingsRepo.saveCalculationMethod(method) }
    }

    fun updateAsrMethod(method: String) {
        viewModelScope.launch { settingsRepo.saveAsrMethod(method) }
    }

    fun togglePrayerAzan(prayerName: String, enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.saveAzanEnabled(prayerName, enabled)
        }
    }

    fun setPrayerAzanSound(prayerName: String, soundKey: String) {
        viewModelScope.launch {
            settingsRepo.saveAzanSound(prayerName, soundKey)
        }
    }
}

