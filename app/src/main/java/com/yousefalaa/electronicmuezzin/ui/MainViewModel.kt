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
        observeSettingsAndCalculate()
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

    private fun observeSettingsAndCalculate() {
        viewModelScope.launch {
            settingsRepo.prayerSettings.collect { settings ->
                if (settings.latitude != 0.0 && settings.longitude != 0.0) {
                    calculatePrayerTimes(settings)
                }
            }
        }
    }

    fun calculatePrayerTimes(settings: PrayerSettings) {
        viewModelScope.launch {
            try {
                val method = PrayerTimesCalculator.CalculationMethod.valueOf(settings.calculationMethod)
                val asrMethod = PrayerTimesCalculator.AsrMethod.valueOf(settings.asrMethod)
                val tz = getTimeZoneOffset()
                val calc = PrayerTimesCalculator.calculate(
                    settings.latitude, settings.longitude, tz, method, asrMethod
                )
                val model = PrayerTimesModel(
                    fajr = calc.fajr,
                    sunrise = calc.sunrise,
                    dhuhr = calc.dhuhr,
                    asr = calc.asr,
                    maghrib = calc.maghrib,
                    isha = calc.isha,
                    midnight = calc.midnight
                )
                _prayerTimes.value = model
                _nextPrayer.value = model.getNextPrayer()

                // Schedule alarms
                azanSettings.value.let { az ->
                    AlarmScheduler.schedulePrayerAlarms(context, model, az)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchLocationAndSave() {
        viewModelScope.launch {
            try {
                val fusedClient = LocationServices.getFusedLocationProviderClient(context)
                fusedClient.lastLocation.addOnSuccessListener { location ->
                    location?.let { loc ->
                        viewModelScope.launch {
                            val cityName = getCityName(loc.latitude, loc.longitude)
                            val tz = getTimeZoneOffset()
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
            val geocoder = Geocoder(context, Locale("ar"))
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
        val tz = TimeZone.getDefault()
        val offset = tz.getOffset(System.currentTimeMillis())
        return offset / 3600000.0
    }

    fun getQiblaDirection(): Double {
        val settings = prayerSettings.value
        return QiblaCalculator.getQiblaDirection(settings.latitude, settings.longitude)
    }

    fun getDistanceToMakkah(): Double {
        val settings = prayerSettings.value
        return QiblaCalculator.getDistanceToMakkah(settings.latitude, settings.longitude)
    }

    fun updateCalcMethod(method: String) {
        viewModelScope.launch {
            settingsRepo.saveCalculationMethod(method)
        }
    }

    fun updateAsrMethod(method: String) {
        viewModelScope.launch {
            settingsRepo.saveAsrMethod(method)
        }
    }

    fun togglePrayerAzan(prayerName: String, enabled: Boolean) {
        viewModelScope.launch {
            val current = azanSettings.value
            val updated = when (prayerName) {
                "الفجر" -> current.copy(fajrEnabled = enabled)
                "الظهر" -> current.copy(dhuhrEnabled = enabled)
                "العصر" -> current.copy(asrEnabled = enabled)
                "المغرب" -> current.copy(maghribEnabled = enabled)
                "العشاء" -> current.copy(ishaEnabled = enabled)
                else -> current
            }
            settingsRepo.saveAzanSettings(updated)
        }
    }
}
