package com.ciro.uji

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock

object AlarmHelper {

    fun setTestAlarm(context: Context, delaySeconds: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val triggerTime = System.currentTimeMillis() + (delaySeconds * 1000L)

        // 1. PendingIntent untuk BroadcastReceiver
        val receiverIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.ciro.uji.ACTION_ALARM_TRIGGER"
            putExtra("EXTRA_MESSAGE", "ALARM BERHASIL MENEMBUS SISTEM!")
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val broadcastPending = PendingIntent.getBroadcast(context, 1001, receiverIntent, flags)

        // 2. PendingIntent untuk showIntent (AlarmClockInfo)
        val showIntent = Intent(context, AlarmActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val showPending = PendingIntent.getActivity(context, 1002, showIntent, flags)

        // 3. Gunakan AlarmClockInfo (Prioritas Tertinggi di Android, kebal Doze Mode & Hibernation)
        val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerTime, showPending)
        alarmManager.setAlarmClock(alarmClockInfo, broadcastPending)
    }

    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val receiverIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.ciro.uji.ACTION_ALARM_TRIGGER"
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val broadcastPending = PendingIntent.getBroadcast(context, 1001, receiverIntent, flags)
        alarmManager.cancel(broadcastPending)
    }
}
