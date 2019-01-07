package com.nooblabs.thermalconfigchanger.services.profiler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nooblabs.thermalconfigchanger.extensions.IS_PROFILER_ENABLED
import com.nooblabs.thermalconfigchanger.extensions.putSharedPreference

class ToggleProfilerService : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val isEnabled = intent.getBooleanExtra(ARGUMENT_IS_PROFILER_ENABLE, false)
        context.applicationContext.putSharedPreference(IS_PROFILER_ENABLED, isEnabled)
        context.sendBroadcast(Intent().apply {
            action = "profiler_state_changed"
        })
    }

    companion object {
        const val ARGUMENT_IS_PROFILER_ENABLE = "is_profiler_enabled"
        const val SERVICE_NAME = "toggle_profiler"
    }
}
