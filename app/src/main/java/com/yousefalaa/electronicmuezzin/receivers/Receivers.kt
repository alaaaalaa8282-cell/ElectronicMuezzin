package com.yousefalaa.electronicmuezzin.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.yousefalaa.electronicmuezzin.ElectronicMuezzinApp.Companion.CHANNEL_RAMADAN
import com.yousefalaa.electronicmuezzin.R
import com.yousefalaa.electronicmuezzin.services.AzanService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

// ========================
// Prayer Alarm Receiver
// ========================
class PrayerAlarmReceiver : BroadcastReceiver() {
    companion object {
        const val EXTRA_PRAYER_NAME = "prayer_name"
        const val EXTRA_AZAN_SOUND = "azan_sound"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME) ?: "الصلاة"
        val azanSound = intent.getStringExtra(EXTRA_AZAN_SOUND) ?: "azan_makkah"
        AzanService.start(context, prayerName, azanSound)
    }
}

// ========================
// Boot Receiver
// ========================
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var settingsRepo: com.yousefalaa.electronicmuezzin.data.repositories.SettingsRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Re-schedule alarms after reboot
                    rescheduleAlarms(context)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    private suspend fun rescheduleAlarms(context: Context) {
        // Collect settings once
        var prayerSettings: com.yousefalaa.electronicmuezzin.data.models.PrayerSettings? = null
        var azanSettings: com.yousefalaa.electronicmuezzin.data.models.PrayerAzanSettings? = null

        settingsRepo.prayerSettings.collect { settings ->
            prayerSettings = settings
            return@collect
        }
        settingsRepo.azanSettings.collect { settings ->
            azanSettings = settings
            return@collect
        }

        prayerSettings?.let { ps ->
            if (ps.latitude != 0.0 && ps.longitude != 0.0) {
                val method = com.yousefalaa.electronicmuezzin.utils.PrayerTimesCalculator.CalculationMethod.valueOf(ps.calculationMethod)
                val asrMethod = com.yousefalaa.electronicmuezzin.utils.PrayerTimesCalculator.AsrMethod.valueOf(ps.asrMethod)
                val calc = com.yousefalaa.electronicmuezzin.utils.PrayerTimesCalculator.calculate(
                    ps.latitude, ps.longitude, ps.timeZoneOffset, method, asrMethod
                )
                val model = com.yousefalaa.electronicmuezzin.data.models.PrayerTimesModel(
                    calc.fajr, calc.sunrise, calc.dhuhr, calc.asr, calc.maghrib, calc.isha, calc.midnight
                )
                azanSettings?.let { az ->
                    com.yousefalaa.electronicmuezzin.utils.AlarmScheduler.schedulePrayerAlarms(context, model, az)
                }
            }
        }
    }
}

// ========================
// Ramadan Alarm Receiver
// ========================
class RamadanAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra("type") ?: return
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val (title, text) = when (type) {
            "suhoor" -> "وقت السحور" to "تبقى 30 دقيقة على أذان الفجر، أسرع في تناول سحورك"
            "iftar" -> "حان وقت الإفطار 🌙" to "اللهم لك صمت وعلى رزقك أفطرت"
            else -> return
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_RAMADAN)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_mosque)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        nm.notify(if (type == "suhoor") 200 else 201, notification)
    }
}
