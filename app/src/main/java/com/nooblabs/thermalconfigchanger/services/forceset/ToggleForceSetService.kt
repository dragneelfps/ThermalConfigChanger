package com.nooblabs.thermalconfigchanger.services.forceset

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nooblabs.thermalconfigchanger.extensions.toggleForceSetService

class ToggleForceSetService : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val newState = intent.getBooleanExtra(ARGUMENT_IS_FORCE_SET, false)
        context.toggleForceSetService(newState)
        context.sendBroadcast(Intent().apply {
            action = "force_set_state_changed"
        })
    }

    companion object {
        const val ARGUMENT_IS_FORCE_SET = "is_force_set"
        const val SERVICE_NAME = "toggle_force_set"
    }
}