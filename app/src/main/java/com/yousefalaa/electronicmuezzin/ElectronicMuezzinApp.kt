package com.yousefalaa.electronicmuezzin

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ElectronicMuezzinApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            // Azan channel
            val azanChannel = NotificationChannel(
                CHANNEL_AZAN,
                "أذان الصلاة",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "تنبيه أذان الصلاة"
                enableVibration(true)
                setShowBadge(true)
            }

            // Adhkar channel
            val adhkarChannel = NotificationChannel(
                CHANNEL_ADHKAR,
                "الأذكار",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "تذكير الأذكار اليومية"
            }

            // Ramadan channel
            val ramadanChannel = NotificationChannel(
                CHANNEL_RAMADAN,
                "رمضان",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "تنبيه الإفطار والسحور"
                enableVibration(true)
            }

            // Quran channel
            val quranChannel = NotificationChannel(
                CHANNEL_QURAN,
                "ختم القرآن",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "تذكير ختم القرآن الكريم"
            }

            manager.createNotificationChannels(
                listOf(azanChannel, adhkarChannel, ramadanChannel, quranChannel)
            )
        }
    }

    companion object {
        const val CHANNEL_AZAN = "channel_azan"
        const val CHANNEL_ADHKAR = "channel_adhkar"
        const val CHANNEL_RAMADAN = "channel_ramadan"
        const val CHANNEL_QURAN = "channel_quran"
    }
}
