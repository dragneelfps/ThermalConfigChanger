package com.nooblabs.thermalconfigchanger

import android.content.Context
import android.util.Log
import com.topjohnwu.superuser.ContainerApp
import com.topjohnwu.superuser.Shell

class App: ContainerApp() {
    init {
        Shell.Config.setFlags(Shell.FLAG_REDIRECT_STDERR)
        Shell.Config.verboseLogging(BuildConfig.DEBUG)
        Shell.Config.addInitializers(CustomInitializer::class.java)
    }

    private class CustomInitializer: Shell.Initializer() {
        override fun onInit(context: Context?, shell: Shell): Boolean {
            Log.d("DRAG", "onInit")
            return true
        }
    }
}