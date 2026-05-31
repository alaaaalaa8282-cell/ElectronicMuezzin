package com.yousefalaa.electronicmuezzin.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.yousefalaa.electronicmuezzin.data.models.PrayerAzanSettings
import com.yousefalaa.electronicmuezzin.data.models.PrayerTimesModel
import com.yousefalaa.electronicmuezzin.receivers.PrayerAlarmReceiver

object AlarmScheduler {

    fun schedulePrayerAlarms(
        context: Context,
        times: PrayerTimesModel,
        azanSettings: PrayerAzanSettings
    ) {
        val prayers = listOf(
            Triple("الفجر", times.fajr, azanSettings.fajrEnabled to azanSettings.fajrSound),
            Triple("الظهر", times.dhuhr, azanSettings.dhuhrEnabled to azanSettings.dhuhrSound),
            Triple("العصر", times.asr, azanSettings.asrEnabled to azanSettings.asrSound),
            Triple("المغرب", times.maghrib, azanSettings.maghribEnabled to azanSettings.maghribSound),
            Triple("العشاء", times.isha, azanSettings.ishaEnabled to azanSettings.ishaSound)
        )

        prayers.forEachIndexed { index, (name, time, settings) ->
            val (enabled, sound) = settings
            if (enabled && time > System.currentTimeMillis()) {
                scheduleAlarm(context, index, name, time, sound)
            }
        }
    }

    private fun scheduleAlarm(
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMs, pendingIntent)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMs, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeMs, pendingIntent)
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

    fun scheduleRamadanAlarm(context: Context, suhoorTime: Long, iftarTime: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Suhoor alarm (30 min before fajr)
        if (suhoorTime > System.currentTimeMillis()) {
            val suhoorIntent = Intent(context, com.yousefalaa.electronicmuezzin.receivers.RamadanAlarmReceiver::class.java).apply {
                putExtra("type", "suhoor")
            }
            val suhoorPending = PendingIntent.getBroadcast(
                context, 100, suhoorIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, suhoorTime, suhoorPending)
        }

        // Iftar alarm (maghrib)
        if (iftarTime > System.currentTimeMillis()) {
            val iftarIntent = Intent(context, com.yousefalaa.electronicmuezzin.receivers.RamadanAlarmReceiver::class.java).apply {
                putExtra("type", "iftar")
            }
            val iftarPending = PendingIntent.getBroadcast(
                context, 101, iftarIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, iftarTime, iftarPending)
        }
    }
}

// Formatting utilities
object TimeFormatter {
    fun formatTime(timeMs: Long): String {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = timeMs
        val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
        val min = cal.get(java.util.Calendar.MINUTE)
        val amPm = if (hour < 12) "ص" else "م"
        val h = if (hour > 12) hour - 12 else if (hour == 0) 12 else hour
        return "${h}:${min.toString().padStart(2, '0')} $amPm"
    }

    fun formatCountdown(diffMs: Long): String {
        val totalSecs = diffMs / 1000
        val hours = totalSecs / 3600
        val minutes = (totalSecs % 3600) / 60
        val secs = totalSecs % 60
        return if (hours > 0) {
            "${hours}س ${minutes}د ${secs}ث"
        } else {
            "${minutes}د ${secs}ث"
        }
    }

    fun getDayName(): String {
        val days = listOf("الأحد", "الاثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت")
        return days[java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK) - 1]
    }

    fun getMonthName(month: Int): String {
        val months = listOf(
            "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
            "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
        )
        return months[(month - 1).coerceIn(0, 11)]
    }
}
