package com.nooblabs.thermalconfigchanger.activities

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.nooblabs.thermalconfigchanger.R
import com.nooblabs.thermalconfigchanger.ThermalMode
import com.nooblabs.thermalconfigchanger.adapters.ApplicationAdapter
import com.nooblabs.thermalconfigchanger.extensions.*
import com.nooblabs.thermalconfigchanger.services.profiler.ProfilerService
import kotlinx.android.synthetic.main.activity_profile_chooser.*

class ProfileChooserActivity : AppCompatActivity() {

    private lateinit var installedApplications: List<PackageInfo>

    private lateinit var applicationAdapter: ApplicationAdapter

    private lateinit var nm: NotificationManager

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            invalidateOptionsMenu()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_chooser)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        applicationAdapter = ApplicationAdapter()
        applicationAdapter.modeChangeListener = object : ApplicationAdapter.OnModeChangeListener {
            override fun onModeChange(packageName: String, newMode: ThermalMode) {
                putSharedPreference(packageName, newMode.value)
            }
        }

        rv_app_list.layoutManager = LinearLayoutManager(this)
        rv_app_list.adapter = applicationAdapter
    }

    override fun onResume() {
        super.onResume()
        invalidateOptionsMenu()
        init()
        registerReceiver(broadcastReceiver, IntentFilter("profiler_state_changed"))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_chooser_options, menu)
        menu?.findItem(R.id.enable_profiler)?.let {
            it.isChecked = getSharedPreference(IS_PROFILER_ENABLED, false) as Boolean
        }
        menu?.findItem(R.id.enable_mode_notifcation)?.let {
            it.isChecked = getSharedPreference(SHOW_MODE_NOTIFICATION, false) as Boolean
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) =
        when (item?.itemId) {
            R.id.enable_profiler -> {
                item.isChecked = !item.isChecked
                if (item.isChecked) {
                    putSharedPreference(IS_PROFILER_ENABLED, true)
                    enqueueWork<ProfilerService>(this, 2)
                } else {
                    putSharedPreference(IS_PROFILER_ENABLED, false)
                }
                true
            }
            R.id.enable_mode_notifcation -> {
                item.isChecked = !item.isChecked
                if (item.isChecked) {
                    putSharedPreference(SHOW_MODE_NOTIFICATION, true)
                } else {
                    putSharedPreference(SHOW_MODE_NOTIFICATION, false)
                    nm.cancel(5)
                }
                true
            }
            R.id.open_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    private fun init() {
        if (checkForUsagePermission()) {
            btn_grant_usage_permission.visibility = View.GONE
            rv_app_list.visibility = View.VISIBLE

            installedApplications = getInstalledApplications()

            val list = ArrayList<Pair<PackageInfo, ThermalMode>>()

            installedApplications.forEach {
                val mode = getSharedPreference(it.packageName, ThermalMode.DEFAULT.value) as Int
                list.add(Pair(it, ThermalMode.values().first { mode == it.value }))
                log(list[list.size - 1].toString())
            }

            applicationAdapter.list = list

        } else {
            //ask for user permission
            btn_grant_usage_permission.visibility = View.VISIBLE
            rv_app_list.visibility = View.GONE
            btn_grant_usage_permission.setOnClickListener {
                openUsageSettings()
            }
        }
    }
}
