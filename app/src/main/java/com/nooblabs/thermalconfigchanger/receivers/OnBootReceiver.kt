package com.nooblabs.thermalconfigchanger.receivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nooblabs.thermalconfigchanger.extensions.APPLY_ON_BOOT_ENABLED
import com.nooblabs.thermalconfigchanger.extensions.IS_SERVICE_ENABLED
import com.nooblabs.thermalconfigchanger.extensions.getSharedPreference
import com.nooblabs.thermalconfigchanger.extensions.log
import com.nooblabs.thermalconfigchanger.services.MainService

class OnBootReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        log("starting on boot receiver")
        context ?: return
        val persistenceMode = context.getSharedPreference(APPLY_ON_BOOT_ENABLED, false) as Boolean
        if (persistenceMode) {
//            currentActualMode = context.currentSetMode
//            Toast.makeText(context, "Set mode to $currentActualMode", Toast.LENGTH_SHORT).show()
//            log("Set mode to $currentActualMode")
            val isServiceEnabled = context.getSharedPreference(IS_SERVICE_ENABLED, false) as Boolean
            if(isServiceEnabled) {
                MainService.startService(context)
            }
        }
    }
}