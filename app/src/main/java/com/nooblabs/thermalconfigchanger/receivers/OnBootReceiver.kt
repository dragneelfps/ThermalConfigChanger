package com.nooblabs.thermalconfigchanger.receivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.widget.Toast
import com.nooblabs.thermalconfigchanger.extensions.currentActualMode
import com.nooblabs.thermalconfigchanger.extensions.currentSetMode
import com.nooblabs.thermalconfigchanger.extensions.log

class OnBootReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        val persistenceMode = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean("persistence_mode", false)
        if (persistenceMode) {
            currentActualMode = context.currentSetMode
            Toast.makeText(context, "Set mode to $currentActualMode", Toast.LENGTH_SHORT).show()
            log("Set mode to $currentActualMode")
        }
    }
}