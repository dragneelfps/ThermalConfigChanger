package com.nooblabs.thermalconfigchanger.extensions

import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Process
import android.provider.Settings
import java.util.*

fun Context.checkForUsagePermission(): Boolean {
    val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    return appOps.checkOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(),
        packageName
    ) == AppOpsManager.MODE_ALLOWED
}

fun Context.openUsageSettings() {
    startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
}

fun Context.getForegroundPackage(): String? {
    var packageName: String? = null
//    val interval = 1000 * 6L
//    val end = System.currentTimeMillis()
//    val begin = end - interval
    val usageStatsManager = getSystemService("usagestats") as UsageStatsManager
//    val usageEvents = usageStatsManager.queryEvents(begin, end)
//    while (usageEvents.hasNextEvent()) {
//        val event = UsageEvents.Event().apply {
//            usageEvents.getNextEvent(this)
//        }
//        when (event.eventType) {
//            UsageEvents.Event.MOVE_TO_FOREGROUND ->
//                packageName = event.packageName
//
//            UsageEvents.Event.MOVE_TO_BACKGROUND ->
//                packageName = null
//        }
//    }
    val end = System.currentTimeMillis()
    val start = end - 1000 * 60L
    val stats: List<UsageStats> =
        usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end)
    val runningTask: SortedMap<Long, UsageStats> = TreeMap()
    stats.filter { this.packageName != it.packageName }
        .forEach { stat -> runningTask[stat.lastTimeUsed] = stat }
    if (runningTask.isEmpty()) {
        return null
    }
    return runningTask[runningTask.lastKey()]?.packageName

}

fun Context.getInstalledApplications(showSystemApps: Boolean): List<PackageInfo> {
    val packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
    return packages.filter { this.packageName != it.packageName &&
            (showSystemApps || (it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0)) }
}