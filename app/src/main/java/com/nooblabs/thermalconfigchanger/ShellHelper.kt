package com.nooblabs.thermalconfigchanger

import android.util.Log
import com.topjohnwu.superuser.Shell

object ShellHelper {

    private const val filePath = "/sys/class/thermal/thermal_message/sconfig"

    fun updateSConfig(newValue: Int): Shell.Result {
        val result = Shell.su("cat $filePath").exec()
        if(result.isSuccess) {
            return Shell.su("echo '$newValue' > $filePath").exec()
        } else {
            Log.d("DRAG", "failed")
            return result
        }
    }

    fun restoreDefault(): Shell.Result {
        return Shell.su("-1 > $filePath").exec()
    }

    fun getCurrent(): Int {
        return Shell.su("cat $filePath").exec().out[0].toInt()
    }
}