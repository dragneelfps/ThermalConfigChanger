package com.nooblabs.thermalconfigchanger.receivers

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.support.v4.app.JobIntentService
import com.nooblabs.thermalconfigchanger.services.forceset.ForceSetService

class ForceSetBootStartReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        val forceSetIntent = Intent(context, ForceSetService::class.java)
        JobIntentService.enqueueWork(
            context, ComponentName(context.packageName, ForceSetService::class.java.name),
            1, forceSetIntent
        )
    }
}