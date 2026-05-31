package com.yousefalaa.electronicmuezzin.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.yousefalaa.electronicmuezzin.ElectronicMuezzinApp.Companion.CHANNEL_AZAN
import com.yousefalaa.electronicmuezzin.MainActivity
import com.yousefalaa.electronicmuezzin.R
import com.yousefalaa.electronicmuezzin.ui.screens.AzanFullScreenActivity

class AzanService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var audioManager: AudioManager? = null
    private var audioFocusRequest: AudioFocusRequest? = null
    private var wakeLock: PowerManager.WakeLock? = null

    companion object {
        const val EXTRA_PRAYER_NAME = "prayer_name"
        const val EXTRA_AZAN_SOUND = "azan_sound"
        const val ACTION_STOP_AZAN = "com.yousefalaa.electronicmuezzin.STOP_AZAN"
        const val NOTIF_ID = 1001

        fun start(context: Context, prayerName: String, azanSound: String) {
            val intent = Intent(context, AzanService::class.java).apply {
                putExtra(EXTRA_PRAYER_NAME, prayerName)
                putExtra(EXTRA_AZAN_SOUND, azanSound)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, AzanService::class.java).apply {
                action = ACTION_STOP_AZAN
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "ElectronicMuezzin::AzanWakeLock"
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_AZAN) {
            stopAzan()
            return START_NOT_STICKY
        }

        val prayerName = intent?.getStringExtra(EXTRA_PRAYER_NAME) ?: "الصلاة"
        val azanSound = intent?.getStringExtra(EXTRA_AZAN_SOUND) ?: "azan_makkah"

        startForeground(NOTIF_ID, buildNotification(prayerName))
        wakeLock?.acquire(10 * 60 * 1000L)

        requestAudioFocus {
            playAzan(azanSound, prayerName)
        }

        // Launch full screen activity
        val fullScreenIntent = Intent(this, AzanFullScreenActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_PRAYER_NAME, prayerName)
            putExtra(EXTRA_AZAN_SOUND, azanSound)
        }
        startActivity(fullScreenIntent)

        return START_NOT_STICKY
    }

    private fun requestAudioFocus(onGranted: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttr = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            val req = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                .setAudioAttributes(audioAttr)
                .setOnAudioFocusChangeListener { }
                .build()
            audioFocusRequest = req
            val result = audioManager?.requestAudioFocus(req)
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) onGranted()
        } else {
            @Suppress("DEPRECATION")
            val result = audioManager?.requestAudioFocus(
                null,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
            )
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) onGranted()
        }
    }

    private fun playAzan(soundName: String, prayerName: String) {
        try {
            val resId = getAzanResId(soundName)
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                val afd = resources.openRawResourceFd(resId)
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                setOnCompletionListener { stopAzan() }
                setOnErrorListener { _, _, _ -> stopAzan(); true }
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            stopAzan()
        }
    }

    private fun getAzanResId(soundName: String): Int = when (soundName) {
        "azan_fajr" -> R.raw.azan_fajr
        "azan_madinah" -> R.raw.azan_madinah
        "azan_egypt" -> R.raw.azan_egypt
        "azan_short" -> R.raw.azan_short
        else -> R.raw.azan_makkah
    }

    private fun stopAzan() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager?.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager?.abandonAudioFocus(null)
        }

        wakeLock?.takeIf { it.isHeld }?.release()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun buildNotification(prayerName: String): Notification {
        val stopIntent = Intent(this, AzanService::class.java).apply {
            action = ACTION_STOP_AZAN
        }
        val stopPending = PendingIntent.getService(
            this, 0, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val mainIntent = Intent(this, MainActivity::class.java)
        val mainPending = PendingIntent.getActivity(
            this, 0, mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_AZAN)
            .setContentTitle("حان وقت $prayerName")
            .setContentText("اضغط لإيقاف الأذان")
            .setSmallIcon(R.drawable.ic_mosque)
            .setContentIntent(mainPending)
            .addAction(R.drawable.ic_stop, "إيقاف", stopPending)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        stopAzan()
        super.onDestroy()
    }
}
