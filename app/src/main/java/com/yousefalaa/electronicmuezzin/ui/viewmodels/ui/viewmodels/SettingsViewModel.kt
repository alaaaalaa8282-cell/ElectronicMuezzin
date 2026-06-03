Enterpackage com.yousefalaa.electronicmuezzin.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yousefalaa.electronicmuezzin.data.models.PrayerAzanSettings
import com.yousefalaa.electronicmuezzin.data.models.PrayerSettings
import com.yousefalaa.electronicmuezzin.data.models.RamadanSettings
import com.yousefalaa.electronicmuezzin.data.repositories.SettingsRepository
import com.yousefalaa.electronicmuezzin.utils.AlarmScheduler
import com.yousefalaa.electronicmuezzin.utils.PrayerTimesCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: SettingsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val prayerSettings = repo.prayerSettings.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000),
        PrayerSettings()
    )

    val azanSettings = repo.azanSettings.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000),
        PrayerAzanSettings()
    )

    val ramadanSettings = repo.ramadanSettings.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000),
        RamadanSettings()
    )

    // ── حفظ وإعادة جدولة الأذان ───────────────────────────
    private fun recalculateAndReschedule() {
        viewModelScope.launch {
            val ps = prayerSettings.value
            val az = azanSettings.value
            if (ps.latitude == 0.0) return@launch
            val method    = PrayerTimesCalculator.CalculationMethod.valueOf(ps.calculationMethod)
            val asrMethod = PrayerTimesCalculator.AsrMethod.valueOf(ps.asrMethod)
            val tz        = TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 3600000.0
            val calc      = PrayerTimesCalculator.calculate(ps.latitude, ps.longitude, tz, method, asrMethod)
            val model     = com.yousefalaa.electronicmuezzin.data.models.PrayerTimesModel(
                fajr    = calc.fajr    + az.fajrOffset    * 60000L,
                sunrise = calc.sunrise + az.sunriseOffset * 60000L,
                dhuhr   = calc.dhuhr   + az.dhuhrOffset   * 60000L,
                asr     = calc.asr     + az.asrOffset     * 60000L,
                maghrib = calc.maghrib + az.maghribOffset  * 60000L,
                isha    = calc.isha    + az.ishaOffset    * 60000L,
                midnight= calc.midnight
            )
            AlarmScheduler.schedulePrayerAlarms(context, model, az)
        }
    }

    // ── أصوات الأذان ──────────────────────────────────────
    fun setAzanSound(prayer: String, sound: String) {
        viewModelScope.launch {
            repo.saveAzanSound(prayer, sound)
            recalculateAndReschedule()
        }
    }

    fun setAzanEnabled(prayer: String, enabled: Boolean) {
        viewModelScope.launch {
            repo.saveAzanEnabled(prayer, enabled)
            recalculateAndReschedule()
        }
    }

    // ── تعديل مواقيت الأذان ──────────────────────────────
    fun setAzanOffset(prayer: String, offset: Int) {
        viewModelScope.launch {
            repo.saveAzanOffset(prayer, offset)
            recalculateAndReschedule()
        }
    }

    // ── مواقيت الإقامة ───────────────────────────────────
    fun setIqamaMinutes(prayer: String, minutes: Int) {
        viewModelScope.launch { repo.saveIqamaMinutes(prayer, minutes) }
    }

    // ── اقتراب الصلاة ────────────────────────────────────
    fun setApproachEnabled(prayer: String, enabled: Boolean) {
        viewModelScope.launch { repo.saveApproachEnabled(prayer, enabled) }
    }

    fun setApproachMinutes(prayer: String, minutes: Int) {
        viewModelScope.launch { repo.saveApproachMinutes(prayer, minutes) }
    }

    // ── طريقة الحساب ─────────────────────────────────────
    fun setCalculationMethod(method: String) {
        viewModelScope.launch {
            repo.saveCalculationMethod(method)
            recalculateAndReschedule()
        }
    }

    fun setAsrMethod(method: String) {
        viewModelScope.launch {
            repo.saveAsrMethod(method)
            recalculateAndReschedule()
        }
    }

    fun setSummerOffset(offset: Int) {
        viewModelScope.launch {
            repo.saveSummerOffset(offset)
            recalculateAndReschedule()
        }
    }

    // ── شاشة الأذان ──────────────────────────────────────
    fun setAzanScreenSettings(show: Boolean, autoStop: Boolean, stopMinutes: Int) {
        viewModelScope.launch { repo.saveAzanScreenSettings(show, autoStop, stopMinutes) }
    }

    // ── رمضان ────────────────────────────────────────────
    fun setRamadanSettings(s: RamadanSettings) {
        viewModelScope.launch { repo.saveRamadanSettings(s) }
    }
}
