package com.nooblabs.thermalconfigchanger.services

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.support.v4.app.JobIntentService
import android.support.v4.app.NotificationCompat
import com.nooblabs.thermalconfigchanger.R
import com.nooblabs.thermalconfigchanger.ThermalMode
import com.nooblabs.thermalconfigchanger.extensions.*

class MainService: JobIntentService() {

    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var mainHandler: Handler

    override fun onHandleWork(intent: Intent) {
        log("started main service")
        mainHandler = Handler(Looper.getMainLooper())
        notificationBuilder = createNotificationBuilder()
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        startForeground(MAIN_NOTIFICATION_ID, buildNotification())
        while (getSharedPreference(IS_SERVICE_ENABLED, false) as Boolean) {
            val isForceSetEnabled = getSharedPreference(IS_FORCE_SET_ENABLE, false) as Boolean
            val shouldShowToastOnSet = getSharedPreference(IS_TOAST_ENABLED, false) as Boolean
            if(isForceSetEnabled) {
                val currentSetMode = currentSetMode
                val currentActualMode2 = currentActualMode
                if (currentActualMode2 != currentSetMode) {
                    log("$SERVICE_NAME Setting user mode: $currentSetMode")
                    setActualModeAndNotify(currentSetMode, shouldShowToastOnSet)
                    sendBroadcast(Intent().apply {
                        action = FORCE_SET_ACTION
                        putExtra(FORCE_SET_PAYLOAD, currentSetMode.name)
                    })
                }
            }
            val isProfilerEnabled = getSharedPreference(IS_PROFILER_ENABLED, false) as Boolean
            if(isProfilerEnabled) {
                val foregroundPackage = getForegroundPackage()
                if (foregroundPackage != null) {
                    log("Foreground package detected: $foregroundPackage")
                    val shouldBeMode =
                            getSharedPreference(foregroundPackage, ThermalMode.DEFAULT.value) as Int?
                    if (shouldBeMode != null) {
                        val currentActualMode2 = currentActualMode
                        if (currentActualMode2 == null || currentActualMode2.value != shouldBeMode) {
                            log("different actual mode detected. changing back to user set mode")
                            setActualModeAndNotify(
                                    ThermalMode.values().first { shouldBeMode == it.value }, shouldShowToastOnSet)
                        }
                    } else {
                        log("No mode set for $foregroundPackage")
                    }
                }
            }
            nm.notify(MAIN_NOTIFICATION_ID, buildNotification())
            Thread.sleep(SLEEP_TIMER)
        }
        stopForeground(true)
    }

    private fun setActualModeAndNotify(currentSetMode: ThermalMode, shouldShowToast: Boolean) {
        currentActualMode = currentSetMode
        if(shouldShowToast) {
            mainHandler.post { toast(currentSetMode.name) }
        }
    }

    private fun buildNotification(): Notification {
        var contentText = DEFAULT_CONTENT_TEXT
        var tickerText: String? = null
        if(getSharedPreference(SHOW_MODE_NOTIFICATION, false) as Boolean) {
            contentText = "Current Mode: $currentActualMode"
            tickerText = "$currentActualMode"
        }
        return notificationBuilder.setContentText(contentText)
                .setTicker(tickerText)
                .build()
    }

    private fun createNotificationBuilder(): NotificationCompat.Builder {
        createNotificationChannel()
        return NotificationCompat.Builder(this, CHANNEL_ID)
                .setOngoing(true)
                .setContentTitle("Thermal Config Changer")
//                .setContentText("service started. Click on this to stop")
                .setSmallIcon(R.mipmap.ic_launcher)
    }

    companion object {
        private const val SERVICE_ID = 345
        const val SERVICE_NAME = "MainService"
        const val MAIN_NOTIFICATION_ID = 123
        const val DEFAULT_CONTENT_TEXT= "Service started."
        fun startService(context: Context) {
            log("starting MainService")
            enqueueWork<MainService>(context, SERVICE_ID)
        }
    }
}
