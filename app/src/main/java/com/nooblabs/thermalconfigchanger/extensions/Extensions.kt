package com.nooblabs.thermalconfigchanger.extensions

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.nooblabs.thermalconfigchanger.ThermalMode
import com.nooblabs.thermalconfigchanger.helpers.getCurrentConfig
import com.nooblabs.thermalconfigchanger.helpers.updateSConfig
import com.nooblabs.thermalconfigchanger.services.forceset.ForceSetService

const val CHANNEL_ID: String = "t_c_c123"
private const val PREF_FILE = "prefs"
const val CURRENT_SET_MODE = "current_set_mode"
const val IS_FORCE_SET_ENABLE = "force_set_enable"
const val IS_PROFILER_ENABLED = "profiler_enable"
const val SHOW_MODE_NOTIFICATION = "show_mode_notification"


fun Context.getSharedPreference(key: String, default: Any?): Any? {
    return getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE).all[key] ?: default
}

fun Context.putSharedPreference(key: String, value: Any) {
    getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE).edit().apply {
        when (value) {
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Boolean -> putBoolean(key, value)
        }
        apply()
    }
}

fun log(message: String) {
    Log.d("DRAG", message)
}

fun Context.toggleForceSetService(state: Boolean) {
    putSharedPreference(IS_FORCE_SET_ENABLE, state)
}

fun Context.stopForceSetService() {
    toggleForceSetService(false)
}

fun Context.startForceSetService() {
    toggleForceSetService(true)
    enqueueWork<ForceSetService>(this, 1)
}

var currentActualMode: ThermalMode?
    get() = ThermalMode.values().firstOrNull { it.value == getCurrentConfig() }
    set(value) {
        updateSConfig(value!!.value)
    }

var Context.currentSetMode: ThermalMode
    get() = ThermalMode.values().first {
        it.value == getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
            .getInt(CURRENT_SET_MODE, -1)
    }
    set(value) {
        getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE).edit().apply {
            putInt(CURRENT_SET_MODE, value.value)
            apply()
        }
    }


fun Context.createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Thermal Config Changer"
        val description =
            "Convenient way to the value of /sys/class/thermal/thermal_message/sconfig"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            this.description = description
        }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}