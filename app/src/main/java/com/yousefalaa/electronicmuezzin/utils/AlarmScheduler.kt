package com.yousefalaa.electronicmuezzin.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.yousefalaa.electronicmuezzin.data.models.PrayerAzanSettings
import com.yousefalaa.electronicmuezzin.data.models.PrayerTimesModel
import com.yousefalaa.electronicmuezzin.receivers.PrayerAlarmReceiver

object AlarmScheduler {

    private const val TAG = "AlarmScheduler"

    fun schedulePrayerAlarms(
        context: Context,
        times: PrayerTimesModel,
        azanSettings: PrayerAzanSettings
    ) {
        val now = System.currentTimeMillis()

        val prayers = listOf(
            Triple("الفجر",   times.fajr,    azanSettings.fajrEnabled    to azanSettings.fajrSound),
            Triple("الظهر",   times.dhuhr,   azanSettings.dhuhrEnabled   to azanSettings.dhuhrSound),
            Triple("العصر",   times.asr,     azanSettings.asrEnabled     to azanSettings.asrSound),
            Triple("المغرب",  times.maghrib, azanSettings.maghribEnabled  to azanSettings.maghribSound),
            Triple("العشاء",  times.isha,    azanSettings.ishaEnabled    to azanSettings.ishaSound)
        )

        // إلغاء الأذانات القديمة أولاً
        cancelAllAlarms(context)

        prayers.forEachIndexed { index, (name, time, settings) ->
            val (enabled, sound) = settings
            // جدول الأذان حتى لو الوقت فاسه اليوم (سيُجدَّل غداً)
            var targetTime = time
            if (targetTime <= now) {
                targetTime += 24 * 60 * 60 * 1000L // أضف يوم كامل
            }
            if (enabled && sound != "silent_mode") {
                scheduleExactAlarm(context, index, name, targetTime, sound)
                Log.d(TAG, "Scheduled $name at $targetTime")
            }
        }
    }

    private fun scheduleExactAlarm(
        context: Context,
        id: Int,
        prayerName: String,
        timeMs: Long,
        sound: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_NAME, prayerName)
            putExtra(PrayerAlarmReceiver.EXTRA_AZAN_SOUND, sound)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMs, pendingIntent)
                } else {
                    // فول باك: استخدم setAndAllowWhileIdle (أقل دقة بس يشتغل)
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMs, pendingIntent)
                    Log.w(TAG, "No SCHEDULE_EXACT_ALARM permission - using inexact alarm for $prayerName")
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMs, pendingIntent)
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException scheduling alarm: ${e.message}")
            // فول باك أخير
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeMs, pendingIntent)
        }
    }

    fun cancelAllAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        for (i in 0..4) {
            val intent = Intent(context, PrayerAlarmReceiver::class.java)
            val pending = PendingIntent.getBroadcast(
                context, i, intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pending?.let { alarmManager.cancel(it) }
        }
    }

    fun scheduleRamadanAlarms(context: Context, suhoorTime: Long, iftarTime: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val now = System.currentTimeMillis()

        if (suhoorTime > now) {
            val i = Intent(context, com.yousefalaa.electronicmuezzin.receivers.RamadanAlarmReceiver::class.java)
                .apply { putExtra("type", "suhoor") }
            val p = PendingIntent.getBroadcast(context, 100, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, suhoorTime, p)
        }

        if (iftarTime > now) {
            val i = Intent(context, com.yousefalaa.electronicmuezzin.receivers.RamadanAlarmReceiver::class.java)
                .apply { putExtra("type", "iftar") }
            val p = PendingIntent.getBroadcast(context, 101, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, iftarTime, p)
        }
    }
}

// ─────────────────────────────────────────────
//  Formatting utilities
// ─────────────────────────────────────────────
object TimeFormatter {
    fun formatTime(timeMs: Long): String {
        val cal  = java.util.Calendar.getInstance()
        cal.timeInMillis = timeMs
        val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
        val min  = cal.get(java.util.Calendar.MINUTE)
        val amPm = if (hour < 12) "ص" else "م"
        val h    = if (hour > 12) hour - 12 else if (hour == 0) 12 else hour
        return "$h:${min.toString().padStart(2, '0')} $amPm"
    }

    fun formatCountdown(diffMs: Long): String {
        val total = diffMs / 1000
        val h = total / 3600
        val m = (total % 3600) / 60
        val s = total % 60
        return if (h > 0) "${h}س ${m}د ${s}ث" else "${m}د ${s}ث"
    }

    fun getDayName(): String {
        val days = listOf("الأحد","الاثنين","الثلاثاء","الأربعاء","الخميس","الجمعة","السبت")
        return days[java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK) - 1]
    }

    fun getMonthName(month: Int): String {
        val months = listOf("يناير","فبراير","مارس","أبريل","مايو","يونيو","يوليو","أغسطس","سبتمبر","أكتوبر","نوفمبر","ديسمبر")
        return months[(month - 1).coerceIn(0, 11)]
    }
}

