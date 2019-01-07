package com.nooblabs.thermalconfigchanger.services.forceset

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.support.v4.app.JobIntentService
import android.support.v4.app.NotificationCompat
import com.nooblabs.thermalconfigchanger.R
import com.nooblabs.thermalconfigchanger.extensions.*

class ForceSetService : JobIntentService() {
    override fun onHandleWork(intent: Intent) {
        log("$SERVICE_NAME service started")
        startForeground(FOREGROUND_ID, buildForegroundNotification())

        while (getSharedPreference(IS_FORCE_SET_ENABLE, false) as Boolean) {
            val currentSetMode = currentSetMode
            val currentActualMode2 = currentActualMode
            if (currentActualMode2 != currentSetMode) {
                log("$SERVICE_NAME Setting user mode: $currentSetMode")
                currentActualMode = currentSetMode
                sendBroadcast(Intent().apply {
                    action = "force_set"
                    putExtra("force_set_mode", currentSetMode)
                })
            }
            Thread.sleep(5000)
        }

        stopForeground(true)
        log("$SERVICE_NAME service stopped")
    }


    private fun buildForegroundNotification(): Notification {
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0,
            Intent(this, ToggleForceSetService::class.java).apply {
                action = ToggleForceSetService.SERVICE_NAME
                putExtra(ToggleForceSetService.ARGUMENT_IS_FORCE_SET, false)
            }, 0
        )
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        return builder.setOngoing(true)
            .setContentTitle("Thermal Config Changer")
            .setContentText("service started. Click on this to stop")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .build()
    }


    companion object {
        private const val FOREGROUND_ID = 619
        private const val SERVICE_NAME = "force_set"
    }

}
