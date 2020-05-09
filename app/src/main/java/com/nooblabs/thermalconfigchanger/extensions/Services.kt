package com.nooblabs.thermalconfigchanger.extensions

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService

inline fun <reified T : JobIntentService> enqueueWork(context: Context, jobId: Int) {
    val intent = Intent(context, T::class.java)
    JobIntentService.enqueueWork(
        context, ComponentName(context.packageName, T::class.java.name),
        jobId, intent
    )
}