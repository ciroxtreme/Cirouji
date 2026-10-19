package com.ciro.uji

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val message = intent?.getStringExtra("EXTRA_MESSAGE") ?: "PENGINGAT UJI COBA CIRO!"

        // 1. Dapatkan WakeLock agar CPU HP tetap bangun selama 15 detik
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
            "Cirouji:AlarmWakeLock"
        )
        wakeLock.acquire(15000L)

        // 2. Buat NotificationChannel dengan Prioritas IMPORTANCE_HIGH + Nada Dering Alarm
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "ciro_uji_urgent_channel"
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val channel = NotificationChannel(
                channelId,
                "Alarm Uji Prioritas Tinggi",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Saluran untuk menembus lockscreen dan memicu layar penuh"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 800)
                setSound(alarmSound, audioAttributes)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setBypassDnd(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 3. FullScreen Intent untuk memicu AlarmActivity langsung ke layar
        val fullScreenIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("EXTRA_MESSAGE", message)
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val fullScreenPending = PendingIntent.getActivity(context, 2001, fullScreenIntent, flags)

        // 4. Bangun Notifikasi Layar Penuh
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("🚨 PENGINGAT CIRO UJI!")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSound(alarmSound)
            .setVibrate(longArrayOf(0, 500, 200, 500, 200, 800))
            .setFullScreenIntent(fullScreenPending, true)
            .setContentIntent(fullScreenPending)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(777, notification)

        // 5. Coba buka Activity secara langsung sebagai lapis ganda
        try {
            context.startActivity(fullScreenIntent)
        } catch (_: Exception) {
            // FullScreenIntent di notifikasi akan menangani jika background start terhalang
        }
    }
}
