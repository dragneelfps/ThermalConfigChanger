package com.nooblabs.thermalconfigchanger.services.profiler

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.JobIntentService
import android.support.v4.app.NotificationCompat
import com.nooblabs.thermalconfigchanger.R
import com.nooblabs.thermalconfigchanger.ThermalMode
import com.nooblabs.thermalconfigchanger.extensions.*

class ProfilerService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        log("$SERVICE_NAME started")
        startForeground(FOREGROUND_ID, buildForegroundNotification())

        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        while (getSharedPreference(IS_PROFILER_ENABLED, false) as Boolean) {

            val foregroundPackage = getForegroundPackage()
            if (foregroundPackage != null) {
                log("Foreground package detected: $foregroundPackage")
                val shouldBeMode =
                    getSharedPreference(foregroundPackage, ThermalMode.DEFAULT.value) as Int?
                if (shouldBeMode != null) {
                    val currentActualMode2 = currentActualMode
                    if (currentActualMode2 == null || currentActualMode2.value != shouldBeMode) {
                        log("different actual mode detected. changing back to user set mode")
                        currentActualMode = ThermalMode.values().first { shouldBeMode == it.value }
                    }
                } else {
                    log("No mode set for $foregroundPackage")
                }
            }
            if (getSharedPreference(SHOW_MODE_NOTIFICATION, false) as Boolean) {
                nm.notify(5, buildCurrentModeNotification())
            }
            Thread.sleep(2000)
        }
        nm.cancel(5)
        stopForeground(true)
        log("$SERVICE_NAME stopped")
    }

    private fun buildForegroundNotification(): Notification {
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0,
            Intent(this, ToggleProfilerService::class.java).apply {
                action = ToggleProfilerService.SERVICE_NAME
                putExtra(ToggleProfilerService.ARGUMENT_IS_PROFILER_ENABLE, false)
            }, 0
        )
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        return builder.setOngoing(true)
            .setContentTitle("Thermal Config Changer")
            .setContentText("Profiler started. Click on this to stop")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun buildCurrentModeNotification(): Notification {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        return builder.setOngoing(true)
            .setContentText("Current Mode $currentActualMode")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOnlyAlertOnce(true)
            .setTicker("$currentActualMode")
            .build()
    }

    companion object {
        const val SERVICE_NAME = "profiler"
        const val FOREGROUND_ID = 124
    }
}