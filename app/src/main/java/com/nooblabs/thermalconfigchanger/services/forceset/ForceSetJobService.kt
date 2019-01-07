package com.nooblabs.thermalconfigchanger.services.forceset

import android.app.Notification
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.FileObserver
import android.os.Handler
import android.support.v4.app.NotificationCompat
import com.nooblabs.thermalconfigchanger.R
import com.nooblabs.thermalconfigchanger.extensions.CHANNEL_ID
import com.nooblabs.thermalconfigchanger.extensions.currentActualMode
import com.nooblabs.thermalconfigchanger.extensions.currentSetMode
import com.nooblabs.thermalconfigchanger.extensions.log
import com.nooblabs.thermalconfigchanger.helpers.filePath

class ForceSetJobService : JobService() {
    private val fileObserver = object : FileObserver(filePath) {
        override fun onEvent(event: Int, path: String?) {
            log("$filePath event occurred: $event")

            val currentSetMode = currentSetMode
            val currentActualMode2 = currentActualMode
            if (currentActualMode2 != currentSetMode) {
                log("ForceSetJobService Setting user mode: $currentSetMode")
                currentActualMode = currentSetMode
                sendBroadcast(Intent().apply {
                    action = "force_set"
                    putExtra("force_set_mode", currentSetMode)
                })
            }
        }
    }
    val handler = Handler { msg ->
        fileObserver.startWatching()
        jobFinished(msg.obj as JobParameters?, false)
        true

    }

    override fun onStartJob(params: JobParameters?): Boolean {
        log("Job started")
//        startForeground(FOREGROUND_ID, buildForegroundNotification())
        fileObserver.startWatching()
//        while (getSharedPreference(IS_FORCE_SET_ENABLE, false) as Boolean) {
//            Thread.sleep(5000)
//        }
//        fileObserver.stopWatching()
//        stopForeground(true)
//        jobFinished(params, false)
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        log("Job stopped")
        fileObserver.stopWatching()
        return false
    }


    private fun buildForegroundNotification(): Notification {
        val pendingIntent = PendingIntent.getService(
            this, 0,
            Intent(this, ToggleForceSetService::class.java).apply {
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
        private const val FOREGROUND_ID = 6193
        private const val SERVICE_NAME = "force_set"
    }
}