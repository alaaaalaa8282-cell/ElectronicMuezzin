package com.yousefalaa.electronicmuezzin.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.yousefalaa.electronicmuezzin.ElectronicMuezzinApp.Companion.CHANNEL_RAMADAN
import com.yousefalaa.electronicmuezzin.R
import com.yousefalaa.electronicmuezzin.data.models.PrayerTimesModel
import com.yousefalaa.electronicmuezzin.data.repositories.SettingsRepository
import com.yousefalaa.electronicmuezzin.services.AzanService
import com.yousefalaa.electronicmuezzin.utils.AlarmScheduler
import com.yousefalaa.electronicmuezzin.utils.PrayerTimesCalculator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.util.TimeZone
import javax.inject.Inject

// ──────────────────────────────────────────────────
//  Prayer Alarm Receiver
// ──────────────────────────────────────────────────
class PrayerAlarmReceiver : BroadcastReceiver() {
    companion object {
        const val EXTRA_PRAYER_NAME = "prayer_name"
        const val EXTRA_AZAN_SOUND  = "azan_sound"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME) ?: "الصلاة"
        val azanSound  = intent.getStringExtra(EXTRA_AZAN_SOUND)  ?: "azan_makkah_sudais"
        // لا تشغّل الأذان لو الوضع الصامت
        if (azanSound == "silent_mode") return
        AzanService.start(context, prayerName, azanSound)
    }
}

// ──────────────────────────────────────────────────
//  Boot Receiver - يعيد جدولة الأذانات بعد الإعادة
// ──────────────────────────────────────────────────
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var settingsRepo: SettingsRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_MY_PACKAGE_REPLACED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val ps = settingsRepo.prayerSettings.first()
                val az = settingsRepo.azanSettings.first()

                if (ps.latitude == 0.0 || ps.longitude == 0.0) return@launch

                val method    = PrayerTimesCalculator.CalculationMethod.valueOf(ps.calculationMethod)
                val asrMethod = PrayerTimesCalculator.AsrMethod.valueOf(ps.asrMethod)
                val tz        = TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 3_600_000.0

                val calc = PrayerTimesCalculator.calculate(ps.latitude, ps.longitude, tz, method, asrMethod)

                val model = PrayerTimesModel(
                    fajr     = calc.fajr     + az.fajrOffset    * 60_000L,
                    sunrise  = calc.sunrise  + az.sunriseOffset * 60_000L,
                    dhuhr    = calc.dhuhr    + az.dhuhrOffset   * 60_000L,
                    asr      = calc.asr      + az.asrOffset     * 60_000L,
                    maghrib  = calc.maghrib  + az.maghribOffset * 60_000L,
                    isha     = calc.isha     + az.ishaOffset    * 60_000L,
                    midnight = calc.midnight
                )

                AlarmScheduler.schedulePrayerAlarms(context, model, az)
            } finally {
                pendingResult.finish()
            }
        }
    }
}

// ──────────────────────────────────────────────────
//  Ramadan Alarm Receiver
// ──────────────────────────────────────────────────
class RamadanAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra("type") ?: return
        val nm   = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val (title, text) = when (type) {
            "suhoor" -> "وقت السحور 🌙" to "تبقّى 30 دقيقة على أذان الفجر، أسرع في تناول سحورك"
            "iftar"  -> "حان وقت الإفطار 🌅" to "اللهم لك صمت وعلى رزقك أفطرت"
            else     -> return
        }

        val notif = NotificationCompat.Builder(context, CHANNEL_RAMADAN)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_mosque)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        nm.notify(if (type == "suhoor") 200 else 201, notif)
    }
}

