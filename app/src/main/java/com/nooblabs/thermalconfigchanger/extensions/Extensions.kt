package com.nooblabs.thermalconfigchanger.extensions

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import com.nooblabs.thermalconfigchanger.ThermalMode
import com.nooblabs.thermalconfigchanger.helpers.getCurrentConfig
import com.nooblabs.thermalconfigchanger.helpers.updateSConfig

const val CHANNEL_ID: String = "t_c_c123"
const val CURRENT_SET_MODE = "current_set_mode"
const val IS_SERVICE_ENABLED = "service_enabled"
const val IS_FORCE_SET_ENABLE = "force_set_enable"
const val IS_TOAST_ENABLED = "toast_enabled"
const val APPLY_ON_BOOT_ENABLED = "apply_on_boot"
const val IS_PROFILER_ENABLED = "profiler_enable"
const val SHOW_MODE_NOTIFICATION = "show_mode_notification"

const val FORCE_SET_ACTION = "com.nooblabs.thermalconfigchanger.force_set"
const val FORCE_SET_PAYLOAD = "force_set_mode"

const val SLEEP_TIMER = 500L

const val XDA_LINK = "https://forum.xda-developers.com/poco-f1/themes/thermal-config-changer-pocophoneroot-t3886603"

fun Context.getSharedPreference(key: String, default: Any?): Any? {
    return PreferenceManager.getDefaultSharedPreferences(this).all[key] ?: default
}

fun Context.putSharedPreference(key: String, value: Any) {
    PreferenceManager.getDefaultSharedPreferences(this).edit().apply {
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

fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.toggleForceSetService(state: Boolean) {
    putSharedPreference(IS_FORCE_SET_ENABLE, state)
}

fun Context.stopForceSetService() {
    toggleForceSetService(false)
}


var currentActualMode: ThermalMode?
    get() = ThermalMode.values().firstOrNull { it.value == getCurrentConfig() }
    set(value) {
        updateSConfig(value!!.value)
    }

var Context.currentSetMode: ThermalMode
    get() = ThermalMode.values().first {
        it.value == getSharedPreference(CURRENT_SET_MODE, -1) as Int
    }
    set(value) {
        putSharedPreference(CURRENT_SET_MODE, value.value)
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